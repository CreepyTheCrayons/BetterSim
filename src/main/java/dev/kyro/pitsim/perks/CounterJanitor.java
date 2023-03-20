package dev.kyro.pitsim.perks;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.objects.PitPerk;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.KillEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CounterJanitor extends PitPerk {
	public static CounterJanitor INSTANCE;

	public CounterJanitor() {
		super("Counter-Janitor", "counter-janitor", new ItemStack(Material.SPONGE), 19, true, "COUNTER_JANITOR", INSTANCE, false);
		INSTANCE = this;
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!playerHasUpgrade(killEvent.getKiller())) return;
		if(MapManager.inDarkzone(killEvent.getKiller())) return;
		if(killEvent.isKillerPlayer() && NonManager.getNon(killEvent.getDead()) == null) {
			PitPlayer pitPlayer = killEvent.getKillerPitPlayer();
			double missingHealth = killEvent.getKiller().getMaxHealth() - killEvent.getKiller().getHealth();
			pitPlayer.heal(missingHealth / 2);
		}
	}

	@Override
	public List<String> getDescription() {
		return new ALoreBuilder("&7Instantly heal half your", "&chealth &7on player kill.").getLore();
	}

	@Override
	public String getSummary() {
		return "&eCounter-Janitor is a perk unlocked in the &erenown shop&7 that &cheals you&7 for substantially " +
				"on player kill. This perk is incompatible with &cVampire";
	}
}
