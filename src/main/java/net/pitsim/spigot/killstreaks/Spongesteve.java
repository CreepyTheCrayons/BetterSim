package net.pitsim.spigot.killstreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.spigot.controllers.objects.Killstreak;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.events.HealEvent;
import net.pitsim.spigot.misc.Misc;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Spongesteve extends Killstreak {
	public static int AMOUNT = 50;
	public static Spongesteve INSTANCE;

	public Spongesteve() {
		super("Spongesteve", "Spongesteve", 40, 8);
		INSTANCE = this;
	}

	@Override
	public void proc(Player player) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		pitPlayer.heal(AMOUNT, HealEvent.HealType.ABSORPTION, AMOUNT * 2);
	}

	@Override
	public void reset(Player player) {

	}

	@Override
	public ItemStack getDisplayStack(Player player) {
		AItemStackBuilder builder = new AItemStackBuilder(Material.SPONGE)
				.setName("&e" + displayName)
				.setLore(new ALoreBuilder(
						"&7Every: &c" + killInterval + " kills",
						"",
						"&7Gain &6" + Misc.getHearts(AMOUNT) + " Absorption&7."
				));

		return builder.getItemStack();
	}

	@Override
	public String getSummary() {
		return "&eSpongesteve&7 is a killstreak that gives &9" + Misc.getHearts(AMOUNT) +
				"&7 of &9absorptio&7 hearts every &c40 kills";
	}
}
