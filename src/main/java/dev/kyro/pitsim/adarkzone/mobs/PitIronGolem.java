package dev.kyro.pitsim.adarkzone.mobs;

import dev.kyro.pitsim.adarkzone.*;
import dev.kyro.pitsim.aitems.mobdrops.IronIngot;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.enums.MobStatus;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.IronGolem;

public class PitIronGolem extends PitMob {

	public PitIronGolem(Location spawnLocation, MobStatus mobStatus) {
		super(spawnLocation, mobStatus);
	}

	@Override
	public SubLevelType getSubLevelType() {
		return SubLevelType.IRON_GOLEM;
	}

	@Override
	public Creature createMob(Location spawnLocation) {
		IronGolem ironGolem = spawnLocation.getWorld().spawn(spawnLocation, IronGolem.class);
		ironGolem.setCustomNameVisible(false);
		ironGolem.setRemoveWhenFarAway(false);
		ironGolem.setCanPickupItems(false);

		return ironGolem;
	}

	@Override
	public String getRawDisplayName() {
		return isMinion() ? "Minion Golem" : "Iron Golem";
	}

	@Override
	public ChatColor getChatColor() {
		return ChatColor.WHITE;
	}

	@Override
	public int getMaxHealth() {
		int maxHealth = DarkzoneBalancing.getAttributeAsInt(getSubLevelType(), DarkzoneBalancing.Attribute.MOB_HEALTH);
		return isMinion() ? maxHealth * 4 : maxHealth;
	}

	@Override
	public double getDamage() {
		return DarkzoneBalancing.getAttributeAsInt(getSubLevelType(), DarkzoneBalancing.Attribute.MOB_DAMAGE);
	}

	@Override
	public int getSpeedAmplifier() {
		return isMinion() ? 4 : 1;
	}

	@Override
	public int getDroppedSouls() {
		return DarkzoneBalancing.getAttributeAsInt(getSubLevelType(), DarkzoneBalancing.Attribute.MOB_SOULS);
	}

	@Override
	public DropPool createDropPool() {
		return new DropPool()
				.addItem(ItemFactory.getItem(IronIngot.class).getItem(), 1);
	}

	@Override
	public PitNameTag createNameTag() {
		return new PitNameTag(this, PitNameTag.NameTagType.NAME_AND_HEALTH)
				.addMob(PitNameTag.RidingType.BABY_RABBIT)
				.addMob(PitNameTag.RidingType.BABY_RABBIT);
	}
}