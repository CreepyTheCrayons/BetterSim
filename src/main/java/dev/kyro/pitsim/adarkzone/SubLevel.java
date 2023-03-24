package dev.kyro.pitsim.adarkzone;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.aitems.StaticPitItem;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.enums.MobStatus;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftCreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SubLevel {
	public static long tick = 0;

	public SubLevelType subLevelType;

	private Location middle;
	private List<Location> spawnableLocations = new ArrayList<>();

//	Boss related fields
	public Class<? extends PitBoss> bossClass;
	public PitBoss pitBoss;
	private EntityType spawnerMob;
	private boolean isBossSpawned = false;
	private Class<? extends StaticPitItem> spawnItemClass;
	private int currentDrops = 0;
	private int requiredDropsToSpawn;
	private int maxMinionsPerPlayer;

	//	Mob related fields
	private Class<? extends PitMob> mobClass;
	public List<PitMob> mobs = new ArrayList<>();
	public int maxMobs;
	public int spawnRadius;
	public MobTargetingSystem mobTargetingSystem = new MobTargetingSystem(this);

	public SubLevel(SubLevelType subLevelType, Class<? extends PitBoss> bossClass, Class<? extends PitMob> mobClass,
					EntityType spawnerMob, Class<? extends StaticPitItem> spawnItemClass,
					Location middle, int maxMobs, int spawnRadius, int requiredDropsToSpawn, int maxMinionsPerPlayer) {
		this.subLevelType = subLevelType;
		this.bossClass = bossClass;
		this.spawnerMob = spawnerMob;
		this.mobClass = mobClass;
		this.spawnItemClass = spawnItemClass;
		this.middle = middle;
		this.maxMobs = maxMobs;
		this.spawnRadius = spawnRadius;
		this.requiredDropsToSpawn = requiredDropsToSpawn;
		this.maxMinionsPerPlayer = maxMinionsPerPlayer;

//		Visualize spawnable spaces
//		new BukkitRunnable() {
//			@Override
//			public void run() {
//				for(Location spawnableLocation : spawnableLocations) {
//					spawnableLocation.getWorld().playEffect(spawnableLocation, Effect.HAPPY_VILLAGER, 1);
//				}
//			}
//		}.runTaskTimer(PitSim.INSTANCE, 0L, 20L);
	}

	public void init() {
		identifySpawnableLocations();
		setSpawner();
		createHologram();
	}

	public void tick() {
		if(tick % 4 == 0) {
			for(PitMob pitMob : new ArrayList<>(mobs)) {
				if(pitMob.getMob().getLocation().distance(getMiddle()) < spawnRadius + 5) continue;
				pitMob.remove();
			}
		}

		if(Math.random() < 0.25) {
			int newMobsNeeded = maxMobs - mobs.size();
			for(int i = 0; i < Math.min(newMobsNeeded, 3); i++) {
				if (!isBossSpawned) spawnMob(null, MobStatus.STANDARD);
			}
		}

		mobTargetingSystem.assignTargets();
	}

	public void identifySpawnableLocations() {
		for(int x = -spawnRadius; x < spawnRadius + 1; x++) {
			for(int z = -spawnRadius; z < spawnRadius + 1; z++) {
				Location location = new Location(MapManager.getDarkzone(), middle.getBlockX() + x + 0.5, middle.getBlockY(), middle.getBlockZ() + z + 0.5);
				if(location.distance(middle) > spawnRadius) continue;
				location.add(0, -3, 0);
				if(!isSpawnableLocation(location)) continue;
				spawnableLocations.add(location);
			}
		}
	}

	public void setSpawner() {
		Block spawnerBlock = middle.getBlock();
		spawnerBlock.setType(Material.MOB_SPAWNER);
		CraftCreatureSpawner spawner = (CraftCreatureSpawner) spawnerBlock.getState();
		spawner.setSpawnedType(spawnerMob);
		if(getSubLevelType() == SubLevelType.WITHER_SKELETON) {
			NBTTagCompound nbtTagCompound = new NBTTagCompound();
			nbtTagCompound.set("SpawnData", new NBTTagCompound());
			nbtTagCompound.setString("EntityId", "Skeleton");
			NBTTagCompound spawnData = (NBTTagCompound) nbtTagCompound.get("SpawnData");
			spawnData.setInt("SkeletonType", 1);
			spawner.getTileEntity().getSpawner().a(nbtTagCompound);
		}
		spawner.setDelay(Integer.MAX_VALUE);
		spawner.update();
	}

	public void createHologram() {
		Hologram hologram = HologramsAPI.createHologram(PitSim.INSTANCE, new Location(
				getMiddle().getWorld(),
				getMiddle().getX() + 0.5,
				getMiddle().getY() + 1.6,
				getMiddle().getZ() + 0.5));
		hologram.setAllowPlaceholders(true);
		hologram.appendTextLine(ChatColor.translateAlternateColorCodes('&',
				"&7Place " + ItemFactory.getItem(getSpawnItemClass()).getName()));
		hologram.appendTextLine("{fast}%sublevel_" + getIdentifier() + "%");
		DarkzoneManager.holograms.add(hologram);
	}

	public Location getMobSpawnLocation() {
		return spawnableLocations.get(new Random().nextInt(spawnableLocations.size()));
	}

	public boolean isSpawnableLocation(Location location) {
		boolean canSpawn = false;

		for(int i = 0; i < 6; i++) {
			Block blockBelow = location.clone().add(0, -1, 0).getBlock();
			Block block = location.getBlock();
			Block blockAbove = location.clone().add(0, 1, 0).getBlock();
			if(blockBelow.getType() == Material.MOB_SPAWNER) return false;
			if(blockBelow.getType() == Material.AIR || block.getType() != Material.AIR || blockAbove.getType() != Material.AIR) {
				location.add(0, 1, 0);
				continue;
			}
			canSpawn = true;
			break;
		}

		if(!canSpawn) return false;
		boolean foundCeiling = false;
		for(int i = 0; i < 15; i++) {
			Block myBlock = location.clone().add(0, i + 2, 0).getBlock();
			if(myBlock.getType() == Material.AIR) continue;
			foundCeiling = true;
			break;
		}
		return foundCeiling;
	}

	public void spawnMob(Location location, MobStatus status) {
		PitMob pitMob;
		try {
			Constructor<? extends PitMob> constructor = mobClass.getConstructor(Location.class, MobStatus.class);
			if(location == null) location = getMobSpawnLocation();
			if(!location.getChunk().isLoaded()) return;
			pitMob = constructor.newInstance(location, status);
		} catch(Exception exception) {
			exception.printStackTrace();
			return;
		}
		mobs.add(pitMob);
	}

	public void spawnBoss(Player summoner) {
		if(isBossSpawned) throw new RuntimeException();
		removeMobs();
		try {
			Constructor<? extends PitBoss> constructor = bossClass.getConstructor(Player.class);
			pitBoss = constructor.newInstance(summoner);
		} catch(Exception exception) {
			throw new RuntimeException(exception);
		}
		isBossSpawned = true;
	}

	public void bossDeath() {
		removeMobs();
		isBossSpawned = false;
	}

	public void removeMobs() {
		for(PitMob mob : new ArrayList<>(mobs)) mob.remove();
	}

	public Class<? extends StaticPitItem> getSpawnItemClass() {
		return spawnItemClass;
	}

	public SubLevelType getSubLevelType() {
		return subLevelType;
	}

	public boolean isBossSpawned() {
		return isBossSpawned;
	}

	public int getCurrentDrops() {
		return currentDrops;
	}

	public void setCurrentDrops(int currentDrops) {
		this.currentDrops = currentDrops;
	}

	public int getRequiredDropsToSpawn() {
		return requiredDropsToSpawn;
	}

	public Location getMiddle() {
		return middle;
	}

	public String getIdentifier() {
		return subLevelType.identifer;
	}

	public Location getBossSpawnLocation() {
		return getMiddle().clone().add(0, 1.5, 0);
	}

	public Class<? extends PitMob> getMobClass() {
		return mobClass;
	}

	public int getMaxMinionsPerPlayer() {
		return maxMinionsPerPlayer;
	}

	public int getIndex() {
		return getSubLevelType().getIndex();
	}
}
