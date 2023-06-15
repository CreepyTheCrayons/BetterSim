package net.pitsim.spigot.adarkzone.bosses;

import net.pitsim.spigot.adarkzone.DarkzoneBalancing;
import net.pitsim.spigot.adarkzone.DropPool;
import net.pitsim.spigot.adarkzone.PitBoss;
import net.pitsim.spigot.adarkzone.SubLevelType;
import net.pitsim.spigot.adarkzone.abilities.ComboAbility;
import net.pitsim.spigot.adarkzone.abilities.minion.GenericMinionAbility;
import net.pitsim.spigot.adarkzone.abilities.PoundAbility;
import net.pitsim.spigot.adarkzone.abilities.PullAbility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

public class PitZombieBoss extends PitBoss {

	public PitZombieBoss(Player summoner) {
		super(summoner);

		abilities(
				new GenericMinionAbility(1, SubLevelType.ZOMBIE, 2, 8),
				new PullAbility(2, 20, new MaterialData(Material.DIRT, (byte) 0)),
				new PoundAbility(2, 5),

				new ComboAbility(8, 5, getDamage() * 0.2),
				null
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
		return "Zombie";
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
		return 2;
	}

	@Override
	public int getDroppedSouls() {
		return DarkzoneBalancing.getAttributeAsRandomInt(getSubLevelType(), DarkzoneBalancing.Attribute.BOSS_SOULS);
	}

	@Override
	public DropPool createDropPool() {
		return new DropPool();
	}
}
