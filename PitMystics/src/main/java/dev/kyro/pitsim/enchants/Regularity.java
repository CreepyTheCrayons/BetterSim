package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.HitCounter;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Regularity extends PitEnchant {

	public static List<UUID> toReg = new ArrayList<>();

	public Regularity() {
		super("Regularity", true, ApplyType.PANTS,
				"regularity", "reg");

		meleOnly = true;
	}

	@EventHandler
	public void onAttack(AttackEvent.Post attackEvent) {
		if(!canApply(attackEvent)) return;

		if(toReg.contains(attackEvent.defender.getUniqueId())) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		double finalDamage = attackEvent.event.getFinalDamage();
		if(finalDamage >= maxFinalDamage(enchantLvl)) return;

		HitCounter.incrementCounter(attackEvent.attacker, this);
		if(!HitCounter.hasReachedThreshold(attackEvent.attacker, this, getStrikes())) return;

		toReg.add(attackEvent.defender.getUniqueId());
		new BukkitRunnable() {
			@Override
			public void run() {
				if(!toReg.contains(attackEvent.defender.getUniqueId())) return;

				double damage = attackEvent.event.getOriginalDamage(EntityDamageEvent.DamageModifier.BASE);
				attackEvent.defender.setNoDamageTicks(0);
				attackEvent.defender.damage(damage * secondHitDamage(enchantLvl) / 100, attackEvent.attacker);
			}
		}.runTaskLater(PitSim.INSTANCE, 3L);

		new BukkitRunnable() {
			@Override
			public void run() {
				toReg.remove(attackEvent.defender.getUniqueId());
			}
		}.runTaskLater(PitSim.INSTANCE, 4L);
	}

	public static int secondHitDamage(int enchantLvl) {

		return enchantLvl * 15 + 15;
	}

	public static double maxFinalDamage(int enchantLvl) {

		return enchantLvl * 0.4 + 2.8;
	}

	public static int getStrikes() {

		return 2;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

//		return new ALoreBuilder("&7If the final damage of your strike", "&7deals less than &c" +
//				Misc.getHearts(maxFinalDamage(enchantLvl)) + " &7damage,",
//				"&7strike again in &a0.1s &7for &c" + secondHitDamage(enchantLvl) + "%", "&7damage").getLore();

		return new ALoreBuilder("&7Every &eSecond &7hit strikes",
				"&7again in &a0.1s &7for &c" + secondHitDamage(enchantLvl) + "% damage").getLore();
	}
}
