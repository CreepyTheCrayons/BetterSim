package net.pitsim.spigot.cosmetics.capes;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.cosmetics.CosmeticType;
import net.pitsim.spigot.cosmetics.PitCosmetic;
import net.pitsim.spigot.cosmetics.collections.CapeCollection;
import net.pitsim.spigot.cosmetics.particles.CritMagicParticle;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class CritMagicCape extends PitCosmetic {
	public CapeCollection cape;

	public CritMagicCape() {
		super("&3Crit Magic Cape", "critmagiccape", CosmeticType.CAPE);
		accountForPitch = false;

		cape = new CapeCollection(new CritMagicParticle(accountForPitch, accountForYaw));
	}

	@Override
	public void onEnable(PitPlayer pitPlayer) {
		runnableMap.put(pitPlayer.player.getUniqueId(), new BukkitRunnable() {
			@Override
			public void run() {
				cape.draw(CritMagicCape.this, pitPlayer, null);
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 4L));
	}

	@Override
	public void onDisable(PitPlayer pitPlayer) {
		if(runnableMap.containsKey(pitPlayer.player.getUniqueId()))
			runnableMap.get(pitPlayer.player.getUniqueId()).cancel();
	}

	@Override
	public ItemStack getRawDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.ENCHANTMENT_TABLE)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7nice"
				))
				.getItemStack();
		return itemStack;
	}
}
