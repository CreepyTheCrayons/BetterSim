package net.pitsim.spigot.placeholders;

import dev.kyro.arcticapi.hooks.papi.APAPIPlaceholder;
import net.pitsim.spigot.perks.Gladiator;
import org.bukkit.entity.Player;

public class GladiatorPlaceholder implements APAPIPlaceholder {

	@Override
	public String getIdentifier() {
		return "gladiator_reduction";
	}

	@Override
	public String getValue(Player player) {
		if(!Gladiator.INSTANCE.hasPerk(player)) return "None";
		int reduction = Gladiator.getReduction(player);
		if(reduction == 0) return "None";

		return "&9-" + reduction + "%";
	}
}
