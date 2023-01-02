package dev.kyro.pitsim.adarkzone;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public abstract class SubLevel {
	private Location middle;
	private Location bossSpawnLocation;

//	Mob related fields
	public List<PitMob> mobs = new ArrayList<>();

//	Boss related fields
	public boolean isBossSpawned = false;
	public PitBoss pitBoss;

	public SubLevel() {
		this.middle = createMiddle();
		this.bossSpawnLocation = middle.clone().add(0, 2, 0);
	}

	//	Mobs
	public abstract Class<? extends PitMob> getMob();
	public abstract int getMaxMobs();
	public abstract Location createMiddle();
	public abstract int getSpawnRadius();

//	Bosses
	public abstract Class<? extends PitBoss> getBoss();
	public abstract int getRequiredDropsToSpawn();

	public void tick() {
		if(Math.random() < 0.75) return;
		int newMobsNeeded = getMaxMobs() - mobs.size();
		for(int i = 0; i < Math.min(newMobsNeeded, 3); i++) spawnMob();
	}

	public void spawnMob() {
		try {
			Constructor<? extends PitMob> constructor = getMob().getConstructor(Player.class);
			PitMob pitMob = constructor.newInstance();
		} catch(Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	public void spawnBoss(Player summoner) {
		if(isBossSpawned) throw new RuntimeException();
		try {
			Constructor<? extends PitBoss> constructor = getBoss().getConstructor(Player.class);
			pitBoss = constructor.newInstance(summoner);
		} catch(Exception exception) {
			throw new RuntimeException(exception);
		}
		isBossSpawned = true;
		disableMobs();
	}

	public void bossDeath() {
		isBossSpawned = false;
	}

	public void disableMobs() {
		for(PitMob mob : mobs) mob.despawn();
		mobs.clear();
	}

	public Location getMiddle() {
		return middle;
	}

	public Location getBossSpawnLocation() {
		return bossSpawnLocation;
	}
}
