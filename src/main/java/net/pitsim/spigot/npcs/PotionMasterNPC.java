package net.pitsim.spigot.npcs;

import net.pitsim.spigot.controllers.MapManager;
import net.pitsim.spigot.controllers.objects.PitNPC;
import net.pitsim.spigot.inventories.PotionMasterGUI;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class PotionMasterNPC extends PitNPC {

	public PotionMasterNPC(List<World> worlds) {
		super(worlds);
	}

	@Override
	public Location getRawLocation() {
		return new Location(MapManager.getDarkzone(), 216.5, 91, -102.5, 25, 0);
	}

	@Override
	public void createNPC(Location location) {
		spawnPlayerNPC("&d&lPOTIONS", "Wiizard", location, false);
	}

	@Override
	public void onClick(Player player) {
		PotionMasterGUI potionMasterGUI = new PotionMasterGUI(player);
		potionMasterGUI.open();
	}
}
