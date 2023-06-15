package net.pitsim.spigot.battlepass.quests;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import net.pitsim.spigot.battlepass.PassQuest;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.misc.Formatter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BrewPotionsQuest extends PassQuest {
	public static BrewPotionsQuest INSTANCE;

	public BrewPotionsQuest() {
		super("&d&lFrom Lead to Gold", "brewpotions", QuestType.WEEKLY);
		INSTANCE = this;
	}

	public void brewPotion(PitPlayer pitPlayer) {
		progressQuest(pitPlayer, 1);
	}

	@Override
	public ItemStack getDisplayStack(PitPlayer pitPlayer, QuestLevel questLevel, double progress) {
		ItemStack itemStack = new AItemStackBuilder(Material.BREWING_STAND_ITEM)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Brew &d" + Formatter.formatLarge(questLevel.getRequirement(pitPlayer)) + " &7potions",
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
		questLevels.add(new QuestLevel(20, 100));
		questLevels.add(new QuestLevel(30, 150));
		questLevels.add(new QuestLevel(40, 200));
	}

	@Override
	public double getMultiplier(PitPlayer pitPlayer) {
		return 1.0;
	}
}
