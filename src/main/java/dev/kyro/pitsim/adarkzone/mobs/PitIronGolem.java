package dev.kyro.pitsim.adarkzone.mobs;

import dev.kyro.pitsim.adarkzone.DropPool;
import dev.kyro.pitsim.adarkzone.PitMob;
import dev.kyro.pitsim.adarkzone.PitNameTag;
import dev.kyro.pitsim.aitems.mobdrops.IronIngot;
import dev.kyro.pitsim.controllers.ItemFactory;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.IronGolem;

public class PitIronGolem extends PitMob {

	public PitIronGolem(Location spawnLocation) {
		super(spawnLocation);
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
		return "Iron Golem";
	}

	@Override
	public ChatColor getChatColor() {
		return ChatColor.WHITE;
	}

	@Override
	public int getMaxHealth() {
		return 180;
	}

	@Override
	public int getSpeedAmplifier() {
		return 1;
	}

	@Override
	public double getOffsetHeight() {
		return 2.0;
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