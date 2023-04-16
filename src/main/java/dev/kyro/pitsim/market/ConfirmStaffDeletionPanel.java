package dev.kyro.pitsim.market;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ConfirmStaffDeletionPanel extends AGUIPanel {
	public MarketListing listing;

	public ConfirmStaffDeletionPanel(AGUI gui, MarketListing listing) {
		super(gui);

		this.listing = listing;

		AItemStackBuilder confirmBuilder = new AItemStackBuilder(Material.STAINED_CLAY, 1, 5)
				.setName("&a&lCONFIRM DELETION")
				.setLore(new ALoreBuilder(
						"&7This listing will be removed.",
						"&7The remaining Item(s) will be returned",
						"&7to you via the &f\"Your Claims\" &7menu.",
						"",
						"&eClick to confirm Deletion!"
				));
		getInventory().setItem(11, confirmBuilder.getItemStack());

		AItemStackBuilder cancelBuilder = new AItemStackBuilder(Material.STAINED_CLAY, 1, 14)
				.setName("&c&lCANCEL")
				.setLore(new ALoreBuilder(
						"&7Return to the previous menu.",
						"",
						"&eClick to cancel Deletion!"
				));
		getInventory().setItem(15, cancelBuilder.getItemStack());
	}

	@Override
	public String getName() {
		return "Confirm Deletion?";
	}

	@Override
	public int getRows() {
		return 3;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		MarketAsyncTask.MarketTask task = MarketAsyncTask.MarketTask.STAFF_REMOVE;

		BukkitRunnable delete = new BukkitRunnable() {
			@Override
			public void run() {
				AOutput.send(player, "&a&lMARKET!&7 Listing successfully deleted!");
				Sounds.SUCCESS.play(player);

				new BukkitRunnable() {
					@Override
					public void run() {
						if(listing.startingBid != -1) {
							YourListingsPanel panel = ((MarketGUI) gui).yourListingsPanel;
							openPanel(panel);
							panel.placeClaimables();
							panel.placeListings();
							return;
						}

						AUtil.giveItemSafely(player, listing.itemData);
						player.closeInventory();
					}
				}.runTask(PitSim.INSTANCE);
			}
		};

		if(event.getSlot() == 11) {
			String failMessage = "&cThere was an error while attempting to delete the listing. Check console for potential errors.";
			new MarketAsyncTask(task, listing.marketUUID, player, 0, delete, MarketAsyncTask.getDefaultFail(player, failMessage));
		}

		if(event.getSlot() == 15) openPreviousGUI();
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {

	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}
}
