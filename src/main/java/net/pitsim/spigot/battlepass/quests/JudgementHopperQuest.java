package net.pitsim.spigot.battlepass.quests;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import net.pitsim.spigot.battlepass.PassQuest;
import net.pitsim.spigot.controllers.HopperManager;
import net.pitsim.spigot.controllers.PlayerManager;
import net.pitsim.spigot.controllers.objects.Hopper;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.events.KillEvent;
import net.pitsim.spigot.misc.Formatter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

public class JudgementHopperQuest extends PassQuest {

	public JudgementHopperQuest() {
		super("&e&lPit is Fine", "judgementhopper", QuestType.WEEKLY);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onKill(KillEvent killEvent) {
		Hopper hopper = HopperManager.getHopper(killEvent.getKillerPlayer());
		if(hopper == null || !PlayerManager.isRealPlayer(killEvent.getDeadPlayer())) return;

		Player player = hopper.judgementPlayer;
		if(player == null) return;
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		progressQuest(pitPlayer, 1);
	}

	@Override
	public ItemStack getDisplayStack(PitPlayer pitPlayer, QuestLevel questLevel, double progress) {
		ItemStack itemStack = new AItemStackBuilder(Material.HOPPER)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Kill a player with the hopper that",
						"&7spawns when you use &9Judgement",
						"&7helmet ability &e" + Formatter.formatLarge(questLevel.getRequirement(pitPlayer)) + " &7times",
						"",
						"&7Progress: &3" + Formatter.formatLarge(progress) + "&7/&3" + Formatter.formatLarge(questLevel.getRequirement(pitPlayer)) + " &8[" +
								AUtil.createProgressBar("|", ChatColor.AQUA, ChatColor.GRAY, 20, progress / questLevel.getRequirement(pitPlayer)) + "&8]",
						"&7Reward: &3" + questLevel.rewardPoints + " &7Quest Points"
				))
				.getItemStack();
		return itemStack;
	}

	@Override
	public QuestLevel getDailyState() {
		return null;
	}

	@Override
	public void createPossibleStates() {
		questLevels.add(new QuestLevel(1, 100));
		questLevels.add(new QuestLevel(2, 150));
		questLevels.add(new QuestLevel(3, 200));
	}

	@Override
	public double getMultiplier(PitPlayer pitPlayer) {
		return 1.0;
	}
}
