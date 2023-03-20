package dev.kyro.pitsim.enchants.overworld;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.HitCounter;
import dev.kyro.pitsim.controllers.PolarManager;
import dev.kyro.pitsim.controllers.objects.AnticheatManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.List;

public class ComboStun extends PitEnchant {

	public ComboStun() {
		super("Combo: Stun", true, ApplyType.MELEE,
				"combostun", "stun", "combo-stun", "cstun");
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerPlayer()) return;
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getAttackerEnchantLevel(this);
		if(enchantLvl == 0) return;
		if(attackEvent.isFakeHit()) return;

		int regLvl = attackEvent.getAttackerEnchantLevel(Regularity.INSTANCE);
		if(Regularity.isRegHit(attackEvent.getDefender()) && Regularity.skipIncrement(regLvl)) return;

		Cooldown cooldown = getCooldown(attackEvent.getAttackerPlayer(), 8 * 20);
		if(cooldown.isOnCooldown()) return;

		PitPlayer pitPlayer = attackEvent.getAttackerPitPlayer();
		HitCounter.incrementCounter(pitPlayer.player, this);
		if(!HitCounter.hasReachedThreshold(pitPlayer.player, this, 5)) return;

		else cooldown.restart();
		int duration = (int) getDuration(enchantLvl) * 20;

		if(PitSim.anticheat instanceof PolarManager) {
			new BukkitRunnable() {
				@Override
				public void run() {
					PitSim.anticheat.exemptPlayer(attackEvent.getDefenderPlayer(), duration * 500L, AnticheatManager.FlagType.KNOCKBACK, AnticheatManager.FlagType.SIMULATION);
				}
			}.runTaskLater(PitSim.INSTANCE, 10);

		Misc.stunEntity(attackEvent.getDefender(), duration);
		Sounds.COMBO_STUN.play(attackEvent.getAttacker());

		if(pitPlayer.stats != null) pitPlayer.stats.stun += getDuration(enchantLvl);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		DecimalFormat decimalFormat = new DecimalFormat("0.0");
		return new PitLoreBuilder(
				"&7The &efifth &7strike on an enemy stuns them for " +
						decimalFormat.format(getDuration(enchantLvl)) + " &7seconds &o(Can only be stunned every 8s)"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is an enchant that stuns " +
				"your opponent every few strikes";
	}

	public double getDuration(int enchantLvl) {
		return enchantLvl * 0.4 + 0.8;
	}
}
