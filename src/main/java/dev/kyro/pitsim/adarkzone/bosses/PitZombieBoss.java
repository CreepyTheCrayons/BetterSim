package dev.kyro.pitsim.adarkzone.bosses;

import dev.kyro.pitsim.adarkzone.DropPool;
import dev.kyro.pitsim.adarkzone.PitBoss;
import dev.kyro.pitsim.adarkzone.SubLevelType;
import dev.kyro.pitsim.adarkzone.abilities.*;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PitZombieBoss extends PitBoss {

	public PitZombieBoss(Player summoner) {
		super(summoner);

		abilities(
//				new TrueDamageAbility(4),
				new RuptureAbility(0.3, 25, 8, 50),
				new PoundAbility(0.3, 5),
				new SnakeAbility(0.3, 15, 8,  Material.ICE, (byte) 0, Sounds.SNAKE_ICE),
				new SlamAbility(0.3, 25, 25,  25)
//				new BlockRainAbility(0.3, 10, 10,  Material.ANVIL, (byte) 0)
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
