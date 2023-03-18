package dev.kyro.pitsim.adarkzone.bosses;

import dev.kyro.pitsim.adarkzone.*;
import dev.kyro.pitsim.adarkzone.abilities.CageAbility;
import dev.kyro.pitsim.adarkzone.abilities.ChargeAbility;
import dev.kyro.pitsim.adarkzone.abilities.WolfMinionAbility;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PitWolfBoss extends PitBoss {

	public PitWolfBoss(Player summoner) {
		super(summoner);

		abilities(
				new ChargeAbility(2),
				new CageAbility(1, 40, 5),
				new WolfMinionAbility(3, 5, 50)
		);
	}

	@Override
	public SubLevelType getSubLevelType() {
		return SubLevelType.WOLF;
	}

	@Override
	public String getRawDisplayName() {
		return "Wolf Boss";
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
	public double getMaxHealth() {
		return DarkzoneBalancing.getAttribute(getSubLevelType(), DarkzoneBalancing.Attribute.BOSS_HEALTH) * 0.5;
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
		return 4;
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
