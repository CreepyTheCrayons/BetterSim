package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.Non;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.DeathCry;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.DeathCrys;
import dev.kyro.pitsim.misc.KillEffects;
import dev.kyro.pitsim.misc.Misc;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.TextComponent;
import net.royawesome.jlibnoise.module.combiner.Max;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.bossbar.BossBar;
import org.inventivetalent.bossbar.BossBarAPI;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerManager implements Listener {

	public static List<UUID> swapCooldown = new ArrayList<>();

	@EventHandler
	public static void onKill(KillEvent killEvent) {

		PitPlayer pitKiller = PitPlayer.getPitPlayer(killEvent.killer);
		PitPlayer pitDead = PitPlayer.getPitPlayer(killEvent.dead);
		Non killingNon = NonManager.getNon(killEvent.killer);
		Non deadNon = NonManager.getNon(killEvent.dead);

		if(pitKiller.killEffect != null && killEvent.killer.hasPermission("pitsim.killeffect")) {
			KillEffects.trigger(killEvent.killer, pitKiller.killEffect, killEvent.dead.getLocation());
		}

		if(pitDead.deathCry != null && killEvent.dead.hasPermission("pitsim.deathcry")) {
			DeathCrys.trigger(killEvent.dead, pitDead.deathCry, killEvent.dead.getLocation());
		}


		if(pitDead.bounty != 0 && killingNon == null) {
			DecimalFormat formatter = new DecimalFormat("#,###.#");

			for(Player player : Bukkit.getOnlinePlayers()) {
				PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
				if(pitPlayer.disabledBounties) continue;


				String bounty1 = ChatColor.translateAlternateColorCodes('&',
						"&6&lBOUNTY CLAIMED!&7 %luckperms_prefix%" + killEvent.killer.getDisplayName() + "&7 killed ");
				String bounty2 = ChatColor.translateAlternateColorCodes('&', "%luckperms_prefix%" + killEvent.dead.getDisplayName()
						+ "&7 for &6&l" + formatter.format(pitDead.bounty)) + "g";
				String bounty3 = PlaceholderAPI.setPlaceholders(killEvent.killer, bounty1);
				String bounty4 = PlaceholderAPI.setPlaceholders(killEvent.dead, bounty2);
				player.sendMessage(bounty3 + bounty4);



			}
			PitSim.VAULT.depositPlayer(killEvent.killer, pitDead.bounty);
			pitDead.bounty = 0;


		}

		if(Math.random() < 0.1 && killingNon == null) {

			int amount = (int) Math.floor(Math.random() * 5 + 1) * 200		;
			pitKiller.bounty += amount;
			String message = "&6&lBOUNTY!&7 bump &6&l" + amount + "g&7 on %luckperms_prefix%" + killEvent.killer.getDisplayName() +
					"&7 for high streak";
			if(!pitKiller.disabledBounties) AOutput.send(killEvent.killer, PlaceholderAPI.setPlaceholders(killEvent.killer, message));
			ASound.play(killEvent.killer, Sound.WITHER_SPAWN, 1, 1);
		}
	}

	@EventHandler
	public static void onClick(PlayerInteractEvent event) {

		Player player = event.getPlayer();
		if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if(Misc.isAirOrNull(player.getItemInHand()) || !player.getItemInHand().getType().toString().contains("LEGGINGS")) return;

		if(swapCooldown.contains(player.getUniqueId())) {

			ASound.play(player, Sound.VILLAGER_NO, 1F, 1F);
			return;
		}

		ItemStack held = player.getItemInHand();
		player.setItemInHand(player.getInventory().getLeggings());
		player.getInventory().setLeggings(held);

		swapCooldown.add(player.getUniqueId());
		new BukkitRunnable() {
			@Override
			public void run() {
				swapCooldown.remove(player.getUniqueId());
			}
		}.runTaskLater(PitSim.INSTANCE, 40L);

		ASound.play(player, Sound.HORSE_ARMOR, 1F, 1.3F);
	}
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		new BukkitRunnable() {
			@Override
			public void run() {
				event.getPlayer().teleport(MapManager.getPlayerSpawn());
			}
		}.runTaskLater(PitSim.INSTANCE, 10L);

	}

	@EventHandler
	public void onItemDamage(PlayerItemDamageEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {

		Player player = event.getPlayer();
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		Location spawnLoc = MapManager.getPlayerSpawn();
		player.teleport(spawnLoc);

		new BukkitRunnable() {
				@Override
				public void run() {

					String message = "%luckperms_prefix%";
					pitPlayer.prefix = "&7[&e" + pitPlayer.playerLevel + "&7] " + PlaceholderAPI.setPlaceholders(player, message);
				}
			}.runTaskLater(PitSim.INSTANCE,  10L);

//		if(!player.isOp()) {
//			BypassManager.bypassAll.add(player);
//			Misc.sendTitle(player, ChatColor.translateAlternateColorCodes('&', "&c&lSYNCING WORLD"), 200);
//			new BukkitRunnable() {
//				int count = 0;
//				@Override
//				public void run() {
//					if((count != 0 && !player.isOnline()) || count++ >= 80) {
//						cancel();
//						BypassManager.bypassAll.remove(player);
//						return;
//					}
//
//					Location spawnLoc = new Location(Bukkit.getWorld("pit"), -108.5, 86, 194.5, 45, 0);
//					player.teleport(spawnLoc);
//				}
//			}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
//		}

		new BukkitRunnable() {
			@Override
			public void run() {

				if(!player.isOp() && !player.getName().equals("Fishduper")) {

					int itemsRemoved = 0;
					for(int i = 0; i < 36; i++) {

						ItemStack itemStack = player.getInventory().getItem(i);
						if(EnchantManager.isIllegalItem(itemStack)) {
							player.getInventory().setItem(i, new ItemStack(Material.AIR));
							itemsRemoved++;
						}
					}
					if(EnchantManager.isIllegalItem(player.getEquipment().getLeggings())) {
						player.getEquipment().setLeggings(new ItemStack(Material.AIR));
						itemsRemoved++;
					}
					if(itemsRemoved != 0) AOutput.error(player, "&c" + itemsRemoved + " &7illegal item" +
							(itemsRemoved == 1 ? " was" : "s were") + " removed from your inventory");
				}

				pitPlayer.updateMaxHealth();
				player.setHealth(player.getMaxHealth());
			}
		}.runTaskLater(PitSim.INSTANCE, 1L);
	}
}
