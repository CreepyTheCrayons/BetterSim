package dev.kyro.pitsim.upgrades;

import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.RenownUpgrade;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Chemist extends RenownUpgrade {
	public Chemist() {
		super("Chemist", "CHEMIST", 35, 21, 11, true, 2);
	}

	@Override
	public List<Integer> getTierCosts() {
		return Arrays.asList(30, 45);
	}

	@Override
	public ItemStack getDisplayItem(Player player) {
		ItemStack item = new ItemStack(Material.POTION, 1, (short) 2);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(UpgradeManager.itemNameString(this, player));
		List<String> lore = new ArrayList<>();
		if(UpgradeManager.hasUpgrade(player, this)) lore.add(ChatColor.translateAlternateColorCodes('&',
				"&7Current: &e+" + UpgradeManager.getTier(player, this) + " Slots"));
		if(UpgradeManager.hasUpgrade(player, this))
			lore.add(ChatColor.GRAY + "Tier: " + ChatColor.GREEN + AUtil.toRoman(UpgradeManager.getTier(player, this)));
		if(UpgradeManager.hasUpgrade(player, this)) lore.add("");
		lore.add(ChatColor.GRAY + "Each Tier:");
		lore.add(ChatColor.GRAY + "Gain " + ChatColor.YELLOW + "+1 Potion Brewing Slot");
		lore.add(ChatColor.GRAY + "in the " + ChatColor.DARK_PURPLE + "Darkzone" + ChatColor.GRAY + ".");
		meta.setLore(UpgradeManager.loreBuilder(this, player, lore, false));
		meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		item.setItemMeta(meta);
		return item;
	}

	@Override
	public String getSummary() {
		return "&eChemist&7 is a r&erenown&7 perk that increases how many &5potions&7 you can brew at a time";
	}
}
