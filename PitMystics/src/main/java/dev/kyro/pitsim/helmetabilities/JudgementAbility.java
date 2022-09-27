package dev.kyro.pitsim.helmetabilities;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.HopperManager;
import dev.kyro.pitsim.controllers.objects.GoldenHelmet;
import dev.kyro.pitsim.controllers.objects.HelmetAbility;
import dev.kyro.pitsim.controllers.objects.Hopper;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class JudgementAbility extends HelmetAbility {
	public static List<UUID> cooldownList = new ArrayList<>();
	public static final int GOLD_COST = 7_000;
	public BukkitTask runnable;

	public JudgementAbility(Player player) {

		super(player, "Judgement", "judgement", true, 15);
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!isActive || player != attackEvent.attacker) return;
		ItemStack goldenHelmet = GoldenHelmet.getHelmet(attackEvent.attacker);
		assert goldenHelmet != null;
		if(!GoldenHelmet.withdrawGold(player, goldenHelmet, GOLD_COST)) {
			AOutput.error(player, "&cNot enough gold!");
			GoldenHelmet.deactivate(player);
			Sounds.NO.play(player);
			return;
		}

		PitPlayer pitAttacker = PitPlayer.getEntityPitPlayer(attackEvent.attacker);

		if(Math.random() < 0.25) {

			pitAttacker.heal(1);
			Sounds.JUDGEMENT_HEAL.play(attackEvent.attacker);
		}

		if(Math.random() < 0.20) {

			Misc.applyPotionEffect(attackEvent.defender, PotionEffectType.WITHER, 60, 2, true, false);
			Sounds.JUDGEMENT_WITHER.play(attackEvent.attacker);
		}

		if(Math.random() < 0.15) {

			Misc.applyPotionEffect(attackEvent.attacker, PotionEffectType.DAMAGE_RESISTANCE, 60, 0, true, false);
			Sounds.JUDGEMENT_RESISTANCE.play(attackEvent.attacker);
		}

		if(Math.random() < 0.10) {

			Misc.applyPotionEffect(attackEvent.attacker, PotionEffectType.INCREASE_DAMAGE, 40, 0, true, false);
			Sounds.JUDGEMENT_STRENGTH.play(attackEvent.attacker);
		}

		if(Math.random() < 0.05) {

			Misc.applyPotionEffect(attackEvent.defender, PotionEffectType.SLOW, 40, 4, true, false);
			Sounds.JUDGEMENT_SLOW.play(attackEvent.attacker);
		}

		if(Math.random() < 0.03) {

			attackEvent.defender.setHealth(attackEvent.defender.getHealth() / 2D);
			Sounds.JUDGEMENT_HALF_ATTACKER.play(attackEvent.attacker);
			Sounds.JUDGEMENT_HALF_DEFENDER.play(attackEvent.defender);
		}

		if(Math.random() < 0.02) {

			Sounds.JUDGEMENT_ZEUS_ATTACKER.play(attackEvent.attacker);
			Sounds.JUDGEMENT_ZEUS_DEFENDER.play(attackEvent.defender);
			new BukkitRunnable() {
				int count = 0;

				@Override
				public void run() {
					if(++count == 5) cancel();
					Misc.strikeLightningForPlayers(attackEvent.defender.getLocation(), 10);
					attackEvent.defender.setHealth(Math.max(attackEvent.defender.getHealth() - 2, 1));
				}
			}.runTaskTimer(PitSim.INSTANCE, 0L, 2L);
		}

		if(Math.random() < 0.005 && !HopperManager.isHopper(attackEvent.defender)) {

			Hopper hopper = HopperManager.callHopper("PayForTruce", Hopper.Type.GSET, attackEvent.defender);
			hopper.team.add(attackEvent.attacker.getUniqueId());
			Sounds.JUDGEMENT_HOPPER.play(attackEvent.attacker);
			Sounds.JUDGEMENT_HOPPER.play(attackEvent.defender);
		}
	}

	@Override
	public void onActivate() {
		ItemStack goldenHelmet = GoldenHelmet.getHelmet(player);
		assert goldenHelmet != null;

		Sounds.HELMET_ACTIVATE.play(player);
		DecimalFormat decimalFormat = new DecimalFormat("#,###");
		AOutput.send(player, "&6&lGOLDEN HELMET! &aActivated &9Judgement&7. (&6-" + decimalFormat.format(GOLD_COST) + "g&7 per hit)");

		runnable = new BukkitRunnable() {
			@Override
			public void run() {
				player.getWorld().playEffect(player.getLocation().add(0, 2, 0), Effect.VILLAGER_THUNDERCLOUD, 1);
			}
		}.runTaskTimer(PitSim.INSTANCE, 0, 4);
	}

	@Override
	public boolean shouldActivate() {
		if(cooldownList.contains(player.getUniqueId())) {
			Sounds.NO.play(player);
			AOutput.error(player, "&c&lCOOLDOWN! &7Please wait before activating &9Judgement &7again");
			return false;
		}

		if(GoldenHelmet.getUsedHelmetGold(player) < GOLD_COST) {
			Sounds.NO.play(player);
			AOutput.error(player, "&cNot enough gold!");
			return false;
		}
		return true;
	}

	@Override
	public void onDeactivate() {
		if(!cooldownList.contains(player.getUniqueId())) {
			cooldownList.add(player.getUniqueId());
			new BukkitRunnable() {
				@Override
				public void run() {
					cooldownList.remove(player.getUniqueId());
				}
			}.runTaskLater(PitSim.INSTANCE, 20 * 60);
			AOutput.send(player, "&6&lGOLDEN HELMET! &cDeactivated &9Judgement&c. &7(60s reactivation cooldown)");
		}
		if(runnable != null) runnable.cancel();
	}

	@Override
	public void onProc() {
	}

	@Override
	public List<String> getDescription() {
		DecimalFormat formatter = new DecimalFormat("#,###.#");
		return Arrays.asList("&7Double-Sneak to toggle", "&7Judgement. Annihilate your", "&7opponents with RNGesus", "",
				"&7Cost: &6" + formatter.format(GOLD_COST) + "g &7per hit");
	}

	@Override
	public ItemStack getDisplayItem() {
		AItemStackBuilder builder = new AItemStackBuilder(Material.BEACON);
		builder.setName("&e" + name);
		ALoreBuilder loreBuilder = new ALoreBuilder();
		loreBuilder.addLore(getDescription());
		builder.setLore(loreBuilder);

		return builder.getItemStack();
	}
}
