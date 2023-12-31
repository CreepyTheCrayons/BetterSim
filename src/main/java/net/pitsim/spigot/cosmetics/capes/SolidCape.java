package net.pitsim.spigot.cosmetics.capes;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.spigot.cosmetics.particles.ParticleColor;
import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.cosmetics.ColorableCosmetic;
import net.pitsim.spigot.cosmetics.CosmeticType;
import net.pitsim.spigot.cosmetics.collections.CapeCollection;
import net.pitsim.spigot.cosmetics.particles.RedstoneParticle;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class SolidCape extends ColorableCosmetic {
	public CapeCollection cape;

	public SolidCape() {
		super("&7&lSolid", "solidcape", CosmeticType.CAPE);
		accountForPitch = false;

		cape = new CapeCollection(new RedstoneParticle(accountForPitch, accountForYaw));
	}

	@Override
	public void onEnable(PitPlayer pitPlayer) {
		ParticleColor particleColor = getColor(pitPlayer);
		runnableMap.put(pitPlayer.player.getUniqueId(), new BukkitRunnable() {
			@Override
			public void run() {
				cape.draw(SolidCape.this, pitPlayer, particleColor);
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 2L));
	}

	@Override
	public void onDisable(PitPlayer pitPlayer) {
		if(runnableMap.containsKey(pitPlayer.player.getUniqueId()))
			runnableMap.get(pitPlayer.player.getUniqueId()).cancel();
	}

	@Override
	public ItemStack getRawDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.BANNER, 1, 15)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Collect all the colors!"
				))
				.getItemStack();
		return itemStack;
	}
}
