package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.scheduler.BukkitRunnable;

public  class PlayerDataManager implements Listener {
	static {
		new BukkitRunnable() {
			int count = 0;
			@Override
			public void run() {
				if(count++ % 60 == 0) {
					for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
						PitPlayer pitPlayer = PitPlayer.getPitPlayer(onlinePlayer);
						pitPlayer.fullSave();
					}
				} else {
					for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
						PitPlayer pitPlayer = PitPlayer.getPitPlayer(onlinePlayer);
						pitPlayer.save();
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE,  20L, 20);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		new BukkitRunnable() {
			@Override
			public void run() {
				pitPlayer.fullSave();
			}
		}.runTaskLater(PitSim.INSTANCE, 10L);
	}

	@EventHandler
	public void onDisable(PluginDisableEvent event) {
		if(event.getPlugin() != PitSim.INSTANCE) return;

		for(Player player : Bukkit.getOnlinePlayers()) {
			PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
			pitPlayer.fullSave();
		}
	}
}
