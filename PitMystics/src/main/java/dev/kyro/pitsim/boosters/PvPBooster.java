package dev.kyro.pitsim.boosters;

import dev.kyro.pitsim.controllers.objects.Booster;
import dev.kyro.pitsim.events.KillEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PvPBooster extends Booster {
	public PvPBooster() {
		super("XP Booster", "xp");
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!isActive()) return;
		killEvent.playerKillWorth *= 2;
//		TODO: not lose lives
	}

	@Override
	public List<String> getDescription() {
		return null;
	}

	@Override
	public ItemStack getDisplayItem() {
		return null;
	}
}
