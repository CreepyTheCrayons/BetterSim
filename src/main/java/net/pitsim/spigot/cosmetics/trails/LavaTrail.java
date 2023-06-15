package net.pitsim.spigot.cosmetics.trails;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.cosmetics.*;
import net.pitsim.spigot.cosmetics.collections.ParticleCollection;
import net.pitsim.spigot.cosmetics.particles.LavaParticle;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class LavaTrail extends PitCosmetic {
	public ParticleCollection collection = new ParticleCollection();

	public LavaTrail() {
		super("&6Lava &4Trail", "lavatrail", CosmeticType.PARTICLE_TRAIL);
		accountForPitch = false;

		PitParticle particle = new LavaParticle(accountForPitch, accountForYaw);

		Vector vector = new Vector(0, 0, 0);
		collection.addParticle("main", particle, new ParticleOffset(vector, 1, 0, 1));
	}

	@Override
	public void onEnable(PitPlayer pitPlayer) {
		runnableMap.put(pitPlayer.player.getUniqueId(), new BukkitRunnable() {
			@Override
			public void run() {
				if(CosmeticManager.isStandingStill(pitPlayer.player)) return;
				Location displayLocation = pitPlayer.player.getLocation();
				for(Player onlinePlayer : CosmeticManager.getDisplayPlayers(pitPlayer.player, displayLocation)) {
					PitPlayer onlinePitPlayer = PitPlayer.getPitPlayer(onlinePlayer);
					if(onlinePlayer != pitPlayer.player && !onlinePitPlayer.playerSettings.trailParticles) continue;

					EntityPlayer entityPlayer = ((CraftPlayer) onlinePlayer).getHandle();
					collection.displayAll(entityPlayer, displayLocation);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L));
	}

	@Override
	public void onDisable(PitPlayer pitPlayer) {
		if(runnableMap.containsKey(pitPlayer.player.getUniqueId()))
			runnableMap.get(pitPlayer.player.getUniqueId()).cancel();
	}

	@Override
	public ItemStack getRawDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.LAVA_BUCKET)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Quick! The floor is lava!"
				))
				.getItemStack();
		return itemStack;
	}
}
