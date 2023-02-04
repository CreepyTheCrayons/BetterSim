package dev.kyro.pitsim.adarkzone.bosses;

import dev.kyro.pitsim.adarkzone.DropPool;
import dev.kyro.pitsim.adarkzone.PitBoss;
import dev.kyro.pitsim.adarkzone.SubLevelType;
import dev.kyro.pitsim.adarkzone.abilities.KnockbackAbility;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PitZombieBoss extends PitBoss {

	public PitZombieBoss(Player summoner) {
		super(summoner);

		abilities(
//				new TrueDamageAbility(4),
//				new StunAbility(0.1, 0),
//				new LaunchAbility(20)
				new KnockbackAbility(5)
		);
	}

	@Override
	public SubLevelType getSubLevelType() {
		return SubLevelType.ZOMBIE;
	}

	@Override
	public String getRawDisplayName() {
		return "Zombie Boss";
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
		return 20;
	}

	@Override
	public double getMeleeDamage() {
		return 10;
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
}
