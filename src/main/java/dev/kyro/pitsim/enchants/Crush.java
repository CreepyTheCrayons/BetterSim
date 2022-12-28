package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Crush extends PitEnchant {

	public Crush() {
		super("Crush", false, ApplyType.MELEE,
				"crush");
		isUncommonEnchant = true;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerPlayer()) return;
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		Cooldown cooldown = getCooldown(attackEvent.getAttackerPlayer(), 2 * 20);
		if(cooldown.isOnCooldown()) return;
		else cooldown.restart();

		Misc.applyPotionEffect(attackEvent.getDefender(), PotionEffectType.WEAKNESS, getDuration(enchantLvl), enchantLvl + 3, true, false);
		Sounds.CRUSH.play(attackEvent.getAttacker());
		Sounds.CRUSH.play(attackEvent.getDefender());
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Strikes apply &cWeakness " + AUtil.toRoman(enchantLvl + 4), "&7(lasts " + (getDuration(enchantLvl) / 20D) +
				"s, 2s cooldown)").getLore();
	}

	public int getDuration(int enchantLvl) {

		return enchantLvl * 6 + 2;
	}
}
