package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Electrolytes extends PitEnchant {

	public Electrolytes() {
		super("Electrolytes", false, ApplyType.PANTS,
				"electrolytes", "electrolyte", "electro", "elec", "lytes");
		isUncommonEnchant = true;
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		int enchantLvl = killEvent.getKillerEnchantLevel(this);

		if(killEvent.getKiller().hasPotionEffect(PotionEffectType.SPEED)) {

			int speedDuration = 0;
			for(PotionEffect potioneffect : killEvent.getKiller().getActivePotionEffects()) {
				int duration = potioneffect.getDuration();
				if(potioneffect.getType() == PotionEffectType.SPEED) {
					speedDuration = duration;
				}
			}

			if(speedDuration > 12.5 * 20) return;

			for(PotionEffect activePotionEffect : killEvent.getKiller().getActivePotionEffects()) {

				if(activePotionEffect.getType().equals(PotionEffectType.SPEED)) {


					if(activePotionEffect.getAmplifier() > 0) {

						if(activePotionEffect.getDuration() + (getSeconds(enchantLvl) * 20) / 2 > getMaxSeconds(enchantLvl) * 20) {

							Misc.applyPotionEffect(killEvent.getKiller(), PotionEffectType.SPEED, getMaxSeconds(enchantLvl) * 20,
									activePotionEffect.getAmplifier(), false, false);
						} else {
							Misc.applyPotionEffect(killEvent.getKiller(), PotionEffectType.SPEED, (activePotionEffect.getDuration()
									+ (getSeconds(enchantLvl) * 20) / 2), activePotionEffect.getAmplifier(), false, false);
						}
					} else {
						if(activePotionEffect.getDuration() + (getSeconds(enchantLvl) * 20) > getMaxSeconds(enchantLvl) * 20) {

							Misc.applyPotionEffect(killEvent.getKiller(), PotionEffectType.SPEED, getMaxSeconds(enchantLvl) * 20,
									activePotionEffect.getAmplifier(), false, false);
						} else {
							Misc.applyPotionEffect(killEvent.getKiller(), PotionEffectType.SPEED, (int)
											activePotionEffect.getDuration() + (getSeconds(enchantLvl) * 20),
									activePotionEffect.getAmplifier(), false, false);
						}
					}
				}
			}
		}
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7If you have &eSpeed &7on kill, add", "&e" + getSeconds(enchantLvl) +
				" &7seconds to its duration.", "&7(Halved for Speed II+, Max " + getMaxSeconds(enchantLvl) + "s)").getLore();
	}

	public int getSeconds(int enchantLvl) {
		return enchantLvl * 2 - 1;
	}

	public int getMaxSeconds(int enchantLvl) {
		return 8;
	}
}
