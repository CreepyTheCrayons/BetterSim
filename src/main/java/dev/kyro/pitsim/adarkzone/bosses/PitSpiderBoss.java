package dev.kyro.pitsim.adarkzone.bosses;

import dev.kyro.pitsim.adarkzone.DropPool;
import dev.kyro.pitsim.adarkzone.PitBoss;
import dev.kyro.pitsim.adarkzone.SubLevelType;
import dev.kyro.pitsim.adarkzone.abilities.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PitSpiderBoss extends PitBoss {

	public PitSpiderBoss(Player summoner) {
		super(summoner);

		abilities(
				new SlamAbility(2, 40, 40, 3),
				new CollapseAbility(2, 5, 5, 20, 20),
				new SpiderMinionAbility(1, 2, 10,  5)
		);
	}

	@Override
	public SubLevelType getSubLevelType() {
		return SubLevelType.SPIDER;
	}

	@Override
	public String getRawDisplayName() {
		return "Spider Boss";
	}

	@Override
	public ChatColor getChatColor() {
		return ChatColor.RED;
	}

	@Override
	public String getSkinName() {
		return "wiji1";
	}

	@Override
	public int getMaxHealth() {
		return 300;
	}

	@Override
	public double getMeleeDamage() {
		return 30;
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
	public DropPool createDropPool() {
		return new DropPool();
	}

	@Override
	public int getSpeedLevel() {
		return 3;
	}
}
