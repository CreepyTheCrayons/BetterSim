package dev.kyro.pitsim.adarkzone.bosses;

import dev.kyro.pitsim.adarkzone.PitBoss;
import dev.kyro.pitsim.adarkzone.SubLevelType;
import dev.kyro.pitsim.adarkzone.abilities.KnockbackAbility;
import dev.kyro.pitsim.adarkzone.abilities.StunAbility;
import dev.kyro.pitsim.adarkzone.abilities.TrueDamageAbility;
import org.bukkit.entity.Player;

public class PitZombieBoss extends PitBoss {

	public PitZombieBoss(Player summoner) {
		super(summoner);

		abilities(
				new TrueDamageAbility(4),
				new StunAbility(0.1, 80),
//				new LaunchAbility(20)
				new KnockbackAbility(5)
		);
	}

	@Override
	public SubLevelType getSubLevelType() {
		return SubLevelType.ZOMBIE;
	}

	@Override
	public String getName() {
		return "&cZombie Boss";
	}

	@Override
	public String getSkinName() {
		return "Zombie";
	}

	@Override
	public int getMaxHealth() {
		return 150;
	}

	@Override
	public double getMeleeDamage() {
		return 0;
	}

	@Override
	public double getReach() {
		return 3;
	}

	@Override
	public double getReachRanged() {
		return 0;
	}
}
