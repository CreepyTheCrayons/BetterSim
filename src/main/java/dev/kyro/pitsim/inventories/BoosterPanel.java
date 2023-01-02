package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.logging.LogManager;
import dev.kyro.pitsim.controllers.BoosterManager;
import dev.kyro.pitsim.controllers.ProxyMessaging;
import dev.kyro.pitsim.controllers.objects.Booster;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class BoosterPanel extends AGUIPanel {
	PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
	public BoosterGUI boosterGUI;

	public BoosterPanel(AGUI gui) {
		super(gui);
		boosterGUI = (BoosterGUI) gui;
		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 8);
	}

	@Override
	public String getName() {
		return "Boosters";
	}

	@Override
	public int getRows() {
		return 3;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		int slot = event.getSlot();
		if(event.getClickedInventory().getHolder() == this) {
			for(Booster booster : BoosterManager.boosterList) {
				if(booster.slot == slot) {
					if(Booster.getBoosterAmount(player, booster) < 1) {
						Sounds.SUCCESS.play(player);
						AOutput.send(player, "&aBuy boosters at https://store.pitsim.net");
						return;
					} else {
						Sounds.SUCCESS.play(player);
						Booster.setBooster(player, booster, Booster.getBoosterAmount(player, booster) - 1);
						ProxyMessaging.sendBoosterUse(booster, player, 60, true);
						LogManager.onBoosterActivate(player, booster);
					}

					player.closeInventory();
				}
			}
		}
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		for(Booster booster : BoosterManager.boosterList) {
			AItemStackBuilder builder = new AItemStackBuilder(booster.getDisplayItem());
			ALoreBuilder loreBuilder = new ALoreBuilder(booster.getDisplayItem().getItemMeta().getLore());
			loreBuilder.addLore("&7You have: &e" + Booster.getBoosterAmount(player, booster), "");
			if(Booster.getBoosterAmount(player, booster) > 0) {
				loreBuilder.addLore("&eClick to use booster!");
			} else loreBuilder.addLore("&eClick to buy booster!");
			builder.setLore(loreBuilder);
			if(booster.minutes > 0) Misc.addEnchantGlint(builder.getItemStack());
			builder.getItemStack().setAmount(Math.min(booster.minutes, 64));
			getInventory().setItem(booster.slot, builder.getItemStack());
		}
	}

	@Override
	public void onClose(InventoryCloseEvent event) {
	}
}
