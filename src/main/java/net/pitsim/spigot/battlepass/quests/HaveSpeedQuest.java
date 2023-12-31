package net.pitsim.spigot.battlepass.quests;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.battlepass.PassQuest;
import net.pitsim.spigot.controllers.MapManager;
import net.pitsim.spigot.controllers.SpawnManager;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.misc.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class HaveSpeedQuest extends PassQuest {
	public static HaveSpeedQuest INSTANCE;

	public HaveSpeedQuest() {
		super("&f&lZooooom", "havespeed", QuestType.WEEKLY);
		INSTANCE = this;
	}

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				if(!HaveSpeedQuest.INSTANCE.isQuestActive()) return;
				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					if(!onlinePlayer.hasPotionEffect(PotionEffectType.SPEED) ||
							SpawnManager.isInSpawn(onlinePlayer) || MapManager.inDarkzone(onlinePlayer))
						continue;
					PitPlayer pitPlayer = PitPlayer.getPitPlayer(onlinePlayer);
					HaveSpeedQuest.INSTANCE.progressQuest(pitPlayer, 1);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0, 20);
	}

	@Override
	public ItemStack getDisplayStack(PitPlayer pitPlayer, QuestLevel questLevel, double progress) {
		ItemStack itemStack = new AItemStackBuilder(Material.SUGAR)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Have speed for &f" + Formatter.formatLarge(questLevel.getRequirement(pitPlayer) / 60) + " &7minutes",
						"&7(not counting in spawn or in",
						"&7the darkzone)",
						"",
						"&7Progress: &3" + Formatter.formatLarge(progress / 60) + "&7/&3" + Formatter.formatLarge(questLevel.getRequirement(pitPlayer) / 60) + " &8[" +
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
	public void createPossibleStates() {
		questLevels.add(new QuestLevel(60 * 60, 100));
		questLevels.add(new QuestLevel(60 * 90, 100));
		questLevels.add(new QuestLevel(60 * 120, 100));
	}

	@Override
	public double getMultiplier(PitPlayer pitPlayer) {
		return 1.0;
	}
}
