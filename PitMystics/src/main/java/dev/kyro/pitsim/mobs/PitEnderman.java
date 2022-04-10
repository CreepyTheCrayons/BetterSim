package dev.kyro.pitsim.mobs;

import dev.kyro.pitsim.controllers.MobManager;
import dev.kyro.pitsim.controllers.objects.PitMob;
import dev.kyro.pitsim.enums.MobType;
import org.bukkit.Location;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class PitEnderman extends PitMob {

	public PitEnderman(Location spawnLoc) {
		super(MobType.ENDERMAN, spawnLoc, 1, "&cEnderman");
	}

	@Override
	public LivingEntity spawnMob(Location spawnLoc) {
		Enderman enderman = (Enderman) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.ENDERMAN);

		enderman.setMaxHealth(50);
		enderman.setHealth(50);

		enderman.setCustomNameVisible(false);
		MobManager.makeTag(enderman, displayName);
		return enderman;
	}

	@Override
	public Map<ItemStack, Integer> getDrops() {
		return null;
	}
}
