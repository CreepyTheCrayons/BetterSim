package dev.kyro.pitsim.adarkzone.aaold;

import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.mobs.*;
import dev.kyro.pitsim.adarkzone.aaold.slayers.SkeletonBossOld;
import dev.kyro.pitsim.adarkzone.aaold.slayers.ZombieBossOld;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Arrays;
import java.util.List;

public enum OldSubLevel {
	ZOMBIE_CAVE(1, 20, new Location(Bukkit.getWorld("darkzone"), 327, 68, -143),
			NBTTag.ZOMBIE_FLESH, "&aRotten Flesh", "%pitsim_zombiecave%", 17,
			Arrays.asList(OldPitZombie.class), ZombieBossOld.class, 12),
	SKELETON_CAVE(2, 20, new Location(Bukkit.getWorld("darkzone"), 424, 53, -128),
			NBTTag.SKELETON_BONE, "&aBone", "%pitsim_skeletoncave%", 17,
			Arrays.asList(OldPitSkeleton.class), SkeletonBossOld.class, 11),
	SPIDER_CAVE(3, 20, new Location(Bukkit.getWorld("darkzone"), 463, 38, -72),
			NBTTag.SPIDER_EYE, "&aSpider Eye", "%pitsim_spidercave%", 17,
			Arrays.asList(OldPitSpider.class), SkeletonBossOld.class, 10),
	CREEPER_CAVE(4, 20, new Location(Bukkit.getWorld("darkzone"), 419, 26, -27),
			NBTTag.CREEPER_POWDER, "&aGunpowder", "%pitsim_creepercave%", 17,
			Arrays.asList(OldPitCreeper.class), SkeletonBossOld.class, 9),
	DEEP_SPIDER_CAVE(5, 20, new Location(Bukkit.getWorld("darkzone"), 342, 20, 15),
			NBTTag.CAVESPIDER_EYE, "&aFermented Spider Eye", "%pitsim_deepspidercave%", 17,
			Arrays.asList(OldPitCaveSpider.class), SkeletonBossOld.class, 8),
	MAGMA_CAVE(6, 20, new Location(Bukkit.getWorld("darkzone"), 235, 20, -23),
			NBTTag.MAGMACUBE_CREAM, "&aMagma Cream", "%pitsim_magmacave%", 17,
			Arrays.asList(OldPitMagmaCube.class), SkeletonBossOld.class, 7),
	PIGMEN_CAVE(7, 20, new Location(Bukkit.getWorld("darkzone"), 210, 20, -115),
			NBTTag.PIGMAN_PORK, "&aRaw Pork", "%pitsim_pigmencave%", 17,
			Arrays.asList(OldPitZombiePigman.class), SkeletonBossOld.class, 6),
	WITHER_CAVE(8, 20, new Location(Bukkit.getWorld("darkzone"), 247, 21, -174),
			NBTTag.WITHER_SKELETON_SKULL, "&aWither Skull", "%pitsim_withercave%", 17,
			Arrays.asList(OldPitWitherSkeleton.class), SkeletonBossOld.class, 5),
	GOLEM_CAVE(9, 20, new Location(Bukkit.getWorld("darkzone"), 313, 20, -217),
			NBTTag.GOLEM_INGOT, "&aIron Ingot", "%pitsim_golemcave%", 17,
			Arrays.asList(OldPitIronGolem.class), SkeletonBossOld.class, 4),
	ENDERMAN_CAVE(10, 20, new Location(Bukkit.getWorld("darkzone"), 388, 20, -226),
			NBTTag.ENDERMAN_PEARL, "&aEnder Pearl", "%pitsim_endermancave%", 17,
			Arrays.asList(OldPitEnderman.class), SkeletonBossOld.class, 3);

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
	public int spawnBossItemCount;

	OldSubLevel(int level, int maxMobs, Location middle, NBTTag bossItem, String itemName, String placeholder,
				int radius, List<Class> mobs, Class Boss, int spawnBossItemCount) {
		this.level = level;
		this.maxMobs = maxMobs;
		this.middle = middle;
		this.bossItem = bossItem;
		this.itemName = itemName;
		this.placeholder = placeholder;
		this.radius = radius;
		this.mobs = mobs;
		this.boss = boss;
		this.spawnBossItemCount = spawnBossItemCount;
	}

	public static OldSubLevel getLevel(int subLevel) {
		for(OldSubLevel value : values()) {
			if(value.level == subLevel) return value;
		}
		return null;
	}


}
