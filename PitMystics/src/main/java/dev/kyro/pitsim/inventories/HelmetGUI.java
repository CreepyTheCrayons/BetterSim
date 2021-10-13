package dev.kyro.pitsim.inventories;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class HelmetGUI extends AGUI {

	public static HelmetPanel helmetPanel;

	public HelmetGUI(Player player) {
		super(player);

		helmetPanel = new HelmetPanel(this);

		setHomePanel(helmetPanel);
	}

	public static Map<Player, ItemStack> depositPlayers = new HashMap<>();

	public static void deposit(Player player, ItemStack helmet) {

		depositPlayers.remove(player);
		depositPlayers.put(player, helmet);
		AOutput.send(player, "&a&lPlease the amount of gold you wish to deposit into the helmet.");

		new BukkitRunnable() {
			@Override
			public void run() {
				depositPlayers.remove(player);
			}
		}.runTaskLater(PitSim.INSTANCE, 1200L);

	}

}
