package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.NumberFormatter;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Solitude extends PitEnchant {

	public Solitude() {
		super("Solitude", true, ApplyType.PANTS,
				"solitude", "soli");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!canAttack(attackEvent)) return;

		int enchantLvl = attackEvent.getDefenderEnchantLevel(this);
		if(enchantLvl == 0) return;
		
		attackEvent.multiplier.add(Misc.getReductionMultiplier(getDamageReduction(enchantLvl)));
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Receive &9-" + Misc.roundString(getDamageReduction(enchantLvl)) + "% &7damage when &9" +
				NumberFormatter.convert(getNearbyPlayers(enchantLvl)), "&7or less players are within 7", "&7blocks").getLore();
	}

	public int getNearbyPlayers(int enchantLvl) {

		return Misc.linearEnchant(enchantLvl, 0.5, 1);
	}

	public double getDamageReduction(int enchantLvl) {

		return 30 + enchantLvl * 10;
	}
}
