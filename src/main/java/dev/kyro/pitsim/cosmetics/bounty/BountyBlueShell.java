package dev.kyro.pitsim.cosmetics.bounty;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.cosmetics.CosmeticType;
import dev.kyro.pitsim.cosmetics.PitCosmetic;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BountyBlueShell extends PitCosmetic {

	public BountyBlueShell() {
		super("&9Blue &bShell", "blueshell", CosmeticType.BOUNTY_CLAIM_MESSAGE);
	}

	@Override
	public String getBountyClaimMessage(String killerName, String deadName, String bounty) {
		return killerName + "&7 threw a blue shell at " + deadName + "&7 and collected " + bounty;
	}

	@Override
	public ItemStack getRawDisplayItem() {
		ItemStack itemStack = new AItemStackBuilder(Material.LAPIS_BLOCK)
				.setName(getDisplayName())
				.setLore(new ALoreBuilder(
						"&7You'll always overtake first",
						"&7place now!"
				))
				.getItemStack();
		return itemStack;
	}
}
