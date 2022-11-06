package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.controllers.objects.RenownUpgrade;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class RenownShopPanel extends AGUIPanel {
	public PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
	public RenownShopGUI renownShopGUI;

	public RenownShopPanel(AGUI gui) {
		super(gui);
		renownShopGUI = (RenownShopGUI) gui;
	}

	@Override
	public String getName() {
		return "Renown Shop";
	}

	@Override
	public int getRows() {
		return 6;
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		int slot = event.getSlot();

		if(event.getClickedInventory().getHolder() == this) {

			if(slot == 49) {
				PrestigeGUI prestigeGUI = new PrestigeGUI(player);
				prestigeGUI.open();
			}

			for(RenownUpgrade upgrade : UpgradeManager.upgrades) {
				if(slot == upgrade.guiSlot) {
					if(upgrade.prestigeReq > pitPlayer.prestige) {
						AOutput.error(player, "&cYou are too low level to acquire this!");
						Sounds.NO.play(player);
						continue;
					}
					if(upgrade.isTiered) {
						if(UpgradeManager.hasUpgrade(player, upgrade) && upgrade.getCustomPanel() != null) {
							openPanel(upgrade.getCustomPanel());
							continue;
						}
						if(upgrade.maxTiers != UpgradeManager.getTier(player, upgrade) && upgrade.getTierCosts().get(UpgradeManager.getTier(player, upgrade)) > pitPlayer.renown) {
							AOutput.error(player, "&cYou do not have enough renown!");
							Sounds.NO.play(player);
							continue;
						}
						if(UpgradeManager.getTier(player, upgrade) < upgrade.maxTiers) {
							RenownShopGUI.purchaseConfirmations.put(player, upgrade);
							openPanel(renownShopGUI.renownShopConfirmPanel);
						} else {
							AOutput.error(player, "&aYou already unlocked the last upgrade!");
							Sounds.NO.play(player);
						}
					} else if(!UpgradeManager.hasUpgrade(player, upgrade)) {
						if(upgrade.renownCost > pitPlayer.renown) {
							AOutput.error(player, "&cYou do not have enough renown!");
							Sounds.NO.play(player);
							continue;
						}
						RenownShopGUI.purchaseConfirmations.put(player, upgrade);
						openPanel(renownShopGUI.renownShopConfirmPanel);
					} else {
						if(UpgradeManager.hasUpgrade(player, upgrade) && upgrade.getCustomPanel() != null) {
							openPanel(upgrade.getCustomPanel());
							continue;
						}
						AOutput.error(player, "&aYou already unlocked this upgrade!");
						Sounds.NO.play(player);
					}

				}
			}
			updateInventory();
			refresh();
		}
		updateInventory();
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		refresh();
	}

	public void refresh() {
		for(RenownUpgrade upgrade : UpgradeManager.upgrades) {
			ItemStack itemStack = upgrade.getDisplayItem(player, false);
			if(upgrade.prestigeReq > pitPlayer.prestige) {
				ALoreBuilder loreBuilder = new ALoreBuilder(itemStack);
				List<String> lore = loreBuilder.getLore();
				lore.remove(lore.size() - 1);
				loreBuilder = new ALoreBuilder(lore).addLore("&cUnlock Prestige: &e" + AUtil.toRoman(upgrade.prestigeReq));
				new AItemStackBuilder(itemStack).setLore(loreBuilder);
				itemStack.setType(Material.BEDROCK);
				itemStack.removeEnchantment(Enchantment.ARROW_FIRE);
			}
			getInventory().setItem(upgrade.guiSlot, itemStack);
		}

		ItemStack back = new ItemStack(Material.ARROW);
		ItemMeta backmeta = back.getItemMeta();
		backmeta.setDisplayName(ChatColor.GREEN + "Go Back");
		List<String> backlore = new ArrayList<>();
		backlore.add(ChatColor.GRAY + "To Prestige & Renown");
		backmeta.setLore(backlore);
		back.setItemMeta(backmeta);

		getInventory().setItem(49, back);
	}

	@Override
	public void onClose(InventoryCloseEvent event) {

	}

}
