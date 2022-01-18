package dev.kyro.pitsim.tutorial;

import dev.kyro.pitsim.controllers.DamageManager;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.tutorial.inventories.EnchantingGUI;
import dev.kyro.pitsim.tutorial.objects.Tutorial;
import dev.kyro.pitsim.tutorial.sequences.*;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

public class TutorialManager implements Listener {
	public static Map<Player, Tutorial> tutorials = new HashMap<>();

	public static void createTutorial(Player player) {
		Tutorial tutorial = new Tutorial(player, getOpenPosition());
		tutorials.put(player, tutorial);
	}


	public static Tutorial getTutorial(Player player) {
		if(!tutorials.containsKey(player)) return null;
		return tutorials.get(player);
	}

	public static int getOpenPosition() {
		int highestPosition = -1;
		for(Tutorial tutorial : tutorials.values()) {
			if(tutorial.position > highestPosition) highestPosition = tutorial.position;
		}
		return highestPosition + 1;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		TutorialManager.createTutorial(player);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if(tutorials.containsKey(player)) tutorials.get(player).cleanUp();
	}

	@EventHandler
	public static void onEnchantingTableClick(PlayerInteractEvent event) {
		if(!tutorials.containsKey(event.getPlayer())) return;
		if(getTutorial(event.getPlayer()).sequence instanceof InitialMysticWellSequence ||
				getTutorial(event.getPlayer()).sequence instanceof ViewEnchantsSequence ||
				getTutorial(event.getPlayer()).sequence instanceof ViewEnchantTiersSequence) return;
		if(event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();

		if(block.getType() != Material.ENCHANTMENT_TABLE) return;

		event.setCancelled(true);

		dev.kyro.pitsim.tutorial.inventories.EnchantingGUI enchantingGUI = new EnchantingGUI(player);
		enchantingGUI.open();
		Sounds.MYSTIC_WELL_OPEN_1.play(player);
		Sounds.MYSTIC_WELL_OPEN_2.play(player);
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply event) {
		for(Tutorial tutorial : TutorialManager.tutorials.values()) {
			if(tutorial.player == event.attacker) {
				for(NPC non : tutorial.nons) {
					if(non.getEntity() == event.defender) {
						event.event.setCancelled(true);
						DamageManager.kill(event, event.attacker, event.defender, false);
					}
				}
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if(event.getClickedBlock() == null) return;
		if(event.getClickedBlock().getType() == Material.ENCHANTMENT_TABLE) {
			Tutorial tutorial = getTutorial(event.getPlayer());
			if(tutorial == null) return;

			if(tutorial.sequence instanceof EnchantBillLsSequence ||
					tutorial.sequence instanceof EnchantRGMSequence ||
					tutorial.sequence instanceof EnchantMegaDrainSequence) return;

			event.setCancelled(true);
		}
	}
}
