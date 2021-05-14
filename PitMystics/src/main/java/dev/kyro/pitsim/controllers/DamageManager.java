package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.enchants.PitBlob;
import dev.kyro.pitsim.enchants.Regularity;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.nons.Non;
import dev.kyro.pitsim.nons.NonManager;
import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DamageManager implements Listener {

	public static List<Player> hitCooldownList = new ArrayList<>();
	public static List<Player> nonHitCooldownList = new ArrayList<>();
	public static Map<EntityShootBowEvent, Map<PitEnchant, Integer>> arrowMap = new HashMap<>();

	static {

		new BukkitRunnable() {
			@Override
			public void run() {

				List<EntityShootBowEvent> toRemove = new ArrayList<>();
				for(Map.Entry<EntityShootBowEvent, Map<PitEnchant, Integer>> entry : arrowMap.entrySet()) {

					if(entry.getKey().getProjectile().isDead()) toRemove.add(entry.getKey());
				}
				for(EntityShootBowEvent remove : toRemove) {
					arrowMap.remove(remove);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBowShoot(EntityShootBowEvent event) {

		if(!(event.getEntity() instanceof Player) || !(event.getProjectile() instanceof Arrow)) return;
		Player shooter = (Player) event.getEntity();
		Arrow arrow = (Arrow) event.getProjectile();
		arrowMap.put(event, EnchantManager.getEnchantsOnPlayer(shooter));
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onAttack(EntityDamageByEntityEvent event) {

		if(!(event.getEntity() instanceof Player)) return;
		Player attacker = getAttacker(event.getDamager());
		Player defender = (Player) event.getEntity();

		Map<PitEnchant, Integer> defenderEnchantMap = EnchantManager.getEnchantsOnPlayer(defender);
		boolean fakeHit = false;

		Non attackingNon = NonManager.getNon(attacker);
		Non defendingNon = NonManager.getNon(defender);
//		Hit on non or by non
//		TODO: wadafrick how does this reg cooldown work
		if((attackingNon != null && nonHitCooldownList.contains(defender)) ||
				(attackingNon == null && defendingNon != null && hitCooldownList.contains(defender)) && !Regularity.toReg.contains(defender.getUniqueId())) {
			event.setCancelled(true);
			return;
		}
//		Regular player to player hit
		Bukkit.broadcastMessage(Regularity.toReg.contains(defender.getUniqueId()) + "");
		if(attackingNon == null && !Regularity.toReg.contains(defender.getUniqueId())) {
			fakeHit = hitCooldownList.contains(defender);
		}

		if(!fakeHit) {
			hitCooldownList.add(defender);
			nonHitCooldownList.add(defender);
			new BukkitRunnable() {
				int count = 0;
				@Override
				public void run() {
					if(++count == 12) cancel();

					if(count == 10) DamageManager.hitCooldownList.remove(defender);
					if(count == 12) DamageManager.nonHitCooldownList.remove(defender);
				}
			}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
		}

		AttackEvent.Pre preEvent = null;
		if(event.getDamager() instanceof Player) {

			preEvent = new AttackEvent.Pre(event, EnchantManager.getEnchantsOnPlayer((Player) event.getDamager()), defenderEnchantMap, fakeHit);
		} else if(event.getDamager() instanceof Arrow) {

			for(Map.Entry<EntityShootBowEvent, Map<PitEnchant, Integer>> entry : arrowMap.entrySet()) {

				if(!entry.getKey().getProjectile().equals(event.getDamager())) continue;
				preEvent = new AttackEvent.Pre(event, arrowMap.get(entry.getKey()), defenderEnchantMap, fakeHit);
			}
		} else if(event.getDamager() instanceof Slime) {

			attacker = PitBlob.getOwner((Slime) event.getDamager());
			if(attacker == null) return;
		}
		if(preEvent == null) return;

		Bukkit.getServer().getPluginManager().callEvent(preEvent);
		if(preEvent.isCancelled()) {
			event.setCancelled(true);
			return;
		}
		AttackEvent.Apply applyEvent = new AttackEvent.Apply(preEvent);
		Bukkit.getServer().getPluginManager().callEvent(applyEvent);
		handleAttack(applyEvent);
		Bukkit.getServer().getPluginManager().callEvent(new AttackEvent.Post(applyEvent));
	}

	public static void handleAttack(AttackEvent.Apply attackEvent) {

//		AOutput.send(attackEvent.attacker, "Initial Damage: " + attackEvent.event.getDamage());

//		if(attackEvent.slime == null) {
		for(PitEnchant pitEnchant : EnchantManager.pitEnchants) {
//				Skip if fake hit and enchant doesn't handle fake hits
			if(!pitEnchant.fakeHits && attackEvent.fakeHit) continue;
//				Skip enchant application if the enchant is a bow enchant and is used in mele
			if(pitEnchant.applyType == ApplyType.BOWS && attackEvent.arrow == null) continue;
//				Skips enchant application if the enchant only works on mele hit and the event is from an arrow
			if(pitEnchant.meleOnly && attackEvent.arrow != null) continue;

//				pitEnchant.onDamage(attackEvent);
		}

		double damage = attackEvent.getFinalDamage();

		attackEvent.event.setDamage(damage);

		if(attackEvent.trueDamage != 0) {
			double finalHealth = attackEvent.defender.getHealth() - attackEvent.trueDamage;
			if(finalHealth <= 0) {
				attackEvent.event.setCancelled(true);
				kill(attackEvent.attacker, attackEvent.defender, false);
				return;
			} else {
				attackEvent.defender.setHealth(Math.max(finalHealth, 0));
			}
		}

		if(attackEvent.selfTrueDamage != 0) {
			double finalHealth = attackEvent.attacker.getHealth() - attackEvent.selfTrueDamage;
			if(finalHealth <= 0) {
				attackEvent.event.setCancelled(true);
				kill(attackEvent.attacker, attackEvent.defender, false);
				return;
			} else {
				attackEvent.attacker.setHealth(Math.max(finalHealth, 0));
				attackEvent.attacker.damage(0);
			}
		}
//		}

//		AOutput.send(attackEvent.attacker, "Final Damage: " + attackEvent.event.getDamage());

		if(attackEvent.event.getFinalDamage() >= attackEvent.defender.getHealth()) {

			attackEvent.event.setCancelled(true);
			kill(attackEvent.attacker, attackEvent.defender, false);
		} else if(attackEvent.event.getFinalDamage() + attackEvent.executeUnder >= attackEvent.defender.getHealth()) {

			attackEvent.event.setCancelled(true);
			kill(attackEvent.attacker, attackEvent.defender, true);
		}
	}

	public static Player getAttacker(Entity damager) {

		if(damager instanceof Player) return (Player) damager;
		if(damager instanceof Arrow) return (Player) ((Arrow) damager).getShooter();

		return null;
	}

	public static void kill(Player attacker, Player dead, boolean exeDeath) {

		PitPlayer pitAttacker = PitPlayer.getPitPlayer(attacker);
		pitAttacker.incrementKills();

		Location spawnLoc = new Location(Bukkit.getWorld("pit"), -107.5, 111, 193.5, 45, 0);

		dead.setHealth(dead.getMaxHealth());
		dead.playEffect(EntityEffect.HURT);
		dead.playSound(dead.getLocation(), Sound.FALL_BIG, 1000, 1F);
		dead.playSound(dead.getLocation(), Sound.FALL_BIG, 1000, 1F);
		Regularity.toReg.remove(dead.getUniqueId());

		DecimalFormat df = new DecimalFormat("##0.00");
		AOutput.send(attacker, "&a&lKILL!&7 on &b" + dead.getName() + " &b+" + "5" + "XP" + " &6+" + df.format(5) + "g");
		AOutput.send(dead, "&cYou Died!");

		Non non = NonManager.getNon(dead);
		if(non == null) {
			dead.teleport(spawnLoc);
		} else {
			Misc.multiKill(attacker);
			non.respawn();
		}

		Non attackNon = NonManager.getNon(attacker);
		if(attackNon != null) {
			attackNon.rewardKill();
		}
	}
}
