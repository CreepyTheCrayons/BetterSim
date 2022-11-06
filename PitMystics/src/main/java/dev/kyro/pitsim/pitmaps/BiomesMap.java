package dev.kyro.pitsim.pitmaps;

import dev.kyro.pitsim.controllers.BoosterManager;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.Booster;
import dev.kyro.pitsim.controllers.objects.PitMap;
import org.bukkit.Location;
import org.bukkit.World;

public class BiomesMap extends PitMap {
	public BiomesMap(String worldName) {
		super(worldName);
	}

	public static Location mid = new Location(null, 0.5, 70, 0.5);

	@Override
	public Location getSpawn() {
		return new Location(world, 0.5, 88, 8.5, -180, 0);
	}

	@Override
	public Location getNonSpawn() {
		Location spawn = new Location(world, 0.5, 86, 0.5);
		spawn.setX(spawn.getX() + (Math.random() * 6 - 3));
		spawn.setZ(spawn.getZ() + (Math.random() * 6 - 3));

		Booster booster = BoosterManager.getBooster("chaos");
		if(booster.isActive()) {
			spawn.add(0, -10, 0);
		} else if(Math.random() < 0.5) {
			spawn.add(0, -5, 0);
		}
		return spawn;
	}

	@Override
	public Location getDarkzoneJoinSpawn() {
		return new Location(world, -67, 72, 3, -90, 0);
	}

	@Override
	public int getTeleportAdd() { return 3; }

	@Override
	public int getTeleportY() { return 72;}

	@Override
	public String getOpenSchematic() { return "plugins/WorldEdit/schematics/doorOpen.schematic"; }

	@Override
	public String getClosedSchematic() { return "plugins/WorldEdit/schematics/doorClosed.schematic"; }

	@Override
	public Location getSchematicPaste() { return new Location(world, -67, 72, 3); }

	@Override
	public Location getMid() {
		Location location = mid.clone();
		location.setWorld(world);
		return location;
	}

	@Override
	public Location getUpgradeNPCSpawn() {
		return new Location(world, 10.5, 88, 4.5, 90, 0);
	}

	@Override
	public Location getPrestigeNPCSpawn() {
		return new Location(world, -12.5, 88, -1.5, -90, 0);
	}

	@Override
	public Location getKyroNPCSpawn() {
		return new Location(world, 7.5, 92, -8.5, 22.5F, 11);
	}

	@Override
	public Location getWijiNPCSpawn() {
		return new Location(world, 0.5, 92, -11.5, 31, 10);
	}

	@Override
	public Location getSplkNPCSpawn() {
		return new Location(world, 8.5, 90, -7.5, 45, 0);
	}

	@Override
	public Location getVnxNPCSpawn() {
		return new Location(world, 2.5, 88, -8.5, 10, 0);
	}

	@Override
	public Location getKeeperNPCSpawn() {
		return new Location(world, -2.5, 88, -10, 10, 0);
	}

	@Override
	public Location getKitNPCSpawn(World world) {
		return new Location(world, -2.5, 90, 12.5, -145, 15);
	}
}
