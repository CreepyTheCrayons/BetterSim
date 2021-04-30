package dev.kyro.pitremake.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitremake.controllers.*;
import dev.kyro.pitremake.enums.ApplyType;
import dev.kyro.pitremake.misc.Misc;

import java.util.List;

public class ComboDamage extends PitEnchant {

	public ComboDamage() {
		super("Combo: Damage", false, ApplyType.SWORDS,
				"combodamage", "cd", "combo-damage", "cdamage");
	}

	@Override
	public DamageEvent onDamage(DamageEvent damageEvent) {

		int enchantLvl = damageEvent.getEnchantLevel(this);
		if(enchantLvl == 0) return damageEvent;

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(damageEvent.attacker);
		HitCounter.incrementCounter(pitPlayer.player, this);
		if(!HitCounter.hasReachedThreshold(pitPlayer.player, this, getStrikes(enchantLvl))) return damageEvent;

		damageEvent.increasePercent += getDamage(enchantLvl) / 100;

		return damageEvent;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Every &e" + Misc.ordinalWords(enchantLvl) + " &7strike deals",
				"&c+" + getDamage(enchantLvl) + "% &7damage").getLore();
	}

	public double getDamage(int enchantLvl) {

		return Math.floor(Math.pow(enchantLvl, 1.75)) * 5 + 15;
	}

	public int getStrikes(int enchantLvl) {

		return Math.max(4 - (int) (enchantLvl * 0.5), 1);
	}
}
