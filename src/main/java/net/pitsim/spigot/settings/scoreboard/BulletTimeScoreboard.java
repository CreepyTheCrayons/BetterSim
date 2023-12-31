package net.pitsim.spigot.settings.scoreboard;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.spigot.controllers.Cooldown;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.enchants.overworld.BulletTime;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BulletTimeScoreboard extends ScoreboardOption {

	@Override
	public String getDisplayName() {
		return "&9Bullet Time";
	}

	@Override
	public String getRefName() {
		return "bullettime";
	}

	@Override
	public String getValue(PitPlayer pitPlayer) {
		if(!BulletTime.INSTANCE.cooldowns.containsKey(pitPlayer.player.getUniqueId())) return null;
		Cooldown cooldown = BulletTime.INSTANCE.cooldowns.get(pitPlayer.player.getUniqueId());
		if(!cooldown.isOnCooldown()) return null;
		int seconds = (int) Math.ceil(cooldown.getTicksLeft() / 20.0);
		return "&6Bullet Time: &e" + seconds + "s";
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		ItemStack itemStack = new AItemStackBuilder(Material.ARROW)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7Shows the current cooldown",
						"&7left on " + BulletTime.INSTANCE.getDisplayName(),
						"&7when applicable"
				)).getItemStack();
		return itemStack;
	}
}
