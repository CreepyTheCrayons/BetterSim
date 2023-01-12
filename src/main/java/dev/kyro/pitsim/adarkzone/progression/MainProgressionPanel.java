package dev.kyro.pitsim.adarkzone.progression;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.adarkzone.notdarkzone.UnlockState;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Lang;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.tutorial.HelpItemStacks;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class MainProgressionPanel extends AGUIPanel {
	public ProgressionGUI progressionGUI;
	public PitPlayer pitPlayer;

	public MainProgressionPanel(AGUI gui) {
		super(gui);
		this.progressionGUI = (ProgressionGUI) gui;
		this.pitPlayer = progressionGUI.pitPlayer;

		inventoryBuilder.setSlots(Material.STAINED_GLASS_PANE, 15, 45, 46, 47, 48, 49, 50, 51, 52);
		getInventory().setItem(53, HelpItemStacks.getMainProgressionStack());

		setInventory();
	}

	@Override
	public String getName() {
		return "" + ChatColor.DARK_PURPLE + ChatColor.BOLD + "Skill Tree";
	}

	@Override
	public int getRows() {
		return 6;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		if(event.getClickedInventory().getHolder() != this) return;
		int slot = event.getSlot();

		for(MainProgressionUnlock unlock : ProgressionManager.mainProgressionUnlocks) {
			if(slot != unlock.getSlot()) continue;
			UnlockState state = ProgressionManager.getUnlockState(pitPlayer, unlock);
			if(state == UnlockState.LOCKED) {
				Sounds.NO.play(player);
			} else if(state == UnlockState.NEXT_TO_UNLOCK) {
				int cost = ProgressionManager.getUnlockCost(pitPlayer, unlock);
				if(pitPlayer.taintedSouls < cost) {
					Lang.NOT_ENOUGH_SOULS.send(player);
					return;
				}
				ProgressionManager.unlock(pitPlayer, unlock);
				setInventory();
			} else if(state == UnlockState.UNLOCKED) {
				if(unlock instanceof MainProgressionMajorUnlock) {
//					TODO: Open sub panel
				}
			}
		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {}

	@Override
	public void onClose(InventoryCloseEvent event) {}

	public void setInventory() {
		for(MainProgressionUnlock unlock : ProgressionManager.mainProgressionUnlocks) {
			UnlockState state = ProgressionManager.getUnlockState(pitPlayer, unlock);
			getInventory().setItem(unlock.getSlot(), unlock.getDisplayStack(pitPlayer, state));
		}
		updateInventory();
	}
}
