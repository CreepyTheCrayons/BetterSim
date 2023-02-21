package dev.kyro.pitsim.enchants.overworld;

import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Moctezuma extends PitEnchant {

	public Moctezuma() {
		super("Moctezuma", false, ApplyType.ALL,
				"moctezuma", "moct", "moc");
		levelStacks = true;
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		int enchantLvl = killEvent.getKillerEnchantLevel(this);
		if(enchantLvl == 0) return;

		killEvent.goldReward += getGoldIncrease(enchantLvl);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Earn &6+" + getGoldIncrease(enchantLvl) + "g &7on kill (assists &7excluded)"
		).getLore();
	}

	public int getGoldIncrease(int enchantLvl) {
		return enchantLvl * 6;
	}
}
