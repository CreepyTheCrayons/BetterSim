package net.pitsim.spigot.darkzone.altar.pedestals;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.spigot.darkzone.altar.AltarPedestal;
import net.pitsim.spigot.cosmetics.particles.ParticleColor;
import net.pitsim.spigot.misc.Misc;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TurmoilPedestal extends AltarPedestal {
	public TurmoilPedestal(Location location) {
		super(location);
	}

	@Override
	public String getDisplayName() {
		return "&2&lTURMOIL";
	}

	@Override
	public ParticleColor getParticleColor() {
		return ParticleColor.DARK_GREEN;
	}

	@Override
	public int getActivationCost() {
		return 1;
	}

	@Override
	public ItemStack getItem(Player player) {
		AItemStackBuilder builder = new AItemStackBuilder(Material.SAPLING, 1, 3)
				.setName("&2Pedestal of Turmoil")
				.setLore(new ALoreBuilder(
						"&7This pedestal greatly &2randomizes",
						"&7your reward chances",
						"",
						"&7Activation Cost: &f" + getActivationCost() + " Souls",
						"&7Status: " + getStatus(player)
				));
		if(isActivated(player)) Misc.addEnchantGlint(builder.getItemStack());

		return builder.getItemStack();
	}
}
