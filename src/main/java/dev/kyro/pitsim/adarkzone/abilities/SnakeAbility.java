package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.PitBossAbility;
import dev.kyro.pitsim.controllers.DamageManager;
import dev.kyro.pitsim.enums.PitEntityType;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.misc.effects.FallingBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class SnakeAbility extends PitBossAbility {
	public int length;
	public double damage;
	public Material blockType;
	public byte blockData;
	public Sounds.SoundEffect effect;

	public SnakeAbility(double routineWeight, int length, double damage, Material blockType, byte blockData, Sounds.SoundEffect effect) {
		super(routineWeight);
		this.length = length;
		this.damage = damage;
		this.blockType = blockType;
		this.blockData = blockData;
		this.effect = effect;
	}

	@Override
	public void onRoutineExecute() {
		Vector direction = getPitBoss().boss.getLocation().getDirection().multiply(15);
		Location origin = getPitBoss().boss.getLocation();
		direction.divide(new Vector(length, length, length));

		int time = 0;

		for(int i = 0; i < length; i++) {
			new BukkitRunnable() {
				@Override
				public void run() {
					origin.add(direction);
					FallingBlock fallingBlock = new FallingBlock(blockType, blockData, origin);
					fallingBlock.setViewers(getViewers());
					fallingBlock.spawnBlock();
					fallingBlock.removeAfter(10);
					fallingBlock.setVelocity(new Vector(0, 0.2, 0));
					effect.play(origin, 20);

					for(Entity entity : origin.getWorld().getNearbyEntities(origin, 1.5, 1.5, 1.5)) {
						if(!Misc.isEntity(entity, PitEntityType.REAL_PLAYER)) continue;
						Player target = (Player) entity;

						DamageManager.createIndirectAttack(getPitBoss().boss, target, damage);
						Misc.applyPotionEffect(target, PotionEffectType.SLOW, 20, 1, false, false);
					}
				}
			}.runTaskLater(PitSim.INSTANCE, time);
			time += 1;
		}
	}
}

