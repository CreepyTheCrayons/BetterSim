package net.pitsim.spigot.npcs;

import net.pitsim.spigot.controllers.MapManager;
import net.pitsim.spigot.controllers.objects.PitNPC;
import net.pitsim.spigot.inventories.TaintedShopGUI;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class TaintedShopNPC extends PitNPC {

	public TaintedShopNPC(List<World> worlds) {
		super(worlds);
	}

	@Override
	public Location getRawLocation() {
		return new Location(MapManager.getDarkzone(), 201, 91, -84.7, 180, 0);
	}

	@Override
	public void createNPC(Location location) {
		spawnPlayerNPC("", "merchant", location, false);
	}

	@Override
	public void onClick(Player player) {
		TaintedShopGUI taintedShopGUI = new TaintedShopGUI(player);
		taintedShopGUI.open();
	}
}
