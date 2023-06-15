package net.pitsim.spigot.upgrades;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import net.pitsim.spigot.controllers.UpgradeManager;
import net.pitsim.spigot.controllers.objects.UnlockableRenownUpgrade;
import net.pitsim.spigot.events.KillEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class Celebrity extends UnlockableRenownUpgrade {
	public static Celebrity INSTANCE;

	public Celebrity() {
		super("Celebrity", "CELEBRITY", 40);
		INSTANCE = this;
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!UpgradeManager.hasUpgrade(killEvent.getKillerPlayer(), this)) return;
		killEvent.goldMultipliers.add(2.0);
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		return new AItemStackBuilder(Material.RAW_FISH, 1, 3)
				.getItemStack();
	}

	@Override
	public String getEffect() {
		return "&7Literally earn &62x gold &7from kills";
	}

	@Override
	public String getSummary() {
		return "&6Celebrity &7is a &erenown&7 upgrade that doubles the &6gold&7 you get on all bot and player kills";
	}

	@Override
	public int getUnlockCost() {
		return 300;
	}
}
