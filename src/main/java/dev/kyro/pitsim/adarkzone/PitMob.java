package dev.kyro.pitsim.adarkzone;

import dev.kyro.pitsim.NameTaggable;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public abstract class PitMob implements NameTaggable {

	private Creature mob;
	private Player target;
	private DropPool dropPool;
	private PitNameTag nameTag;

	public PitMob(Location spawnLocation) {
		spawn(spawnLocation);
	}

	public abstract String getRawDisplayName();
	public abstract ChatColor getChatColor();
	public abstract EntityType getEntityType();
	public abstract int getMaxHealth();
	public abstract int getSpeedAmplifier();
	public abstract DropPool createDropPool();
	public abstract PitNameTag createNameTag();

	public String getDisplayName() {
		return getChatColor() + getRawDisplayName();
	}

	public void onSpawn() {}

	public void spawn(Location spawnLocation) {
		mob = (Creature) spawnLocation.getWorld().spawnEntity(spawnLocation, getEntityType());
		dropPool = createDropPool();
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
		getSubLevel().mobs.remove(this);
	}

	public void setTarget(Player target) {
		this.target = target;
		mob.setTarget(target);
	}

	public Player getTarget() {
		return target;
	}

	public void rewardKill(Player killer) {
		killer.getInventory().addItem(dropPool.getRandomDrop());
	}

	public Creature getMob() {
		return mob;
	}

	public void setMob(Creature mob) {
		this.mob = mob;
	}

	@Override
	public LivingEntity getTaggableEntity() {
		return mob;
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
}
