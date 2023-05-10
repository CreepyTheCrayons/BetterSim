package dev.kyro.pitsim.misc.effects;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.enums.PitEntityType;
import dev.kyro.pitsim.misc.Misc;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftItem;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SelectiveDrop implements Listener {
	public static List<SelectiveDrop> selectiveDrops = new ArrayList<>();

	private final ItemStack itemStack;
	private final Location location;
	private final List<UUID> permittedPlayers;

	private Item droppedItem;
	private long lifetime;
	private final UUID identifier;

	private final BukkitTask task;

	public SelectiveDrop(ItemStack itemStack, Location location) {
		this(itemStack, location, new ArrayList<>());
	}

	public SelectiveDrop(ItemStack itemStack, Location location,  UUID permittedPlayer) {
		this(itemStack, location, Collections.singletonList(permittedPlayer));
	}

	public SelectiveDrop(ItemStack itemStack, Location location, List<UUID> permittedPlayers) {
		this.itemStack = itemStack;
		this.location = location;
		this.permittedPlayers = permittedPlayers;
		this.lifetime = 6000;

		identifier = UUID.randomUUID();

		task = new BukkitRunnable() {
			long ticksLived = 0;

			@Override
			public void run() {
				ticksLived += 10;
				if(ticksLived >= lifetime) {
					cancel();
					removeItem();
				}
			}
		}.runTaskLater(PitSim.INSTANCE, 10);

		Bukkit.getPluginManager().registerEvents(this, PitSim.INSTANCE);
		selectiveDrops.add(this);
	}

	public void dropItem() {
		NBTItem nbtItem = new NBTItem(itemStack, true);
		nbtItem.setString(NBTTag.DROPPED_ITEM_UUID.toString(), identifier.toString());
		droppedItem = location.getWorld().dropItemNaturally(location, itemStack);
	}

	public void removeItem() {
		if(droppedItem != null) droppedItem.remove();
	}

	public void setLifetime(long lifetime) {
		this.lifetime = lifetime;
	}

	public SelectiveDrop addPlayer(Player player) {
		if(!permittedPlayers.contains(player.getUniqueId())) permittedPlayers.add(player.getUniqueId());
		if(droppedItem == null) return this;

		Entity entity = ((CraftItem) droppedItem).getHandle();
		EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

		PacketPlayOutSpawnEntity packet = new PacketPlayOutSpawnEntity(entity, 2);
		nmsPlayer.playerConnection.sendPacket(packet);

		PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(entity.getId(), entity.getDataWatcher(), true);
		nmsPlayer.playerConnection.sendPacket(metadataPacket);

		return this;
	}

	public SelectiveDrop removePlayer(Player player) {
		permittedPlayers.remove(player.getUniqueId());
		if(droppedItem == null) return this;

		Entity entity = ((CraftItem) droppedItem).getHandle();
		EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

		PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entity.getId());
		nmsPlayer.playerConnection.sendPacket(packet);

		return this;
	}

	private void cleanUp() {
		task.cancel();
		removeItem();
		HandlerList.unregisterAll(this);

		selectiveDrops.remove(this);
	}

	@EventHandler
	public void onPickUp(PlayerPickupItemEvent event) {
		if(!event.getItem().getUniqueId().equals(droppedItem.getUniqueId())) return;

		if(!Misc.isEntity(event.getPlayer(), PitEntityType.REAL_PLAYER)) {
			event.setCancelled(true);
			return;
		}

		UUID uuid = event.getPlayer().getUniqueId();
		if(!permittedPlayers.contains(uuid)) {
			event.setCancelled(true);
			return;
		}

		NBTItem nbtItem = new NBTItem(event.getItem().getItemStack(), true);
		nbtItem.removeKey(NBTTag.DROPPED_ITEM_UUID.toString());

		cleanUp();
	}

	public Item getDroppedItem() {
		return droppedItem;
	}

	public List<UUID> getPermittedPlayers() {
		return permittedPlayers;
	}

	public boolean isItem(Item item) {
		ItemStack itemStack = item.getItemStack();
		NBTItem nbtItem = new NBTItem(itemStack);
		String identifier = nbtItem.getString(NBTTag.DROPPED_ITEM_UUID.toString());
		if(identifier == null) return false;
		return UUID.fromString(identifier).equals(this.identifier);
	}
}