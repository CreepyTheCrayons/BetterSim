package net.pitsim.spigot.battlepass.quests.dzkillmobs;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import net.pitsim.spigot.darkzone.DarkzoneManager;
import net.pitsim.spigot.darkzone.mobs.PitZombiePigman;
import net.pitsim.spigot.battlepass.PassManager;
import net.pitsim.spigot.battlepass.PassQuest;
import net.pitsim.spigot.controllers.PlayerManager;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.events.KillEvent;
import net.pitsim.spigot.misc.Formatter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class KillZombiePigmenQuest extends PassQuest {

	public KillZombiePigmenQuest() {
		super("&c&lZombie Pigman Slayer", "killzombiepigmen", QuestType.WEEKLY);
		weight = PassManager.DARKZONE_KILL_QUEST_WEIGHT;
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!PlayerManager.isRealPlayer(killEvent.getKillerPlayer())) return;
		if(!(DarkzoneManager.getPitMob(killEvent.getDead()) instanceof PitZombiePigman)) return;

		progressQuest(killEvent.getKillerPitPlayer(), 1);
	}

	@Override
	public ItemStack getDisplayStack(PitPlayer pitPlayer, QuestLevel questLevel, double progress) {
		ItemStack itemStack = new AItemStackBuilder(Material.PORK)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Kill &c" + Formatter.formatLarge(questLevel.getRequirement(pitPlayer)) + " &7zombie pigmen",
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
		questLevels.add(new QuestLevel(500, 100));
		questLevels.add(new QuestLevel(750, 150));
		questLevels.add(new QuestLevel(1000, 200));
	}

	@Override
	public double getMultiplier(PitPlayer pitPlayer) {
		return 1.0;
	}
}
