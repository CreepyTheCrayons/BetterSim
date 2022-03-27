package dev.kyro.pitsim.mobs;

import dev.kyro.pitsim.controllers.MobManager;
import dev.kyro.pitsim.controllers.objects.PitMob;
import dev.kyro.pitsim.enums.MobType;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;

public class PitMagmaCube extends PitMob {

	public PitMagmaCube(Location spawnLoc) {
		super(MobType.MAGMA_CUBE, spawnLoc, 1, "&cMagma Cube");
	}

	@Override
	public LivingEntity spawnMob(Location spawnLoc) {
		MagmaCube magmaCube = (MagmaCube) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.MAGMA_CUBE);

		magmaCube.setMaxHealth(50);
		magmaCube.setHealth(50);

		magmaCube.setCustomNameVisible(false);
		MobManager.makeTag(magmaCube, displayName);
		return magmaCube;
	}
}
