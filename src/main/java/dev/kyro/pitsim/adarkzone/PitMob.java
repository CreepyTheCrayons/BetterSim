package dev.kyro.pitsim.adarkzone;

import dev.kyro.pitsim.NameTaggable;
import dev.kyro.pitsim.PitSim;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class PitMob implements NameTaggable {
	private Creature mob;
	private DropPool dropPool;
	private PitNameTag nameTag;

	public PitMob(Location spawnLocation) {
		this.dropPool = createDropPool();
		spawn(spawnLocation);
	}

	public abstract Creature createMob(Location spawnLocation);
	public abstract String getRawDisplayName();
	public abstract ChatColor getChatColor();
	public abstract int getMaxHealth();
	public abstract int getSpeedAmplifier();
	public abstract DropPool createDropPool();

	public abstract PitNameTag createNameTag();

	public String getDisplayName() {
		return getChatColor() + getRawDisplayName();
	}

	public void spawn(Location spawnLocation) {
		mob = createMob(spawnLocation);
		if(mob.isInsideVehicle()) mob.getVehicle().remove();
		mob.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999, getSpeedAmplifier(), true, false));

		mob.setMaxHealth(getMaxHealth());
		mob.setHealth(getMaxHealth());
		new BukkitRunnable() {
			@Override
			public void run() {
				mob.setMaxHealth(getMaxHealth());
				mob.setHealth(getMaxHealth());
			}
		}.runTaskLater(PitSim.INSTANCE, 1L);

		nameTag = createNameTag();
		nameTag.attach();
	}

	public void kill(Player killer) {
		dropPool.singleDistribution(killer);
		remove();
	}

	public void remove() {
		if(mob != null) mob.remove();
		nameTag.remove();
		getSubLevel().mobTargetingSystem.changeTargetCooldown.remove(this);
		getSubLevel().mobs.remove(this);
	}

	public void setTarget(Player target) {
		mob.setTarget(target);
	}

	public Player getTarget() {
		return (Player) mob.getTarget();
	}

	public Creature getMob() {
		return mob;
	}

	public void setMob(Creature mob) {
		this.mob = mob;
	}

	public SubLevel getSubLevel() {
		for(SubLevel subLevel : DarkzoneManager.subLevels) {
			for(PitMob pitMob : subLevel.mobs) if(pitMob == this) return subLevel;
		}
		throw new RuntimeException();
	}

	public DropPool getDropPool() {
		return dropPool;
	}

	public PitNameTag getNameTag() {
		return nameTag;
	}

	public void clearEquipment(Creature creature) {
		creature.getEquipment().setArmorContents(new ItemStack[5]);
	}

	@Override
	public LivingEntity getTaggableEntity() {
		return mob;
	}
}
