package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;

import java.util.List;

public class GoldBump extends PitEnchant {

	public GoldBump() {
		super("Gold Bump", false, ApplyType.ALL,
				"goldbump", "gold-bump", "bump", "gbump");
		levelStacks = true;
	}

//	@EventHandler
//	public void onKill(KillEvent killEvent) {
//
//		int enchantLvl = killEvent.attackEvent.getAttackerEnchantLevel(this);
//		if(enchantLvl == 0) return;
//
//		killEvent.goldReward += getGoldIncrease(enchantLvl);
//	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Earn &6+" + getGoldIncrease(enchantLvl) + "g &7per kill").getLore();
	}

	public int getGoldIncrease(int enchantLvl) {

		return enchantLvl * 4;
	}
}
