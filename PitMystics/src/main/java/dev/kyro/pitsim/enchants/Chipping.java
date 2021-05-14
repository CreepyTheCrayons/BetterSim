package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.event.EventHandler;

import java.util.List;

public class 	Chipping extends PitEnchant {

	public Chipping() {
		super("Chipping", false, ApplyType.BOWS,
				"chipping", "chip");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canAttack(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0 || !attackEvent.arrow.isCritical()) return;

		attackEvent.trueDamage += getDamage(enchantLvl);
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Deals &c" + Misc.getHearts(getDamage(enchantLvl)) + " &7extra true damage").getLore();
	}

	public double getDamage(int enchantLvl) {

		return enchantLvl;
	}
}
