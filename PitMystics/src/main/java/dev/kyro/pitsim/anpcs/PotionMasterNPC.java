package dev.kyro.pitsim.anpcs;

import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitNPC;
import dev.kyro.pitsim.inventories.PotionMasterGUI;
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
		return new Location(MapManager.getDarkzone(), 195.5, 91, -84.5, -147, 0);
	}

	@Override
	public void createNPC(Location location) {
		spawnPlayerNPC("&d&lPOTIONS", "Wiizard", location);
	}

	@Override
	public void onClick(Player player) {
		PotionMasterGUI potionMasterGUI = new PotionMasterGUI(player);
		potionMasterGUI.open();
	}
}
