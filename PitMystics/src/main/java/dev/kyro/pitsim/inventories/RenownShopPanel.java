package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.data.APlayerData;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.controllers.objects.RenownUpgrade;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public class RenownShopPanel extends AGUIPanel {

    FileConfiguration playerData = APlayerData.getPlayerData(player);
    PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
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
        return 5;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        int slot = event.getSlot();

        if(event.getClickedInventory().getHolder() == this) {

            for(RenownUpgrade upgrade : UpgradeManager.upgrades) {
                if(slot == upgrade.guiSlot) {
                    if(upgrade.levelReq > pitPlayer.playerLevel) {
                        AOutput.error(player, "&cYou are too low level to acquire this!");
                        player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1 ,1);
                        continue;
                    }
                    if(upgrade.isTiered) {
                        if(UpgradeManager.hasUpgrade(player, upgrade) && upgrade.getCustomPanel() != null) {
                            openPanel(upgrade.getCustomPanel());
                            continue;
                        }
                        if(upgrade.maxTiers != UpgradeManager.getTier(player, upgrade) && upgrade.getTierCosts().get(UpgradeManager.getTier(player, upgrade)) > pitPlayer.renown) {
                            AOutput.error(player, "&cYou do not have enough renown!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1 ,1);
                            continue;
                        }
                        if(UpgradeManager.getTier(player, upgrade) < upgrade.maxTiers) {
                            RenownShopGUI.purchaseConfirmations.put(player, upgrade);
                            openPanel(renownShopGUI.renownShopConfirmPanel);
                        } else {
                            AOutput.error(player, "&aYou already unlocked the last upgrade!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1 ,1);
                        }
                    } else if(!UpgradeManager.hasUpgrade(player, upgrade)) {
                        if(upgrade.renownCost > pitPlayer.renown) {
                            AOutput.error(player, "&cYou do not have enough renown!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1 ,1);
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
                        player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1 ,1);
                    }

                }
            }
            APlayerData.savePlayerData(player);
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

        ItemStack item = new ItemStack(Material.BEDROCK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Unknown upgrade");


        for (RenownUpgrade upg : UpgradeManager.upgrades) {
            if(upg.levelReq > pitPlayer.playerLevel) {
                List<String> lore = Collections.singletonList(ChatColor.GRAY + "Level: " + ChatColor.YELLOW + upg.levelReq);
                meta.setLore(lore);
                item.setItemMeta(meta);
                getInventory().setItem(upg.guiSlot, item);
            } else getInventory().setItem(upg.guiSlot, upg.getDisplayItem(player, false));
        }
    }

    @Override
    public void onClose(InventoryCloseEvent event) {

    }

}
