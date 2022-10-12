package dev.kyro.pitsim.tutorial;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.DamageManager;
import dev.kyro.pitsim.controllers.SkinManager;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.KillType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.events.OofEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.tutorial.inventories.ApplyEnchantPanel;
import dev.kyro.pitsim.tutorial.inventories.EnchantingGUI;
import dev.kyro.pitsim.tutorial.inventories.EnchantingPanel;
import dev.kyro.pitsim.tutorial.objects.Tutorial;
import dev.kyro.pitsim.tutorial.sequences.*;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class TutorialManager implements Listener {
	public static Map<Player, Tutorial> tutorials = new HashMap<>();

	public static final String DUMMY_SKIN_NAME = "wiji1";

	static {
		SkinManager.loadSkin(DUMMY_SKIN_NAME);
	}

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
		if(isEligable(player)) {
			TutorialManager.createTutorial(player);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if(tutorials.containsKey(player)) tutorials.get(player).cleanUp();

	}

	@EventHandler
	public static void onEnchantingTableClick(PlayerInteractEvent event) {
		if(!tutorials.containsKey(event.getPlayer())) return;
		if(event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();

		if(block.getType() != Material.ENCHANTMENT_TABLE) return;

		Tutorial tutorial = TutorialManager.getTutorial(player);
		if(tutorial == null) return;

		if(tutorial.sequence instanceof InitialMysticWellSequence) {
			BukkitTask runnable = new BukkitRunnable() {
				@Override
				public void run() {
					EnchantingGUI enchantGUI = new EnchantingGUI(player);
					player.openInventory(enchantGUI.getHomePanel().getInventory());
				}
			}.runTaskLater(PitSim.INSTANCE, 1L);
			event.setCancelled(true);
			return;
		}

		if(tutorial.sequence instanceof ViewEnchantsSequence) {
			BukkitTask runnable = new BukkitRunnable() {
				@Override
				public void run() {
					player.openInventory(EnchantingPanel.openEnchantsPanel(player).getInventory());
				}
			}.runTaskLater(PitSim.INSTANCE, 1L);
			event.setCancelled(true);
			return;
		}

		if(tutorial.sequence instanceof ViewEnchantTiersSequence) {
			BukkitTask runnable = new BukkitRunnable() {
				@Override
				public void run() {
					player.openInventory(ApplyEnchantPanel.openEnchantsPanel(player).getInventory());
				}
			}.runTaskLater(PitSim.INSTANCE, 1L);
			event.setCancelled(true);
			return;
		}

		dev.kyro.pitsim.tutorial.inventories.EnchantingGUI enchantingGUI = new EnchantingGUI(player);
		enchantingGUI.open();
		Sounds.MYSTIC_WELL_OPEN_1.play(player);
		Sounds.MYSTIC_WELL_OPEN_2.play(player);

		event.setCancelled(true);
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply event) {
		for(Tutorial tutorial : TutorialManager.tutorials.values()) {
			if(tutorial.player == event.getAttacker()) {
				for(NPC non : tutorial.nons) {
					if(non.getEntity() == event.getDefender()) {
						event.getEvent().setCancelled(true);
						DamageManager.kill(event, event.getAttacker(), event.getDefender(), KillType.DEFAULT);
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

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDeath(KillEvent event) {
		Tutorial tutorial = getTutorial(event.getDeadPlayer());
		if(tutorial == null) return;

		event.getDead().teleport(tutorial.playerSpawn);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onOof(OofEvent event) {
		Tutorial tutorial = getTutorial(event.getPlayer());
		if(tutorial == null) return;

		event.getPlayer().teleport(tutorial.playerSpawn);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDeath(PlayerDeathEvent event) {
		Tutorial tutorial = getTutorial(event.getEntity());
		if(tutorial == null) return;

		event.getEntity().teleport(tutorial.playerSpawn);
	}

//	@EventHandler
//	public void onClose(InventoryCloseEvent event) {
//		Player player = (Player) event.getPlayer();
//		Tutorial tutorial = getTutorial(player);
//		if(tutorial == null) return;
//
//		if(tutorial.sequence instanceof InitialMysticWellSequence && event.getInventory().getName().equals("Mystic Well")) {
//			BukkitTask runnable = new BukkitRunnable() {
//				@Override
//				public void run() {
//					EnchantingGUI enchantGUI = new EnchantingGUI(player);
//					player.openInventory(enchantGUI.getHomePanel().getInventory());
//				}
//			}.runTaskLater(PitSim.INSTANCE, 1L);
//		}
//
//		if(tutorial.sequence instanceof ViewEnchantsSequence && event.getInventory().getName().equals("Choose an Enchant")) {
//			BukkitTask runnable = new BukkitRunnable() {
//				@Override
//				public void run() {
//					player.openInventory(EnchantingPanel.openEnchantsPanel(player).getInventory());
//				}
//			}.runTaskLater(PitSim.INSTANCE, 1L);
//		}
//
//		if(tutorial.sequence instanceof ViewEnchantTiersSequence && event.getInventory().getName().equals("Choose a Level")) {
//			BukkitTask runnable = new BukkitRunnable() {
//				@Override
//				public void run() {
//					player.openInventory(ApplyEnchantPanel.openEnchantsPanel(player).getInventory());
//				}
//			}.runTaskLater(PitSim.INSTANCE, 1L);
//		}
//	}

	public static boolean isEligable(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(pitPlayer.prestige > 0) return false;
		for(ItemStack itemStack : player.getInventory()) {
			if(!Misc.isAirOrNull(itemStack)) return false;
		}

		if(UpgradeManager.hasUpgrade(player, "TENACITY")) return false;

		return !pitPlayer.tutorial;
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		Tutorial tutorial = getTutorial(player);
		if(tutorial == null) return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onOpen(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Tutorial tutorial = getTutorial(player);
		if(tutorial == null) return;

		if(event.getClickedBlock() == null) return;
		if(event.getClickedBlock().getType() == Material.CHEST) {
			event.setCancelled(true);
		}
	}
}
