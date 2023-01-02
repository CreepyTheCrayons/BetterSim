package dev.kyro.pitsim.mobs;

import dev.kyro.pitsim.brewing.ingredients.RawPork;
import dev.kyro.pitsim.adarkzone.aaold.OldMobManager;
import dev.kyro.pitsim.adarkzone.aaold.OldPitMob;
import dev.kyro.pitsim.enums.MobType;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class OldPitZombiePigman extends OldPitMob {

	public OldPitZombiePigman(Location spawnLoc) {
		super(MobType.ZOMBIE_PIGMAN, spawnLoc, 7, MobValues.pigmanDamage, "&cZombie Pigman", MobValues.pigmanSpeed);
	}

	@Override
	public LivingEntity spawnMob(Location spawnLoc) {
		PigZombie zombiePigman = (PigZombie) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.PIG_ZOMBIE);

		zombiePigman.setMaxHealth(MobValues.pigmanHealth);
		zombiePigman.setHealth(MobValues.pigmanHealth);
		zombiePigman.setAngry(true);
		zombiePigman.setCustomNameVisible(false);
		zombiePigman.setRemoveWhenFarAway(false);
		zombiePigman.setBaby(false);
		OldMobManager.makeTag(zombiePigman, displayName);
		return zombiePigman;
	}

	@Override
	public Map<ItemStack, Integer> getDrops() {
		Map<ItemStack, Integer> drops = new HashMap<>();
		drops.put(RawPork.INSTANCE.getItem(), 8);

		return drops;
	}
}
