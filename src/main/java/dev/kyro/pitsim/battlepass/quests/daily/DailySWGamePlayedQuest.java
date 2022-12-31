package dev.kyro.pitsim.battlepass.quests.daily;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.battlepass.PassQuest;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.events.MessageEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class DailySWGamePlayedQuest extends PassQuest {

	public DailySWGamePlayedQuest() {
		super("&f&lPlay Skywars", "dailyskywars", QuestType.DAILY);
	}

	@EventHandler
	public void onMessage(MessageEvent event) {
		PluginMessage message = event.getMessage();
		List<String> strings = message.getStrings();
		List<Integer> ints = message.getIntegers();
		if(strings.get(0).equals("SKYWARS PASS QUEST")) {
			UUID playerUUID = UUID.fromString(strings.get(1));

			Player player = Bukkit.getPlayer(playerUUID);
			if(!player.isOnline()) return;

			PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
			progressQuest(pitPlayer, ints.get(0));
		}
	}

	@Override
	public ItemStack getDisplayItem(PitPlayer pitPlayer, QuestLevel questLevel, double progress) {
		ItemStack itemStack = new AItemStackBuilder(Material.WOOL)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Get at least &f1 &7kill",
						"&7in &f" + Misc.formatLarge(questLevel.getRequirement(pitPlayer)) + " &7skywars games",
						"",
						"&7Progress: &3" + Misc.formatLarge(progress) + "&7/&3" + Misc.formatLarge(questLevel.getRequirement(pitPlayer)) + " &8[" +
								AUtil.createProgressBar("|", ChatColor.AQUA, ChatColor.GRAY, 20,
										progress / questLevel.getRequirement(pitPlayer)) + "&8]",
						"&7Reward: &3" + questLevel.rewardPoints + " &7Quest Points"
				))
				.getItemStack();
		return itemStack;
	}

	@Override
	public void createPossibleStates() {
		questLevels.add(new QuestLevel(2, 40));
	}

	@Override
	public double getMultiplier(PitPlayer pitPlayer) {
		return 1.0;
	}
}
