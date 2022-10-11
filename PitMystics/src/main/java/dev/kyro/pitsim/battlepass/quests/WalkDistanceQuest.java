package dev.kyro.pitsim.battlepass.quests;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.battlepass.PassQuest;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class WalkDistanceQuest extends PassQuest {
	public static WalkDistanceQuest INSTANCE;
	public static Map<UUID, Long> distanceMap = new HashMap<>();

	public WalkDistanceQuest() {
		super("&f&lMarathon Runner", "distancewalked", QuestType.WEEKLY);
		INSTANCE = this;
	}

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					if(!distanceMap.containsKey(onlinePlayer.getUniqueId())) {
						distanceMap.put(onlinePlayer.getUniqueId(), (long) onlinePlayer.getStatistic(Statistic.WALK_ONE_CM) +
								onlinePlayer.getStatistic(Statistic.SPRINT_ONE_CM));
						continue;
					}
					long currentDistance = onlinePlayer.getStatistic(Statistic.WALK_ONE_CM) + onlinePlayer.getStatistic(Statistic.SPRINT_ONE_CM);
					long diff = currentDistance - distanceMap.get(onlinePlayer.getUniqueId());
					distanceMap.put(onlinePlayer.getUniqueId(), currentDistance);
					WalkDistanceQuest.INSTANCE.progressQuest(PitPlayer.getPitPlayer(onlinePlayer), diff);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 20);
	}

	@Override
	public ItemStack getDisplayItem(PitPlayer pitPlayer, QuestLevel questLevel, double progress) {
		ItemStack itemStack = new AItemStackBuilder(Material.WATCH)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Walk or sprint &f" + Misc.formatLarge(questLevel.getRequirement(pitPlayer) / 100) + " &7blocks",
						"",
						"&7Progress: &3" + Misc.formatLarge(progress / 100) + "&7/&3" + Misc.formatLarge(questLevel.getRequirement(pitPlayer) / 100) + " &8[" +
								AUtil.createProgressBar("|", ChatColor.AQUA, ChatColor.GRAY, 20,
								progress / questLevel.getRequirement(pitPlayer)) + "&8]",
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
	public List<QuestLevel> getWeeklyPossibleStates() {
		List<QuestLevel> questLevels = new ArrayList<>();
		questLevels.add(new QuestLevel(10_000 * 100, 100));
		return questLevels;
	}

	@Override
	public double getMultiplier(PitPlayer pitPlayer) {
		return 1.0;
	}
}
