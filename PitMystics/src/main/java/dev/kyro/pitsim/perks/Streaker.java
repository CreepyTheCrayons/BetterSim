package dev.kyro.pitsim.perks;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.objects.PitPerk;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.events.OofEvent;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.*;

public 	class Streaker extends PitPerk {
	public static Map<LivingEntity, Integer> playerTimes = new HashMap<>();
	public static Map<LivingEntity, Double> xpReward = new HashMap<>();

	public static Streaker INSTANCE;

	public Streaker() {
		super("Streaker", "streaker", new ItemStack(Material.WHEAT), 16, true, "STREAKER", INSTANCE, false);
		INSTANCE = this;
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		xpReward.remove(killEvent.getDead());
		playerTimes.remove(killEvent.getDead());

		if(!playerHasUpgrade(killEvent.getKiller())) return;
		if(!killEvent.isDeadPlayer() || NonManager.getNon(killEvent.getDead()) == null) return;
		killEvent.xpCap += 80;

		if(xpReward.containsKey(killEvent.getKiller())) killEvent.xpMultipliers.add(xpReward.get(killEvent.getKiller()));

		if(!killEvent.isKillerPlayer()) return;
		PitPlayer pitPlayer = killEvent.getKillerPitPlayer();

		if(pitPlayer.getKills() + 1 >= pitPlayer.megastreak.getRequiredKills()) {
			if(playerTimes.containsKey(killEvent.getKiller())) {
				Player player = killEvent.getKillerPlayer();
				double xp = 0;

//				TODO: Update lore
				if(playerTimes.get(player) > 90) xp = 1;
				if(playerTimes.get(player) <= 90) xp = 1.1;
				if(playerTimes.get(player) <= 80) xp = 1.2;
				if(playerTimes.get(player) <= 60) xp = 1.3;
				if(playerTimes.get(player) <= 45) xp = 1.4;
				if(playerTimes.get(player) <= 30) xp = 1.5;
				if(playerTimes.get(player) <= 25) xp = 1.6;
				if(playerTimes.get(player) <= 20) xp = 1.7;
				if(playerTimes.get(player) <= 15) xp = 1.8;
				if(playerTimes.get(player) <= 10) xp = 1.9;
				if(playerTimes.get(player) <= 5) xp = 2;

				xpReward.put(player, xp);

				DecimalFormat format = new DecimalFormat("0.#");
				AOutput.send(player, "&b&lSTREAKER! &7You hit your megastreak in &e" +
						playerTimes.get(player) + " seconds&7. Gain &b+" + format.format(Math.ceil((xp - 1) * 100)) + "% XP &7for the rest of the streak.");
				Sounds.STREAKER.play(player);
				playerTimes.remove(player);
				return;
			}
		}

		if(!playerTimes.containsKey(killEvent.getKiller()) && !pitPlayer.megastreak.isOnMega()) {
			playerTimes.put(killEvent.getKiller(), 0);
			AOutput.send(killEvent.getKiller(), "&b&lSTREAKER! &7Streak timer started!");
			Sounds.STREAKER.play(killEvent.getKiller());
		}
	}

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Map.Entry<LivingEntity, Integer> entry : playerTimes.entrySet()) {
					int time = entry.getValue();
					time = time + 1;

					playerTimes.put(entry.getKey(), time);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 20L);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {

		xpReward.remove(event.getPlayer());
		playerTimes.remove(event.getPlayer());
	}

	@EventHandler
	public void onOof(OofEvent event) {
		xpReward.remove(event.getPlayer());
		playerTimes.remove(event.getPlayer());
	}

	@Override
	public List<String> getDescription() {
		return new ALoreBuilder("&7Upon reaching your &emegastreak&7,",
				"&7gain &bmore XP the faster you hit mega", "&7Passively gain &b+80 max XP").getLore();
	}
}
