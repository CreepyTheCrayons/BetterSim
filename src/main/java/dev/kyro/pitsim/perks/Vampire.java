package dev.kyro.pitsim.perks;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitPerk;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.HealEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Vampire extends PitPerk {
	public static Vampire INSTANCE;

	public static double initialHealing = 1;

	public Vampire() {
		super("Vampire", "vampire", new ItemStack(Material.FERMENTED_SPIDER_EYE), 10, false, "", INSTANCE, true);
		INSTANCE = this;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerPlayer()) return;
		if(!playerHasUpgrade(attackEvent.getAttacker())) return;
		if(MapManager.inDarkzone(attackEvent.getAttacker())) return;
		PitPlayer pitAttacker = attackEvent.getAttackerPitPlayer();

		double healing = initialHealing;
		if(attackEvent.getArrow() != null && attackEvent.getArrow().isCritical()) healing *= 2;
		HealEvent healEvent = pitAttacker.heal(healing);
	}

	@Override
	public List<String> getDescription() {
		return new ALoreBuilder("&7Heal &c" + Misc.getHearts(initialHealing) + " &7on hit.", "&7Doubled on arrow crit.").getLore();
	}

	@Override
	public String getSummary() {
		return "&aVampire &7is a perk that heals you on hit";
	}
}
