package dev.kyro.pitsim.battlepass.quests.daily;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.battlepass.PassQuest;
import dev.kyro.pitsim.controllers.PlayerManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.controllers.objects.PlayerToPlayerCooldown;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class DailyPlayerKillQuest extends PassQuest {
	public PlayerToPlayerCooldown cooldown = new PlayerToPlayerCooldown(20 * 60 * 2);

	public DailyPlayerKillQuest() {
		super("&c&lKill Players", "dailyplayerkills", QuestType.DAILY);
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!PlayerManager.isRealPlayer(killEvent.getKillerPlayer()) || !canProgressQuest(killEvent.getKillerPitPlayer())
				|| !PlayerManager.isRealPlayer(killEvent.getDeadPlayer())) return;

		if(cooldown.isOnCooldown(killEvent.getKillerPlayer(), killEvent.getDeadPlayer())) return;

		progressQuest(killEvent.getKillerPitPlayer(), 1);
	}

	@Override
	public ItemStack getDisplayStack(PitPlayer pitPlayer, QuestLevel questLevel, double progress) {
		ItemStack itemStack = new AItemStackBuilder(Material.IRON_SWORD)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Kill &c" + Misc.formatLarge(questLevel.getRequirement(pitPlayer)) + " &7players",
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
		questLevels.add(new QuestLevel(10.0, 40));
	}

	@Override
	public double getMultiplier(PitPlayer pitPlayer) {
		return 1.0;
	}
}
