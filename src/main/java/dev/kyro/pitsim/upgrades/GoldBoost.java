package dev.kyro.pitsim.upgrades;

import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.RenownUpgrade;
import dev.kyro.pitsim.events.KillEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GoldBoost extends RenownUpgrade {

	public GoldBoost() {
		super("Renown Gold Boost", "GOLD_BOOST", 10, 10, 1, true, 10);
	}

	@Override
	public List<Integer> getTierCosts() {
		return Arrays.asList(10, 12, 14, 16, 18, 20, 22, 24, 26, 28);
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!killEvent.isKillerPlayer()) return;
		if(!UpgradeManager.hasUpgrade(killEvent.getKillerPlayer(), this)) return;

		int tier = UpgradeManager.getTier(killEvent.getKillerPlayer(), this);
		if(tier == 0) return;

		double percent = 2.5 * tier;

		killEvent.goldMultipliers.add((percent / 100D) + 1);
	}

	@Override
	public ItemStack getDisplayItem(Player player) {
		ItemStack item = new ItemStack(Material.GOLD_NUGGET);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(UpgradeManager.itemNameString(this, player));
		List<String> lore = new ArrayList<>();
		if(UpgradeManager.hasUpgrade(player, this)) lore.add(ChatColor.translateAlternateColorCodes('&',
				"&7Current: &6+" + 2.5 * UpgradeManager.getTier(player, this) + "&6% gold (g)"));
		if(UpgradeManager.hasUpgrade(player, this))
			lore.add(ChatColor.GRAY + "Tier: " + ChatColor.GREEN + AUtil.toRoman(UpgradeManager.getTier(player, this)));
		if(UpgradeManager.hasUpgrade(player, this)) lore.add("");
		lore.add(ChatColor.GRAY + "Each Tier:");
		lore.add(ChatColor.GRAY + "Earn " + ChatColor.GOLD + "+2.5% gold (g) " + ChatColor.GRAY + "from");
		lore.add(ChatColor.GRAY + "kills.");
		meta.setLore(UpgradeManager.loreBuilder(this, player, lore, false));
		item.setItemMeta(meta);
		return item;
	}

	@Override
	public String getSummary() {
		return "&eRenown &6Gold Boost &7is an renown upgrade that gives you &62.5% extra gold &7on a player/bot kill per tier";
	}
}
