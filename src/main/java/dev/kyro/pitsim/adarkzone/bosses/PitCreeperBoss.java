package dev.kyro.pitsim.adarkzone.bosses;

import dev.kyro.pitsim.adarkzone.BossManager;
import dev.kyro.pitsim.adarkzone.DropPool;
import dev.kyro.pitsim.adarkzone.PitBoss;
import dev.kyro.pitsim.adarkzone.SubLevelType;
import dev.kyro.pitsim.adarkzone.abilities.ComboAbility;
import dev.kyro.pitsim.adarkzone.abilities.GenericMinionAbility;
import dev.kyro.pitsim.adarkzone.abilities.LandMineAbility;
import dev.kyro.pitsim.adarkzone.abilities.TNTAbility;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PitCreeperBoss extends PitBoss {

	public PitCreeperBoss(Player summoner) {
		super(summoner);

		abilities(
				new GenericMinionAbility(1, SubLevelType.CREEPER, 1, 2),
				new TNTAbility(2, 1),
				new LandMineAbility(2, 3, 20, 20 * 45, 20),
				new ComboAbility(5, 1)
		);
	}

	@Override
	public SubLevelType getSubLevelType() {
		return SubLevelType.CREEPER;
	}

	@Override
	public String getRawDisplayName() {
		return "Creeper Boss";
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
		return BossManager.getHealth(getSubLevelType());
	}

	@Override
	public double getMeleeDamage() {
		return BossManager.getDamage(getSubLevelType());
	}

	@Override
	public double getReach() {
		return 2.5;
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
		return 6;
	}
}
