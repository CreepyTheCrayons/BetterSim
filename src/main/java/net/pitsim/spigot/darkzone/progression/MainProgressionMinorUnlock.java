package net.pitsim.spigot.darkzone.progression;


import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import net.pitsim.spigot.enums.UnlockState;
import net.pitsim.spigot.controllers.objects.PitPlayer;
import net.pitsim.spigot.misc.Misc;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MainProgressionMinorUnlock extends MainProgressionUnlock {

	public MainProgressionMinorUnlock(String id, int guiXPos, int guiYPos) {
		super(id, guiXPos, guiYPos);
	}

	@Override
	public String getDisplayName() {
		return "&5Progression Path";
	}

	@Override
	public ItemStack getDisplayStack(PitPlayer pitPlayer, UnlockState unlockState) {
		int cost = ProgressionManager.getUnlockCost(pitPlayer, this);
		ItemStack baseStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) unlockState.data);
		ALoreBuilder loreBuilder = new ALoreBuilder();

		ProgressionManager.addPurchaseCostLore(this, loreBuilder, unlockState, pitPlayer.taintedSouls, cost, false);
		if(unlockState == UnlockState.UNLOCKED) Misc.addEnchantGlint(baseStack);

		return new AItemStackBuilder(baseStack)
				.setName(unlockState.chatColor + "Path Unlock")
				.setLore(loreBuilder)
				.getItemStack();
	}
}