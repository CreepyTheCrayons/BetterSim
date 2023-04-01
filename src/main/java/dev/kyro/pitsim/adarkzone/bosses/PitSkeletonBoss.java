package dev.kyro.pitsim.adarkzone.bosses;

import dev.kyro.pitsim.adarkzone.DarkzoneBalancing;
import dev.kyro.pitsim.adarkzone.DropPool;
import dev.kyro.pitsim.adarkzone.PitBoss;
import dev.kyro.pitsim.adarkzone.SubLevelType;
import dev.kyro.pitsim.adarkzone.abilities.RuptureAbility;
import dev.kyro.pitsim.adarkzone.abilities.SnakeAbility;
import dev.kyro.pitsim.adarkzone.abilities.blockrain.HailAbility;
import dev.kyro.pitsim.adarkzone.abilities.minion.DefensiveMinionAbility;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PitSkeletonBoss extends PitBoss {

	public PitSkeletonBoss(Player summoner) {
		super(summoner);

		abilities(
				new RuptureAbility(1, 15, getDamage() * 0.5),
				new SnakeAbility(2, 20, getDamage() * 0.75, Material.QUARTZ_BLOCK, (byte) 0, Sounds.BONE_SNAKE),
				new HailAbility(2, 25, 100, getDamage() * 2.5),
				new DefensiveMinionAbility(SubLevelType.SKELETON, 1, 3, 6 * 20),
				null
		);
	}

	@Override
	public SubLevelType getSubLevelType() {
		return SubLevelType.SKELETON;
	}

	@Override
	public String getRawDisplayName() {
		return "Skeleton Boss";
	}

	@Override
	public ChatColor getChatColor() {
		return ChatColor.RED;
	}

	@Override
	public String getSkinName() {
		return "Skeleton";
	}

	@Override
	public double getMaxHealth() {
		return DarkzoneBalancing.getAttributeAsInt(getSubLevelType(), DarkzoneBalancing.Attribute.BOSS_HEALTH);
	}

	@Override
	public double getDamage() {
		return DarkzoneBalancing.getAttribute(getSubLevelType(), DarkzoneBalancing.Attribute.BOSS_DAMAGE);
	}

	@Override
	public double getReach() {
		return 3;
	}

	@Override
	public double getReachRanged() {
		return 0;
	}

	@Override
	public int getSpeedLevel() {
		return 1;
	}

	@Override
	public int getDroppedSouls() {
		return DarkzoneBalancing.getAttributeAsInt(getSubLevelType(), DarkzoneBalancing.Attribute.BOSS_SOULS);
	}

	@Override
	public DropPool createDropPool() {
		return new DropPool();
	}
}
