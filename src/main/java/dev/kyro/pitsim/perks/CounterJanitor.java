package dev.kyro.pitsim.perks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.objects.PitPerk;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.upgrades.UnlockCounterJanitor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class CounterJanitor extends PitPerk {
	public static CounterJanitor INSTANCE;

	public CounterJanitor() {
		super("Counter-Janitor", "counter-janitor");
		renownUpgradeClass = UnlockCounterJanitor.class;
		INSTANCE = this;
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!hasPerk(killEvent.getKiller())) return;
		if(killEvent.isKillerPlayer() && NonManager.getNon(killEvent.getDead()) == null) {
			PitPlayer pitPlayer = killEvent.getKillerPitPlayer();
			double missingHealth = killEvent.getKiller().getMaxHealth() - killEvent.getKiller().getHealth();
			pitPlayer.heal(missingHealth / 2);
		}
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		return new AItemStackBuilder(Material.SPONGE)
				.getItemStack();
	}

	@Override
	public PitLoreBuilder getBaseDescription() {
		return new PitLoreBuilder(
				"&7Instantly heal half your &chealth &7on player kill"
		);
	}

	@Override
	public String getSummary() {
		return "&eCounter-Janitor is a perk unlocked in the &erenown shop&7 that &cheals you&7 for substantially " +
				"on player kill. This perk is incompatible with &cVampire";
	}
}
