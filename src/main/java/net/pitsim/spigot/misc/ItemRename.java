package net.pitsim.spigot.misc;

import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.events.PitQuitEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class ItemRename implements Listener {
	public static Map<Player, ItemStack> renamePlayers = new HashMap<>();

	public static void renameItem(Player player, ItemStack item) {

		renamePlayers.remove(player);
		renamePlayers.put(player, item);
		AOutput.send(player, "&a&lPlease type your desired name for the item that you were holding");
		AOutput.send(player, "&7&o(You may include color codes using the & symbol)");

		new BukkitRunnable() {
			@Override
			public void run() {
				renamePlayers.remove(player);
			}
		}.runTaskLater(PitSim.INSTANCE, 1200L);
	}

	@EventHandler
	public void onLeave(PitQuitEvent event) {
		renamePlayers.remove(event.getPlayer());
	}
}
