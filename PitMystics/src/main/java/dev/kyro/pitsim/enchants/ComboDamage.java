package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.controllers.HitCounter;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.event.EventHandler;

import java.util.List;

public class ComboDamage extends PitEnchant {

	public ComboDamage() {
		super("Combo: Damage", false, ApplyType.MELEE,
				"combodamage", "cd", "combo-damage", "cdamage");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerIsPlayer()) return;
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;
		if(attackEvent.isFakeHit()) return;

		int regLvl = attackEvent.getAttackerEnchantLevel(Regularity.INSTANCE);
		if(Regularity.isRegHit(attackEvent.getDefender()) && Regularity.skipIncrement(regLvl)) return;

		PitPlayer pitPlayer = attackEvent.getAttackerPitPlayer();
		HitCounter.incrementCounter(pitPlayer.player, this);
		if(!HitCounter.hasReachedThreshold(pitPlayer.player, this, getStrikes(enchantLvl))) return;

		attackEvent.increasePercent += getDamage(enchantLvl) / 100D;

		Sounds.COMBO_PROC.play(attackEvent.getAttacker());
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Every&e" + Misc.ordinalWords(getStrikes(enchantLvl)) + " &7strike deals",
				"&c+" + getDamage(enchantLvl) + "% &7damage").getLore();
	}

	public int getDamage(int enchantLvl) {

		return (int) (Math.floor(Math.pow(enchantLvl, 1.75)) * 5 + 25);
	}

	public int getStrikes(int enchantLvl) {

		return Math.max(4 - (int) (enchantLvl * 0.5), 1);

	}
}
