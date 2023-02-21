package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.aitems.mystics.TaintedScythe;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class TaintedManager implements Listener {
	public static List<Player> players = new ArrayList<>();

	@EventHandler(priority = EventPriority.LOW)
	public void onAttack(EntityDamageByEntityEvent event) {
		if(!(event.getDamager() instanceof Player)) return;
		Player attacker = (Player) event.getDamager();

		ItemStack held = attacker.getItemInHand();
		PitItem pitItem = ItemFactory.getItem(TaintedScythe.class);
		if(!pitItem.isThisItem(held)) return;
		double multiplier = Misc.isCritical((Player) event.getDamager()) ? 1.5 : 1;
		event.setDamage(TaintedScythe.BASE_DAMAGE * multiplier);
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		if(players.contains(event.getPlayer())) return;
		if(!Bukkit.getOnlinePlayers().contains(event.getPlayer())) return;
		players.add(event.getPlayer());

		new BukkitRunnable() {
			@Override
			public void run() {
				players.remove(event.getPlayer());
			}
		}.runTaskLater(PitSim.INSTANCE, 40);
	}

	@EventHandler
	public void onOpen(InventoryOpenEvent event) {
		if(players.contains((Player) event.getPlayer())) event.setCancelled(true);
	}
}
