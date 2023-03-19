package dev.kyro.pitsim.npcs;

import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitNPC;
import dev.kyro.pitsim.inventories.TaintedShopGUI;
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
		return new Location(MapManager.getDarkzone(), 214, 91, -113, 25, 0);
	}

	@Override
	public void createNPC(Location location) {
		spawnPlayerNPC("", "debrided", location, false);
	}

	@Override
	public void onClick(Player player) {
		TaintedShopGUI taintedShopGUI = new TaintedShopGUI(player);
		taintedShopGUI.open();
	}
}
