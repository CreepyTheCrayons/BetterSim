package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.pitsim.controllers.DamageEvent;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import org.bukkit.Sound;

import java.util.List;

public class Billionaire extends PitEnchant {

	public Billionaire() {
		super("Billionaire", true, ApplyType.SWORDS,
				"bill", "billionaire");
	}

	@Override
	public DamageEvent onDamage(DamageEvent damageEvent) {

		int enchantLvl = damageEvent.getEnchantLevel(this);
		if(enchantLvl == 0) return damageEvent;

		damageEvent.multiplier.add(getDamageMultiplier(enchantLvl));
		ASound.play(damageEvent.attacker, Sound.ORB_PICKUP, 1, 0.73F);

		return damageEvent;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Hits with this sword deal &c" + getDamageMultiplier(enchantLvl) + "x",
				"&cdamage &7but cost &6" + getGoldCost(enchantLvl) + "g").getLore();
	}

	public double getDamageMultiplier(int enchantLvl) {

		return (double) Math.round((1 + (double) enchantLvl / 3) * 100) / 100;
	}

	public int getGoldCost(int enchantLvl) {

		return (int) (Math.floor(Math.pow(enchantLvl, 1.75)) * 50 + 50);
	}
}
