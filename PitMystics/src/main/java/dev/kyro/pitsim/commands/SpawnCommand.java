package dev.kyro.pitsim.commands;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.HopperManager;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.SpawnManager;
import dev.kyro.pitsim.controllers.objects.Hopper;
import dev.kyro.pitsim.helmetabilities.PhoenixAbility;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		Player player = (Player) sender;
		if(player.getWorld() == MapManager.getDarkzone()) {
			AOutput.error(player, "&c&lNOPE! &7You cannot spawn here.");
			return false;
		}
		SpawnManager.lastLocationMap.remove(player);
		Location teleportLoc = MapManager.currentMap.getSpawn(player.getWorld());
		player.teleport(teleportLoc);
		PhoenixAbility.alreadyActivatedList.remove(player.getUniqueId());
		for(Hopper hopper : HopperManager.hopperList) {
			if(player != hopper.target) continue;
			hopper.remove();
		}

		return false;
	}
}
