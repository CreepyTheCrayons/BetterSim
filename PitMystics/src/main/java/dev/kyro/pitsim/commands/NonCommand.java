package dev.kyro.pitsim.commands;

import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.objects.Non;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NonCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		if(!player.isOp()) return false;

		for(World world : MapManager.currentMap.lobbies)
			new Non(NonManager.botIGNs.get((int) (Math.random() * NonManager.botIGNs.size())), world);

		return false;
	}
}