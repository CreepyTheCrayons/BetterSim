package dev.kyro.pitsim.boosters;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.Booster;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PvPBooster extends Booster {
	public static PvPBooster INSTANCE;

	public PvPBooster() {
		super("PvP Booster", "pvp", 14, ChatColor.RED);
		INSTANCE = this;
	}

	@Override
	public ItemStack getBaseDisplayItem() {
		return new AItemStackBuilder(Material.GOLD_SWORD)
				.setLore(new ALoreBuilder(
						"&7Don't lose lives on &3Jewel",
						"&7items"
				)).getItemStack();
	}
}
