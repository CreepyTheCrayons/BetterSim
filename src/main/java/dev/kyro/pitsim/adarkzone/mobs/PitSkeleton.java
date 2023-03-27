package dev.kyro.pitsim.adarkzone.mobs;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.*;
import dev.kyro.pitsim.adarkzone.notdarkzone.PitEquipment;
import dev.kyro.pitsim.aitems.mobdrops.Bone;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.enums.MobStatus;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Skeleton;
import org.bukkit.scheduler.BukkitRunnable;

public class PitSkeleton extends PitMob {

	public PitSkeleton(Location spawnLocation, MobStatus mobStatus) {
		super(spawnLocation, mobStatus);
	}

	@Override
	public SubLevelType getSubLevelType() {
		return SubLevelType.SKELETON;
	}

	@Override
	public Creature createMob(Location spawnLocation) {
		Skeleton skeleton = spawnLocation.getWorld().spawn(spawnLocation, Skeleton.class);
		skeleton.setCustomNameVisible(false);
		skeleton.setRemoveWhenFarAway(false);
		skeleton.setCanPickupItems(false);

		new BukkitRunnable() {
			@Override
			public void run() {
				PitEquipment equipment = new PitEquipment();
				equipment.setEquipment(skeleton);
			}
		}.runTaskLater(PitSim.INSTANCE, 1L);

		return skeleton;
	}

	@Override
	public String getRawDisplayName() {
		return isMinion() ? "Minion Skeleton" : "Skeleton";
	}

	@Override
	public ChatColor getChatColor() {
		return ChatColor.GRAY;
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
				.addRareItem(() -> ItemFactory.getItem(Bone.class).getItem(), DarkzoneBalancing.MOB_ITEM_DROP_PERCENT);
	}

	@Override
	public PitNameTag createNameTag() {
		return new PitNameTag(this, PitNameTag.NameTagType.NAME_AND_HEALTH)
				.addMob(PitNameTag.RidingType.SMALL_MAGMA_CUBE);
	}
}