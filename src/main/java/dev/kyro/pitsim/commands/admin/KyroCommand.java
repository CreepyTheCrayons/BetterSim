package dev.kyro.pitsim.commands.admin;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.commands.ACommand;
import dev.kyro.arcticapi.commands.AMultiCommand;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.DarkzoneLeveling;
import dev.kyro.pitsim.ahelp.HelpManager;
import dev.kyro.pitsim.aserverstatistics.StatisticDataChunk;
import dev.kyro.pitsim.aserverstatistics.StatisticsManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class KyroCommand extends ACommand {
	public KyroCommand(AMultiCommand base, String executor) {
		super(base, executor);
	}

	@Override
	public void execute(CommandSender sender, Command command, String alias, List<String> args) {
		if(!(sender instanceof Player)) return;
		Player player = (Player) sender;
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(!Misc.isKyro(player.getUniqueId())) {
			AOutput.error(player, "&c&lERROR!&7 You have to be &9Kyro &7to do this");
			return;
		}

		if(args.isEmpty()) {
			sendHelpMessage(player);
			return;
		}

		String subCommand = args.get(0).toLowerCase();
		if(subCommand.equals("sync")) {
			AOutput.send(player, "&9&lAI!&7 Updating Dialogflow model");
			new Thread(HelpManager::updateIntentsAndPages).start();
		} else if(subCommand.equals("clear")) {
			AOutput.send(player, "&9&lAI!&7 Clearing saved Dialogflow intent requests");
			HelpManager.clearStoredData();
		} else if(subCommand.equals("stats")) {
			StatisticDataChunk dataChunk = StatisticsManager.getDataChunk();
			dataChunk.send();
			StatisticsManager.resetDataChunk();
			AOutput.send(player, "&9&lDEV!&7 Sending statistics to proxy");
		} else if(subCommand.equals("altarxp")) {
			pitPlayer.darkzoneData.altarXP = 0;
			new BukkitRunnable() {
				@Override
				public void run() {
					DarkzoneLeveling.giveXP(pitPlayer, 100);
				}
			}.runTaskTimer(PitSim.INSTANCE, 0L, 10L);
		} else if(subCommand.equals("lockitem")) {
			ItemStack itemStack = player.getItemInHand();
			NBTItem nbtItem = new NBTItem(itemStack, true);
			nbtItem.setBoolean(NBTTag.IS_LOCKED.getRef(), true);
			player.setItemInHand(itemStack);
			player.updateInventory();
			AOutput.send(player, "&c&lLOCKED!&7 This item has been locked!");
			Sounds.SUCCESS.play(player);
		} else {
			sendHelpMessage(player);
		}
	}

	@Override
	public List<String> getTabComplete(Player player, String current, List<String> args) {
		return null;
	}

	public void sendHelpMessage(Player player) {
		AOutput.error(player, "&c&lERROR!&7 Usage: <sync|clear|stats|altarxp|lockitem>");
	}
}
