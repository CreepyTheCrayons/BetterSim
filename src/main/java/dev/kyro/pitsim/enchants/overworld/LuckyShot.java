package dev.kyro.pitsim.enchants.overworld;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.Sounds;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class LuckyShot extends PitEnchant {

	public List<Arrow> luckyShots = new ArrayList<>();

	public LuckyShot() {
		super("Lucky Shot", true, ApplyType.BOWS,
				"luckyshot", "lucky-shot", "lucky", "luck");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerPlayer()) return;
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;

		for(Arrow luckyShot : luckyShots) {
			if(luckyShot.equals(attackEvent.getArrow())) {

				attackEvent.multipliers.add(3.0);
				String attack = "&e&lLUCKY SHOT!&7 against %luckperms_prefix%%player_name%&7!";
				String defend = "&c&lOUCH! %luckperms_prefix%%player_name% &7got a lucky shot against you!";
				if(attackEvent.isDefenderPlayer()) {
					Misc.sendTitle(attackEvent.getDefenderPlayer(), " ", 20);
					Misc.sendSubTitle(attackEvent.getDefenderPlayer(), "&c&lOUCH!", 20);
					Sounds.LUCKY_SHOT.play(attackEvent.getDefender());
					AOutput.send(attackEvent.getAttacker(), PlaceholderAPI.setPlaceholders(attackEvent.getDefenderPlayer(), attack));
				}
				AOutput.send(attackEvent.getDefender(), PlaceholderAPI.setPlaceholders(attackEvent.getAttackerPlayer(), defend));
			}
		}
	}

	@EventHandler
	public void onShoot(EntityShootBowEvent event) {
		if(!(event.getEntity() instanceof Player) || !(event.getProjectile() instanceof Arrow)) return;
		Player player = (Player) event.getEntity();

		int enchantLvl = EnchantManager.getEnchantLevel(((Player) event.getEntity()).getPlayer(), this);

		double chanceCalculation = Math.random();
		double enchantChance = getChance(enchantLvl) / 100D;

		if(chanceCalculation <= enchantChance) {
			luckyShots.add((Arrow) event.getProjectile());

			PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
			pitPlayer.stats.lucky++;
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onHit(ProjectileHitEvent event) {
		if(!(event.getEntity() instanceof Arrow) || !(event.getEntity().getShooter() instanceof Player)) return;

		if(luckyShots.size() == 0) return;
		try {
			for(Arrow luckyShot : luckyShots) {
				if(luckyShot.equals(event.getEntity())) {
					Arrow luckyArrow = (Arrow) event.getEntity();
					if(luckyArrow.equals(luckyShot)) {
						new BukkitRunnable() {
							@Override
							public void run() {
								luckyShots.remove(luckyShot);
							}
						}.runTaskLater(PitSim.INSTANCE, 1L);
					}
				}
			}
		} catch(Exception ignored) {}
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&e" + getChance(enchantLvl) + "&e% &7chance for a shot to deal triple damage"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that has a very " +
				"low chance to make your arrows deal triple damage";
	}

	public int getChance(int enchantLvl) {
		return enchantLvl * 4 + 3;
	}
}
