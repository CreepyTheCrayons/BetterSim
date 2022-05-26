package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class PortalManager implements Listener {

	@EventHandler
	public void onPortal(EntityPortalEvent event) {
		if(event.getEntity() instanceof Player) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onPortal(PlayerPortalEvent event) {
		if(event.getCause() != PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) return;
		event.setCancelled(true);
		Player player = event.getPlayer();
		Location playerLoc = player.getLocation();

		Location teleportLoc;
		if(player.getWorld() != Bukkit.getWorld("darkzone")) {
			teleportLoc = playerLoc.clone().add(235, 40, -97);
			teleportLoc.setWorld(Bukkit.getWorld("darkzone"));
			teleportLoc.setX(173);
			teleportLoc.setY(92);
			teleportLoc.setZ(-94);
		}
		else {
			teleportLoc = playerLoc.clone().add(-240, -20, 97);
			teleportLoc.setWorld(Bukkit.getWorld("biomes1"));
			teleportLoc.setY(72);
		}


		if(teleportLoc.getYaw() > 0 || teleportLoc.getYaw() < -180) teleportLoc.setYaw(-teleportLoc.getYaw());
		teleportLoc.add(3, 0, 0);


		player.teleport(teleportLoc);
		player.setVelocity(new Vector(1.5, 1, 0));
		if(player.getWorld() == Bukkit.getWorld("darkzone")) {
			Misc.sendTitle(player, "&d&k||&5&lDarkzone&d&k||", 40);
			Misc.sendSubTitle(player, "", 40);
			AOutput.send(player, "&7You have been sent to the &d&k||&5&lDarkzone&d&k||&7.");
		}
		else {
			Misc.sendTitle(player, "&a&lOverworld", 40);
			Misc.sendSubTitle(player, "", 40);
			AOutput.send(player, "&7You have been sent to the &a&lOverworld&7.");

			MusicManager.stopPlaying(player);
		}
	}

	@EventHandler
	public static void onTp(PlayerTeleportEvent event) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if(!MapManager.inDarkzone(event.getPlayer())) MusicManager.stopPlaying(event.getPlayer());
			}
		}.runTaskLater(PitSim.INSTANCE, 10);

	}
}
