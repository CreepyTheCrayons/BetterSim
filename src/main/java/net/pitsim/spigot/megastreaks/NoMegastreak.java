package net.pitsim.spigot.megastreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import net.pitsim.spigot.controllers.objects.Megastreak;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.misc.PitLoreBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class NoMegastreak extends Megastreak {
	public static NoMegastreak INSTANCE;

	public NoMegastreak() {
		super("&7No Megastreak", "nomegastreak", Integer.MAX_VALUE, 0, 0);
		INSTANCE = this;
	}

	@Override
	public String getPrefix(Player player) {
		return null;
	}

	@Override
	public ItemStack getBaseDisplayStack(Player player) {
		return new AItemStackBuilder(Material.REDSTONE_BLOCK)
				.getItemStack();
	}

	@Override
	public void addBaseDescription(PitLoreBuilder loreBuilder, PitPlayer pitPlayer) {
		loreBuilder.addLongLine(
				"&7Go on high streaks with no reward modifiers and no debuffs"
		);
	}

	@Override
	public String getSummary() {
		return null;
	}
}
