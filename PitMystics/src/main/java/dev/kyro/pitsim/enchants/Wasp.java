package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Wasp extends PitEnchant {

	public Wasp() {
		super("Wasp", false, ApplyType.BOWS,
				"wasp");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canAttack(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		Misc.applyPotionEffect(attackEvent.defender, PotionEffectType.WEAKNESS, getDuration(enchantLvl) * 20, enchantLvl, true, false);
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Apply &cWeakness " + AUtil.toRoman(enchantLvl + 1) + " &7(" +
				getDuration(enchantLvl) + "s) on hit").getLore();
	}

//	TODO: Wasp damage equation
	public int getDuration(int enchantLvl) {

		switch(enchantLvl) {
			case 1:
				return 6;
			case 2:
				return 11;
			case 3:
				return 16;

		}

		return 0;
	}
}
