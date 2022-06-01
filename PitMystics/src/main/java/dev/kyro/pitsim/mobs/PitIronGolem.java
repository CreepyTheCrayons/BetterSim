package dev.kyro.pitsim.mobs;

import dev.kyro.pitsim.brewing.ingredients.IronIngot;
import dev.kyro.pitsim.brewing.ingredients.RawPork;
import dev.kyro.pitsim.controllers.MobManager;
import dev.kyro.pitsim.controllers.objects.PitMob;
import dev.kyro.pitsim.enums.MobType;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class PitIronGolem extends PitMob {

	public PitIronGolem(Location spawnLoc) {
		super(MobType.IRON_GOLEM, spawnLoc, 9, "&cIron Golem");
	}

	@Override
	public LivingEntity spawnMob(Location spawnLoc) {
		IronGolem ironGolem = (IronGolem) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.IRON_GOLEM);

		ironGolem.setMaxHealth(50);
		ironGolem.setHealth(50);
		ironGolem.setRemoveWhenFarAway(false);

		ironGolem.setCustomNameVisible(false);
		MobManager.makeTag(ironGolem, displayName);
		return ironGolem;
	}

	@Override
	public Map<ItemStack, Integer> getDrops() {
		Map<ItemStack, Integer> drops = new HashMap<>();
		drops.put(IronIngot.INSTANCE.getItem(), 50);

		return drops;
	}
}
