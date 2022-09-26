package dev.kyro.pitsim.controllers;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class ItemManager implements Listener {

	public static ItemStack enableUndroppable(ItemStack itemStack) {

		if(Misc.isAirOrNull(itemStack)) return itemStack;
		NBTItem nbtItem = new NBTItem(itemStack);

		nbtItem.setBoolean(NBTTag.UNDROPPABLE.getRef(), true);
		return nbtItem.getItem();
	}

	public static ItemStack enableDropConfirm(ItemStack itemStack) {

		if(Misc.isAirOrNull(itemStack)) return itemStack;
		NBTItem nbtItem = new NBTItem(itemStack);

		nbtItem.setBoolean(NBTTag.DROP_CONFIRM.getRef(), true);
		return nbtItem.getItem();
	}

	@EventHandler
	public static void onInventoryClick(InventoryClickEvent event) {
		if(event.getAction() != InventoryAction.DROP_ALL_CURSOR && event.getAction() != InventoryAction.DROP_ALL_SLOT &&
				event.getAction() != InventoryAction.DROP_ONE_CURSOR && event.getAction() != InventoryAction.DROP_ONE_SLOT)
			return;

		ItemStack itemStack = !Misc.isAirOrNull(event.getCursor()) ? event.getCursor() : event.getCurrentItem();
		Player player = (Player) event.getWhoClicked();
		if(Misc.isAirOrNull(itemStack)) return;
		NBTItem nbtItem = new NBTItem(itemStack);

		if(!nbtItem.hasKey(NBTTag.DROP_CONFIRM.getRef())) return;

		event.setCancelled(true);
		player.updateInventory();
		AOutput.error(player, "This item cannot be dropped from your inventory");
		Sounds.WARNING_LOUD.play(player);
	}

	@EventHandler(ignoreCancelled = true)
	public static void onItemDrop(PlayerDropItemEvent event) {
		ItemStack itemStack = event.getItemDrop().getItemStack();
		Player player = event.getPlayer();
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(Misc.isAirOrNull(itemStack)) return;
		NBTItem nbtItem = new NBTItem(itemStack);

		Location darkAuction = AuctionDisplays.pedestalLocations[0];
		double distance = 50;
		if(player.getWorld() == MapManager.getDarkzone()) distance = darkAuction.distance(event.getPlayer().getLocation());

		boolean cancelDrop;
		cancelDrop = event.getPlayer().getWorld() == MapManager.getDarkzone() && distance < 50;
		if(cancelDrop) {
			event.setCancelled(true);
			AOutput.error(player, "&cYou cannot drop items in this area!");
			Sounds.WARNING_LOUD.play(player);
			return;
		}


		if(nbtItem.hasKey(NBTTag.UNDROPPABLE.getRef())) {

			event.setCancelled(true);
			AOutput.error(player, "You are not able to drop that item");
			Sounds.WARNING_LOUD.play(player);
		}

		if(nbtItem.hasKey(NBTTag.DROP_CONFIRM.getRef())) {

			if(pitPlayer.confirmedDrop == null || !pitPlayer.confirmedDrop.equals(itemStack)) {

				event.setCancelled(true);
				new BukkitRunnable() {
					@Override
					public void run() {
						if(pitPlayer.confirmedDrop != null && pitPlayer.confirmedDrop.equals(itemStack))
							pitPlayer.confirmedDrop = null;
					}
				}.runTaskLater(PitSim.INSTANCE, 60L);
				pitPlayer.confirmedDrop = itemStack;
				AOutput.error(player, "&e&lWARNING! &7You are about to drop an item. Click the drop button again to drop the item.");
				Sounds.WARNING_LOUD.play(player);
			} else {
				pitPlayer.confirmedDrop = null;
			}
		}
	}


	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		ItemStack item = event.getItemDrop().getItemStack();
		if(Misc.isAirOrNull(item)) return;

		NBTItem nbtItem = new NBTItem(item);
		if(nbtItem.hasKey(NBTTag.IS_GEM.getRef()) && !nbtItem.hasKey(NBTTag.UNDROPPABLE.getRef())) {
			nbtItem.setBoolean(NBTTag.UNDROPPABLE.getRef(), true);
			event.getItemDrop().setItemStack(new ItemStack(Material.AIR));
			AUtil.giveItemSafely(event.getPlayer(), nbtItem.getItem());
			event.setCancelled(true);
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				event.getPlayer().getInventory().remove(Material.STONE);
			}
		}.runTaskLater(PitSim.INSTANCE, 1);

	}
}
