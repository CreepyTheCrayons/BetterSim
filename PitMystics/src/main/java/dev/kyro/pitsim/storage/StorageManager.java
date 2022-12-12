package dev.kyro.pitsim.storage;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.events.MessageEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StorageManager implements Listener {
	private static final List<StorageProfile> profiles = new ArrayList<>();

	public static StorageProfile getProfile(Player player) {
		for(StorageProfile profile : profiles) {
			if(profile.getUUID().equals(player.getUniqueId())) return profile;
		}

		StorageProfile profile = new StorageProfile(player.getUniqueId());
		profiles.add(profile);

		return profile;
	}

	public static StorageProfile getProfile(UUID uuid) {
		for(StorageProfile profile : profiles) {
			if(profile.getUUID().equals(uuid)) return profile;
		}

		StorageProfile profile = new StorageProfile(uuid);
		profiles.add(profile);

		return profile;
	}

	public static StorageProfile getInitialProfile(UUID uuid) {
		StorageProfile profile = new StorageProfile(uuid, 18);
		profiles.add(profile);

		return profile;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent event) {
		if(PitSim.getStatus() == PitSim.ServerStatus.ALL) return;

		Player player = event.getPlayer();
		StorageProfile profile = getProfile(player);
		profile.playerHasBeenOnline = true;

		if(!profile.hasData()) {
			player.kickPlayer(ChatColor.RED + "An error occurred when loading your data. Please report this issue.");
			return;
		}

		player.getInventory().setContents(profile.getCachedInventory());
		player.getInventory().setArmorContents(profile.getArmor());
		player.updateInventory();
	}

	public static void quitInitiate(Player player) {
		StorageProfile profile = getProfile(player);

		profile.cachedInventory = player.getInventory().getContents();
		profile.armor = player.getInventory().getArmorContents();
	}

	public static void quitCleanup(Player player) {
		if(PitSim.getStatus() == PitSim.ServerStatus.ALL) return;

		StorageProfile profile = getProfile(player);

		profiles.remove(profile);
		player.getInventory().clear();
		player.getInventory().setArmorContents(new ItemStack[] {new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR)});
	}

	@EventHandler
	public void onPluginMessage(MessageEvent event) {
		PluginMessage message = event.getMessage();
		List<String> strings = message.getStrings();

		if(strings.size() < 2) return;

		if(strings.get(0).equals("SAVE CONFIRMATION")) {
			UUID uuid = UUID.fromString(strings.get(1));

			StorageProfile profile = getProfile(uuid);

			profile.receiveSaveConfirmation(message);
		}

		if(strings.get(0).equals("LOAD REQUEST")) {
			UUID uuid = UUID.fromString(strings.get(1));
			System.out.println("Loading profile for: " + uuid);

			StorageProfile profile = getInitialProfile(uuid);
		}

		if(strings.get(0).equals("PLAYER DATA")) {
			UUID uuid = UUID.fromString(strings.get(1));

			StorageProfile profile = getProfile(uuid);
			message.getStrings().remove(0);
			message.getStrings().remove(0);
			profile.setData(message);
		}
	}

	@EventHandler
	public void onPickup(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		StorageProfile profile = getProfile(player);
		if(profile.hasData() && profile.isSaving()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPickup(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		StorageProfile profile = getProfile(player);
		if(profile.hasData() && profile.isSaving()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onClick(InventoryDragEvent event) {
		Player player = (Player) event.getWhoClicked();
		StorageProfile profile = getProfile(player);
		if(profile.hasData() && profile.isSaving()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		StorageProfile profile = getProfile(player);
		if(profile.hasData() && profile.isSaving()) {
			event.setCancelled(true);
		}

		for(int i = 0; i < profile.enderChest.length; i++) {
			Inventory inv = profile.enderChest[i];

			if(inv.equals(event.getClickedInventory())) {

				if(event.getSlot() == 36 && i > 0) {
					player.openInventory(profile.enderChest[i - 1]);
					event.setCancelled(true);
					return;
				}

				if(event.getSlot() == 44 && (i + 1) < StorageProfile.ENDERCHEST_PAGES) {
					player.openInventory(profile.enderChest[i + 1]);
					event.setCancelled(true);
					return;
				}

				if(event.getSlot() == 40) {
					EnderchestGUI gui = new EnderchestGUI(player);
					gui.open();
					event.setCancelled(true);
					return;
				}

				if(event.getSlot() < 9 || event.getSlot() > 35) {
					event.setCancelled(true);
				}
			}
		}
	}



}
