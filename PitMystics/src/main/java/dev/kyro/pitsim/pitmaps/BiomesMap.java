package dev.kyro.pitsim.pitmaps;

import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitMap;
import org.bukkit.Location;
import org.bukkit.World;

public class BiomesMap extends PitMap {
	public BiomesMap(String... worldNames) {
		super(worldNames);
	}

	@Override
	public Location getSpawn(World world) {
		if(!lobbies.contains(world) || (!MapManager.multiLobbies && world != MapManager.currentMap.firstLobby))
			return getSpawn(lobbies.get(0));
		return new Location(world, 0.5, 88, 8.5, -180, 0);
	}

	@Override
	public Location getNonSpawn(World world) {
		Location spawn = new Location(world, 0.5, 86, 0.5);
		spawn.setX(spawn.getX() + (Math.random() * 8 - 4));
		spawn.setZ(spawn.getZ() + (Math.random() * 8 - 4));
		return spawn;
	}

	@Override
	public Location getMid(World world) {
		return new Location(world, 0.5, 70, 0.5);
	}

	@Override
	public Location getUpgradeNPCSpawn(World world) {
		return new Location(world, 10.5, 88, 4.5, 90, 0);
	}

	@Override
	public Location getPrestigeNPCSpawn(World world) {
		return new Location(world, -12.5, 88, -1.5, -90, 0);
	}

	@Override
	public Location getKyroNPCSpawn(World world) {
		return new Location(world, 7.5, 92, -8.5, 22.5F, 11);
	}

	@Override
	public Location getWijiNPCSpawn(World world) {
		return new Location(world, 0.5, 92, -11.5, 31, 10);
	}

	@Override
	public Location getVnxNPCSpawn(World world) {
		return new Location(world, 2.5, 88, -8.5, 10, 0);
	}
}
