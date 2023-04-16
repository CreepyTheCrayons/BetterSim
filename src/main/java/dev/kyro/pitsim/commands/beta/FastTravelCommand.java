package dev.kyro.pitsim.commands.beta;

import dev.kyro.arcticapi.commands.ACommand;
import dev.kyro.arcticapi.commands.AMultiCommand;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.FastTravelGUI;
import dev.kyro.pitsim.controllers.CombatManager;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class FastTravelCommand extends ACommand {
	public FastTravelCommand(AMultiCommand base, String executor) {
		super(base, executor);
	}

	@Override
	public void execute(CommandSender sender, Command command, String alias, List<String> args) {
		if(!(sender instanceof Player)) return;
		Player player = (Player) sender;

		if(!player.isOp() && !PitSim.isDev() && CombatManager.isInCombat(player)) {
			AOutput.error(player, "&c&lERROR!&7 You cannot use this while in combat!");
			Sounds.NO.play(player);
			return;
		}

		FastTravelGUI fastTravelGUI = new FastTravelGUI(player);
		fastTravelGUI.open();
	}

	@Override
	public List<String> getTabComplete(Player player, String current, List<String> args) {
		return null;
	}
}
