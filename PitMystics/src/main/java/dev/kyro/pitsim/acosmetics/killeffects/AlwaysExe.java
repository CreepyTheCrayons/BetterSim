package dev.kyro.pitsim.acosmetics.killeffects;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.acosmetics.CosmeticType;
import dev.kyro.pitsim.acosmetics.PitCosmetic;
import dev.kyro.pitsim.controllers.PlayerManager;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class AlwaysExe extends PitCosmetic {

	public AlwaysExe() {
		super("&aAlways Exe", "alwaysexe", CosmeticType.KILL_EFFECT);
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!PlayerManager.isRealPlayer(killEvent.getKillerPlayer()) || !isEnabled(killEvent.getKillerPitPlayer()) || killEvent.isExeDeath()) return;
		Sounds.EXE.play(killEvent.getKillerPlayer());
		killEvent.getKillerPlayer().playEffect(killEvent.getDeadPlayer().getLocation().add(0, 1, 0), Effect.STEP_SOUND, 152);
	}

	@Override
	public ItemStack getRawDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.GOLD_SWORD)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"",
						"&dRARE! &9Executioner",
						"&7Always applies the executioner",
						"&7kill effect"
				))
				.getItemStack();
		return itemStack;
	}
}
