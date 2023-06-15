package net.pitsim.spigot.megastreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.spigot.battlepass.quests.daily.DailyMegastreakQuest;
import net.pitsim.spigot.controllers.NonManager;
import net.pitsim.spigot.controllers.objects.Megastreak;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.events.AttackEvent;
import net.pitsim.spigot.misc.PitLoreBuilder;
import net.pitsim.spigot.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class StashStreaker extends Megastreak {
	public static StashStreaker INSTANCE;

	public StashStreaker() {
		super("&8Stash Streaker", "stashstreaker", 200, 0, 50);
		INSTANCE = this;
	}

	public static boolean isActive(PitPlayer pitPlayer) {
		if(pitPlayer == null) return false;
		return pitPlayer.getMegastreak() instanceof StashStreaker && pitPlayer.isOnMega();
	}

	@EventHandler
	public void onAttack(AttackEvent.Pre attackEvent) {
		if(!hasMegastreak(attackEvent.getAttackerPlayer())) return;
		PitPlayer pitPlayer = attackEvent.getAttackerPitPlayer();
		if(!pitPlayer.isOnMega() || NonManager.getNon(attackEvent.getDefender()) == null) return;
		attackEvent.setCancelled(true);
		AOutput.error(pitPlayer.player, "&c&lERROR!&7 You cannot attack bots while on " + getCapsDisplayName() + "&7!");
	}

	@Override
	public void proc(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		Sounds.MEGA_GENERAL.play(player.getLocation());
		pitPlayer.stats.timesOnStashStreaker++;
		DailyMegastreakQuest.INSTANCE.onMegastreakComplete(pitPlayer);
	}

	@Override
	public String getPrefix(Player player) {
		return "&8&lSTASH";
	}

	@Override
	public ItemStack getBaseDisplayStack(Player player) {
		return new AItemStackBuilder(Material.CHAINMAIL_LEGGINGS)
				.getItemStack();
	}

	@Override
	public void addBaseDescription(PitLoreBuilder loreBuilder, PitPlayer pitPlayer) {
		loreBuilder.addLore(
				"&7On Trigger:",
				"&a\u25a0 &7Gain access to /ec while not",
				"   &7in combat",
				"",
				"&7BUT:",
				"&c\u25a0 &7You cannot attack bots",
				"",
				"&7On Death:",
				"&e\u25a0 &7Protects your inventory"
		);
	}

	@Override
	public String getSummary() {
		return getCapsDisplayName() + "&7 is a Megastreak that";
	}
}
