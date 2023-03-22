package dev.kyro.pitsim.adarkzone.mobs;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.*;
import dev.kyro.pitsim.adarkzone.notdarkzone.PitEquipment;
import dev.kyro.pitsim.aitems.mobdrops.RottenFlesh;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.enums.MobStatus;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Zombie;
import org.bukkit.scheduler.BukkitRunnable;

public class PitZombie extends PitMob {

	public PitZombie(Location spawnLocation, MobStatus mobStatus) {
		super(spawnLocation, mobStatus);
	}

	@Override
	public SubLevelType getSubLevelType() {
		return SubLevelType.ZOMBIE;
	}

	@Override
	public Creature createMob(Location spawnLocation) {
		Zombie zombie = spawnLocation.getWorld().spawn(spawnLocation, Zombie.class);
		zombie.setCustomNameVisible(false);
		zombie.setRemoveWhenFarAway(false);
		zombie.setCanPickupItems(false);

		zombie.setBaby(false);
		zombie.setVillager(false);

		new BukkitRunnable() {
			@Override
			public void run() {
				new PitEquipment().setEquipment(zombie);
			}
		}.runTaskLater(PitSim.INSTANCE, 1L);

		return zombie;
	}

	@Override
	public String getRawDisplayName() {
		return isMinion() ? "Minion Zombie" : "Zombie";
	}

	@Override
	public ChatColor getChatColor() {
		return ChatColor.DARK_GREEN;
	}

	@Override
	public int getMaxHealth() {
		return DarkzoneBalancing.getAttributeAsInt(getSubLevelType(), DarkzoneBalancing.Attribute.MOB_HEALTH);
	}

	@Override
	public double getDamage() {
		return DarkzoneBalancing.getAttributeAsInt(getSubLevelType(), DarkzoneBalancing.Attribute.MOB_DAMAGE);
	}

	@Override
	public int getSpeedAmplifier() {
		return 1;
	}

	@Override
	public int getDroppedSouls() {
		return DarkzoneBalancing.getAttributeAsInt(getSubLevelType(), DarkzoneBalancing.Attribute.MOB_SOULS);
	}

	@Override
	public DropPool createDropPool() {
		return new DropPool()
				.addItem(ItemFactory.getItem(RottenFlesh.class).getItem(), 1);
	}

	@Override
	public PitNameTag createNameTag() {
		return new PitNameTag(this, PitNameTag.NameTagType.NAME_AND_HEALTH)
				.addMob(PitNameTag.RidingType.SMALL_MAGMA_CUBE);
	}
}