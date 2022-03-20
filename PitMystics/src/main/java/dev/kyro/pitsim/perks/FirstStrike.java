package dev.kyro.pitsim.perks;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitPerk;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirstStrike extends PitPerk {
	public Map<LivingEntity, List<LivingEntity>> hitPlayers = new HashMap<>();

	public static FirstStrike INSTANCE;

	public FirstStrike() {
		super("First Strike", "firststrike", new ItemStack(Material.COOKED_CHICKEN), 15, true, "FIRST_STRIKE", INSTANCE);
		INSTANCE = this;
	}

	@EventHandler
	public void onHit(AttackEvent.Apply attackEvent) {

		if(!playerHasUpgrade(attackEvent.attacker)) return;

		if(!hitPlayers.containsKey(attackEvent.attacker)) hitPlayers.put(attackEvent.attacker, new ArrayList<>());
		List<LivingEntity> hitList = hitPlayers.get(attackEvent.attacker);

		if(!hitList.contains(attackEvent.defender)) {
			attackEvent.increasePercent += 30 / 100D;
			Misc.applyPotionEffect(attackEvent.attacker, PotionEffectType.SPEED, 5 * 20, 0, false, false);
		}


		List<LivingEntity> newList = new ArrayList<>(hitList);
		newList.add(attackEvent.defender);
		hitPlayers.put(attackEvent.attacker, newList);

		new BukkitRunnable() {
			@Override
			public void run() {
				hitPlayers.get(attackEvent.attacker).remove(attackEvent.defender);
			}
		}.runTaskLater(PitSim.INSTANCE, 120L);
	}

	@Override
	public List<String> getDescription() {
		return new ALoreBuilder("&7First hit on a player deals", "&c+30% damage &7and grants", "&eSpeed I &7(5s).").getLore();
	}
}
