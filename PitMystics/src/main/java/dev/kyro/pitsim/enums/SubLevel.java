package dev.kyro.pitsim.enums;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.mobs.*;
import dev.kyro.pitsim.slayers.SkeletonBoss;
import dev.kyro.pitsim.slayers.ZombieBoss;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Arrays;
import java.util.List;

public enum SubLevel {
	ZOMBIE_CAVE(1, 20, new Location(Bukkit.getWorld("darkzone"), 327, 68, -143), NBTTag.ZOMBIE_FLESH, "&aRotten Flesh", "%pitsim_zombiecave%", 17, Arrays.asList(PitZombie.class), ZombieBoss.class),
	SKELETON_CAVE(2, 20, new Location(Bukkit.getWorld("darkzone"), 424, 53, -128), NBTTag.SKELETON_BONE, "&aBone", "%pitsim_skeletoncave%", 17, Arrays.asList(PitSkeleton.class), SkeletonBoss.class),
	SPIDER_CAVE(3, 20, new Location(Bukkit.getWorld("darkzone"), 463, 38, -72), NBTTag.SPIDER_EYE, "&aSpider Eye", "%pitsim_spidercave%", 17, Arrays.asList(PitSpider.class), SkeletonBoss.class),
	CREEPER_CAVE(4, 20, new Location(Bukkit.getWorld("darkzone"), 419, 26, -27), NBTTag.CREEPER_POWDER, "&aGunpowder", "%pitsim_creepercave%", 17, Arrays.asList(PitCreeper.class), SkeletonBoss.class),
	DEEP_SPIDER_CAVE(5, 20, new Location(Bukkit.getWorld("darkzone"), 342, 20, 15), NBTTag.CAVESPIDER_EYE, "&aFermented Spider Eye", "%pitsim_deepspidercave%", 17, Arrays.asList(PitCaveSpider.class), SkeletonBoss.class),
	MAGMA_CAVE(6, 20, new Location(Bukkit.getWorld("darkzone"), 235, 20, -23), NBTTag.MAGMACUBE_CREAM, "&aMagma Cream", "%pitsim_magmacave%", 17, Arrays.asList(PitMagmaCube.class), SkeletonBoss.class),
	PIGMEN_CAVE(7, 20, new Location(Bukkit.getWorld("darkzone"), 210, 20 ,-115), NBTTag.PIGMAN_PORK, "&aRaw Pork", "%pitsim_pigmencave%", 17, Arrays.asList(PitZombiePigman.class), SkeletonBoss.class);
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
