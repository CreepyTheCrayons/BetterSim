package dev.kyro.pitsim.misc;

import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.RenownUpgrade;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RenownUpgradeDisplays {


	public static ItemStack getDisplayItem(RenownUpgrade upgrade, Player player) {
		if(upgrade.equals(RenownUpgrade.GOLD_BOOST)) {
			ItemStack item = new ItemStack(Material.GOLD_NUGGET);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(itemNameString(upgrade, player));
			List<String> lore = new ArrayList<>();
			if(RenownUpgrade.hasUpgrade(player, upgrade)) lore.add(ChatColor.translateAlternateColorCodes('&',
					"&7Current: &6+" + 2.5 * RenownUpgrade.getTier(player, upgrade) + "&6% gold (g)"));
			if(RenownUpgrade.hasUpgrade(player, upgrade)) lore.add(ChatColor.GRAY + "Tier: " + ChatColor.GREEN + AUtil.toRoman(RenownUpgrade.getTier(player, upgrade)));
			if(RenownUpgrade.hasUpgrade(player, upgrade)) lore.add("");
			lore.add(ChatColor.GRAY + "Each tier:");
			lore.add(ChatColor.GRAY + "Earn " + ChatColor.GOLD + "+2.5% gold (g) " + ChatColor.GRAY + "from");
			lore.add(ChatColor.GRAY + "kills.");
			meta.setLore(loreBuilder(upgrade, player, lore));
			item.setItemMeta(meta);
			return item;
		}
		if(upgrade.equals(RenownUpgrade.XP_BOOST)) {
			ItemStack item = new ItemStack(Material.EXP_BOTTLE);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(itemNameString(upgrade, player));
			List<String> lore = new ArrayList<>();
			if(RenownUpgrade.hasUpgrade(player, upgrade)) lore.add(ChatColor.translateAlternateColorCodes('&',
					"&7Current: &b+" + 5 * RenownUpgrade.getTier(player, upgrade) + " &bXP"));
			if(RenownUpgrade.hasUpgrade(player, upgrade)) lore.add(ChatColor.GRAY + "Tier: " + ChatColor.GREEN + AUtil.toRoman(RenownUpgrade.getTier(player, upgrade)));
			if(RenownUpgrade.hasUpgrade(player, upgrade)) lore.add("");
			lore.add(ChatColor.GRAY + "Each tier:");
			lore.add(ChatColor.GRAY + "Earn " + ChatColor.AQUA + "+5 XP " + ChatColor.GRAY + "from kills.");
			meta.setLore(loreBuilder(upgrade, player, lore));
			item.setItemMeta(meta);
			return item;
		}
		if(upgrade.equals(RenownUpgrade.TENACITY)) {
			ItemStack item = new ItemStack(Material.MAGMA_CREAM);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(itemNameString(upgrade, player));
			List<String> lore = new ArrayList<>();
			if(RenownUpgrade.hasUpgrade(player, upgrade)) lore.add(ChatColor.translateAlternateColorCodes('&',
					"&7Current: Heal &c" + 0.5 * RenownUpgrade.getTier(player, upgrade) + "\u2764 &7on kill."));
			if(RenownUpgrade.hasUpgrade(player, upgrade)) lore.add(ChatColor.GRAY + "Tier: " + ChatColor.GREEN + AUtil.toRoman(RenownUpgrade.getTier(player, upgrade)));
			if(RenownUpgrade.hasUpgrade(player, upgrade)) lore.add("");
			lore.add(ChatColor.GRAY + "Each tier:");
			lore.add(ChatColor.GRAY + "Heal " + ChatColor.RED + "0.5\u2764 "  + ChatColor.GRAY + "on kill.");
			meta.setLore(loreBuilder(upgrade, player, lore));
			item.setItemMeta(meta);
			return item;
		}

		return null;
	}

	private static String renownCostString(RenownUpgrade upgrade, Player player) {
		if(!upgrade.isTiered) {
			if(!RenownUpgrade.hasUpgrade(player, upgrade)) return ChatColor.YELLOW + String.valueOf(upgrade.renownCost) + " Renown";
			return ChatColor.GREEN + "Already unlocked!";
		}
		if(RenownUpgrade.getTier(player, upgrade) == 0) return ChatColor.YELLOW + String.valueOf(upgrade.renownCost) + " Renown";
		if(RenownUpgrade.getTier(player, upgrade) == upgrade.maxTiers) return ChatColor.GREEN + "Fully upgraded!";
		return ChatColor.YELLOW + upgrade.tierCosts.get(RenownUpgrade.getTier(player, upgrade)).toString() + " Renown";
	}

	private static String itemNameString(RenownUpgrade upgrade, Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(!upgrade.isTiered && RenownUpgrade.hasUpgrade(player, upgrade)) return ChatColor.GREEN + upgrade.refName;
		if(upgrade.isTiered && RenownUpgrade.getTier(player, upgrade) == upgrade.maxTiers) return ChatColor.GREEN + upgrade.refName;
		if(!upgrade.isTiered && pitPlayer.renown < upgrade.renownCost) return ChatColor.RED + upgrade.refName;
		if(upgrade.isTiered && pitPlayer.renown < upgrade.tierCosts.get(RenownUpgrade.getTier(player, upgrade))) return ChatColor.RED + upgrade.refName;
		return ChatColor.YELLOW + upgrade.refName;
	}

	private static String purchaseMessageString(RenownUpgrade upgrade, Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(!upgrade.isTiered && RenownUpgrade.hasUpgrade(player, upgrade)) return ChatColor.GREEN + "Unlocked!";
		if(upgrade.isTiered && RenownUpgrade.getTier(player, upgrade) == upgrade.maxTiers) return ChatColor.GREEN + "Max tier unlocked!";
		if(!upgrade.isTiered && pitPlayer.renown < upgrade.renownCost) return ChatColor.RED + "Not enough renown!";
		if(upgrade.isTiered && pitPlayer.renown < upgrade.tierCosts.get(RenownUpgrade.getTier(player, upgrade))) return ChatColor.RED + "Not enough renown!";
		return ChatColor.YELLOW + "Click to purchase!";
	}

	private static List<String> loreBuilder(RenownUpgrade upgrade, Player player, List<String> originalLore) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		List<String> lore = new ArrayList<>(originalLore);
		lore.add("");
		if(upgrade.isTiered && RenownUpgrade.getTier(player, upgrade) != upgrade.maxTiers) {
			lore.add(ChatColor.translateAlternateColorCodes('&', "&7Cost: &e" + upgrade.tierCosts.get(RenownUpgrade.getTier(player, upgrade)) + " Renown"));
			lore.add(ChatColor.translateAlternateColorCodes('&', "&7You have: &e" + pitPlayer.renown + " Renown"));
			lore.add("");
		}
		if(!upgrade.isTiered && !RenownUpgrade.hasUpgrade(player, upgrade)) {
			lore.add(ChatColor.translateAlternateColorCodes('&', "&7Cost: &e" + upgrade.tierCosts.get(RenownUpgrade.getTier(player, upgrade)) + " Renown"));
			lore.add(ChatColor.translateAlternateColorCodes('&', "&7You have: &e" + pitPlayer.renown + " Renown"));
			lore.add("");
		}
		lore.add(purchaseMessageString(upgrade, player));

		return lore;
	}

	public static List<Integer> goldBoostCosts = Arrays.asList(10, 12, 14, 16, 18, 20, 22, 24, 26, 28);
	public static List<Integer> XPBoostCosts = Arrays.asList(10, 12, 14, 16, 18, 20, 22, 24, 26, 28);
	public static List<Integer> TenacityCosts = Arrays.asList(10, 50);
}
