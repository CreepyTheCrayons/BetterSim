package dev.kyro.pitsim.enums;

import dev.kyro.pitsim.mobs.PitCreeper;
import dev.kyro.pitsim.mobs.PitSkeleton;
import dev.kyro.pitsim.mobs.PitSpider;
import dev.kyro.pitsim.mobs.PitZombie;
import dev.kyro.pitsim.slayers.SkeletonBoss;
import dev.kyro.pitsim.slayers.ZombieBoss;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Arrays;
import java.util.List;

public enum SubLevel {
	ZOMBIE_CAVE(1, 30, new Location(Bukkit.getWorld("darkzone"), 56, 53, -95), NBTTag.ZOMBIE_FLESH, "&aRotten Flesh", "%pitsim_zombiecave%", 25, Arrays.asList(PitZombie.class), ZombieBoss.class),
	SKELETON_CAVE(2, 30, new Location(Bukkit.getWorld("darkzone"), 56, 44, -43), NBTTag.SKELETON_BONE, "&aBone", "%pitsim_skeletoncave%", 20, Arrays.asList(PitSkeleton.class), SkeletonBoss.class),
	SPIDER_CAVES(3, 30, new Location(Bukkit.getWorld("darkzone"), 102, 28, -69), NBTTag.SPIDER_EYE, "&aSpider Eye", "%pitsim_spidercave%", 20, Arrays.asList(PitSpider.class), SkeletonBoss.class),
	CREEPER_CAVES(4, 20, new Location(Bukkit.getWorld("darkzone"), 8, 32, -30), NBTTag.CREEPER_POWDER, "&aGunpowder", "%pitsim_creepercave%", 20, Arrays.asList(PitCreeper.class), SkeletonBoss.class);

//	MAGMA_CUBE,
//	WITHER_SKELETON,
//	IRON_GOLEM,
//	SPIDER,
//	CAVE_SPIDER,
//	ENDERMAN,
//	ZOMBIE_PIGMAN;

	public int level;
	public int maxMobs;
	public Location middle;
	public NBTTag bossItem;
	public String itemName;
	public String placeholder;
	public int radius;
	public List<Class> mobs;
	public Class boss;

	SubLevel(int level, int maxMobs, Location middle, NBTTag bossItem, String itemName, String placeholder, int radius, List<Class> mobs, Class Boss) {
		this.level = level;
		this.maxMobs = maxMobs;
		this.middle = middle;
		this.bossItem = bossItem;
		this.itemName = itemName;
		this.placeholder = placeholder;
		this.radius = radius;
		this.mobs = mobs;
		this.boss = boss;
	}


}
