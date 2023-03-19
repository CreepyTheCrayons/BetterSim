package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.inventories.BidGUI;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AuctionDisplays implements Listener {

	public static Location[] pedestalLocations = new Location[3];
	public static UUID[] pedestalItems = new UUID[3];
	public static UUID[] pedestalArmorStands = new UUID[3];

	public static UUID[] highestBidStands = new UUID[3];
	public static UUID[] highestBidderStands = new UUID[3];
	public static UUID[] rightClickStands = new UUID[3];

	public static UUID timerStandUUID;

	public static NPC[] clickables = new NPC[3];

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				if(!PitSim.getStatus().isDarkzone()) return;

				if(!hasPlayers(pedestalLocations[0])) return;

				for(int i = 0; i < 3; i++) {

					Item item = getItem(pedestalItems[i]);
					if(item != null) item.teleport(pedestalLocations[i]);

					for(Entity nearbyEntity : MapManager.getDarkzone().getNearbyEntities(pedestalLocations[i], 1, 1, 1)) {
						if(!(nearbyEntity instanceof Item)) continue;
						if(nearbyEntity.getUniqueId().equals(pedestalItems[i])) continue;
						nearbyEntity.remove();
					}

					int highestBid = AuctionManager.auctionItems[i].getHighestBid();
					UUID highestBidder = AuctionManager.auctionItems[i].getHighestBidder();

					ArmorStand highestBidStand = getStand(highestBidStands[i]);
					if(highestBidder != null)
						highestBidStand.setCustomName(ChatColor.YELLOW + "Highest Bid: " + ChatColor.WHITE + highestBid + " Tainted Souls");
					else
						highestBidStand.setCustomName(ChatColor.YELLOW + "Starting Bid: " + ChatColor.WHITE + highestBid + " Tainted Souls");

					String message = highestBidder == null ? "No One!" : Bukkit.getOfflinePlayer(highestBidder).getName();
					ArmorStand highestBidderStand = getStand(highestBidderStands[i]);
					highestBidderStand.setCustomName(ChatColor.YELLOW + "By: " + ChatColor.GOLD + message);

				}

				for(int i = 0; i < clickables.length; i++) {
					NPC clickable = clickables[i];

					clickable.spawn(pedestalLocations[i]);
					clickable.teleport(pedestalLocations[i], PlayerTeleportEvent.TeleportCause.UNKNOWN);
					if(clickable.isSpawned())
						((LivingEntity) clickable.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
				}

				if(AuctionManager.haveAuctionsEnded()) {
					getStand(timerStandUUID).setCustomName(ChatColor.YELLOW + "Ending Soon");
				} else
					getStand(timerStandUUID).setCustomName(ChatColor.YELLOW + "Time Left: " + ChatColor.WHITE + AuctionManager.getRemainingTime());
			}
		}.runTaskTimer(PitSim.INSTANCE, 20, 20);
	}

	public static void onStart() {
		ArmorStand timerStand = (ArmorStand) MapManager.getDarkzone().spawnEntity(new Location(MapManager.getDarkzone(), 178.5, 50, -1011.5), EntityType.ARMOR_STAND);
		timerStand.setGravity(false);
		timerStand.setVisible(false);
		timerStand.setCustomNameVisible(true);
//        timerStand.setRemoveWhenFarAway(false);
		timerStand.setCustomName(ChatColor.YELLOW + "Time Left: " + ChatColor.WHITE + "0m");
		timerStandUUID = timerStand.getUniqueId();

		for(int i = 0; i < 3; i++) {

			NPCRegistry registry = CitizensAPI.getNPCRegistry();
			NPC npc = registry.createNPC(EntityType.MAGMA_CUBE, "");
			npc.spawn(pedestalLocations[i]);
			clickables[i] = npc;

			ArmorStand highestBidStand = (ArmorStand) MapManager.getDarkzone().spawnEntity(pedestalLocations[i].clone().add(0, 0.6, 0), EntityType.ARMOR_STAND);
			highestBidStand.setVisible(false);
			highestBidStand.setCustomNameVisible(true);
			highestBidStand.setGravity(false);
//            highestBidStand.setRemoveWhenFarAway(false);

			highestBidStand.setCustomName(ChatColor.YELLOW + "Highest Bid: " + ChatColor.WHITE + " Tainted Souls");
			highestBidStands[i] = highestBidStand.getUniqueId();

			ArmorStand highestBidderStand = (ArmorStand) MapManager.getDarkzone().spawnEntity(pedestalLocations[i].clone().add(0, 0.3, 0), EntityType.ARMOR_STAND);
			highestBidderStand.setVisible(false);
			highestBidderStand.setCustomNameVisible(true);
			highestBidderStand.setGravity(false);
//            highestBidderStand.setRemoveWhenFarAway(false);

			highestBidderStand.setCustomName(ChatColor.YELLOW + "By: " + ChatColor.GOLD);
			highestBidderStands[i] = highestBidderStand.getUniqueId();

			ArmorStand rightClickStand = (ArmorStand) MapManager.getDarkzone().spawnEntity(pedestalLocations[i].clone().add(0, 0, 0), EntityType.ARMOR_STAND);
			rightClickStand.setVisible(false);
			rightClickStand.setCustomNameVisible(true);
			rightClickStand.setGravity(false);
			rightClickStand.setCustomName(ChatColor.YELLOW + "Right-Click to Bid!");
//            rightClickStand.setRemoveWhenFarAway(false);
			rightClickStands[i] = rightClickStand.getUniqueId();
		}
	}

	public static void showItems() {
		pedestalLocations[0] = new Location(MapManager.getDarkzone(), 172.5, 52, -1013.5);
		pedestalLocations[1] = new Location(MapManager.getDarkzone(), 178.5, 52, -1017.5);
		pedestalLocations[2] = new Location(MapManager.getDarkzone(), 184.5, 52, -1013.5);
		for(Location pedestalLocation : pedestalLocations) {
			pedestalLocation.getChunk().load();
		}

		for(int i = 0; i < pedestalLocations.length; i++) {
			Location pedestalLocation = pedestalLocations[i];
			ItemStack dropItem = AuctionManager.auctionItems[i].item.item.clone();
			ItemMeta meta = dropItem.getItemMeta();
			meta.setDisplayName(UUID.randomUUID().toString());
			dropItem.setItemMeta(meta);

			pedestalItems[i] = pedestalLocation.getWorld().dropItem(pedestalLocation, dropItem).getUniqueId();

			ArmorStand stand = (ArmorStand) pedestalLocation.getWorld().spawnEntity(pedestalLocation.clone().subtract(0, 1.33, 0), EntityType.ARMOR_STAND);
			stand.setVisible(false);
			stand.setGravity(false);
			stand.setHelmet(new ItemStack(Material.GLASS));
			stand.setCustomName(AuctionManager.auctionItems[i].item.itemName);
			stand.setRemoveWhenFarAway(false);
			stand.setCustomNameVisible(true);
			pedestalArmorStands[i] = stand.getUniqueId();

		}
	}

	@EventHandler
	public void onUnload(ChunkUnloadEvent event) {
		if(event.getChunk().getWorld() != MapManager.getDarkzone()) return;
		for(Location pedestalLocation : pedestalLocations) {
			if(pedestalLocation.getChunk().equals(event.getChunk())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onRightClick(NPCRightClickEvent event) {
		for(int i = 0; i < clickables.length; i++) {
			NPC clickable = clickables[i];

			if(clickable.getId() == event.getNPC().getId()) {
				BidGUI bidGUI = new BidGUI(event.getClicker(), i);
				bidGUI.open();
			}
		}
	}

	@EventHandler
	public void onPickUp(PlayerPickupItemEvent event) {
		if(event.getPlayer().getWorld() != MapManager.getDarkzone()) return;

		if(pedestalLocations[0] == null) return;

		if(pedestalLocations[0].distance(event.getPlayer().getLocation()) < 50) {
			event.setCancelled(true);
		}

		for(UUID pedestalItem : pedestalItems) {
			if(pedestalItem.equals(event.getItem().getUniqueId())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onDespawn(ItemDespawnEvent event) {
		for(UUID pedestalItem : pedestalItems) {
			if(pedestalItem.equals(event.getEntity().getUniqueId())) event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onAttack(AttackEvent.Pre event) {
		List<UUID> stands = new ArrayList<>();

		stands.addAll(Arrays.asList(pedestalArmorStands));
		stands.addAll(Arrays.asList(highestBidderStands));
		stands.addAll(Arrays.asList(highestBidStands));
		stands.addAll(Arrays.asList(rightClickStands));
		stands.add(timerStandUUID);

		for(UUID armorStand : stands) {
			if(armorStand.equals(event.getDefender().getUniqueId())) event.setCancelled(true);
		}
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		for(UUID armorStand : pedestalArmorStands) {
			if(armorStand.equals(event.getEntity().getUniqueId())) {
				event.setCancelled(true);
			}
		}
	}

	public static ArmorStand getStand(UUID uuid) {
		for(Entity entity : MapManager.getDarkzone().getEntities()) {
			if(!(entity instanceof ArmorStand)) continue;
			if(entity.getUniqueId().equals(uuid)) return (ArmorStand) entity;
		}
		return null;
	}

	public static Item getItem(UUID uuid) {
		for(Entity entity : MapManager.getDarkzone().getEntities()) {
			if(!(entity instanceof Item)) continue;
			if(entity.getUniqueId().equals(uuid)) return (Item) entity;
		}
		return null;
	}

	public static boolean hasPlayers(World world) {
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if(onlinePlayer.getWorld() == world) return true;
		}
		return false;
	}

	public static boolean hasPlayers(Location location) {
		for(Entity entity : location.getWorld().getNearbyEntities(location, 50, 50, 50)) {
			if(entity instanceof Player) return true;
		}
		return false;
	}

}
