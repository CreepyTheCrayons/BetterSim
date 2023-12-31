package net.pitsim.spigot.enchants.tainted.chestplate;

import net.pitsim.spigot.controllers.EnchantManager;
import net.pitsim.spigot.controllers.objects.PitEnchant;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.enums.ApplyType;
import net.pitsim.spigot.events.ManaRegenEvent;
import net.pitsim.spigot.misc.Misc;
import net.pitsim.spigot.misc.PitLoreBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Terror extends PitEnchant {
	public static Terror INSTANCE;

	public Terror() {
		super("Terror", true, ApplyType.CHESTPLATES,
				"terror");
		isTainted = true;
		INSTANCE = this;
	}

	@EventHandler
	public void onManaRegen(ManaRegenEvent event) {
		Player player = event.getPlayer();
		int enchantLvl = EnchantManager.getEnchantLevel(player, this);
		if(enchantLvl == 0) return;
		event.multipliers.add(Misc.getReductionMultiplier(getManaReduction(enchantLvl)));
	}

	public static double getAvoidanceMultiplier(Player player) {
		int enchantLvl = EnchantManager.getEnchantLevel(player, INSTANCE);
		if(enchantLvl == 0) return 1;

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(!pitPlayer.hasManaUnlocked()) return 1;

		return getAvoidanceMultiplier(enchantLvl);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		if(enchantLvl == 1) {
			return new PitLoreBuilder(
					"&7Mobs look at you funny. They probably don't like you. " +
							"When worn, regain mana &b" + getManaReduction(enchantLvl) + "% &7slower"
			).getLore();
		} else if(enchantLvl == 2) {
			return new PitLoreBuilder(
					"&7Mobs prefer to fight other players. When worn, regain mana &b" + getManaReduction(enchantLvl) + "% &7slower"
			).getLore();
		} else {
			return new PitLoreBuilder(
					"&7Mobs actively try to avoid you. When worn, regain mana &b" + getManaReduction(enchantLvl) + "% &7slower"
			).getLore();
		}
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is a &5Darkzone &7enchant that " +
				"decreases the amount of mobs that naturally target you";
	}

	public static double getAvoidanceMultiplier(int enchantLvl) {
		return enchantLvl * 0.4 + 0.5;
	}

	public static int getManaReduction(int enchantLvl) {
		return 30;
	}
}
