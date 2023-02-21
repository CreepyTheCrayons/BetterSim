package dev.kyro.pitsim.enchants.tainted.znotcodeduncommon;

import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.misc.PitLoreBuilder;

import java.util.List;

public class ShieldBuster extends PitEnchant {
	public static ShieldBuster INSTANCE;

	public ShieldBuster() {
		super("Shield Buster", false, ApplyType.SCYTHES,
				"shieldbuster", "buster");
		isUncommonEnchant = true;
		isTainted = true;
		INSTANCE = this;
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7I can't be asked to code this"
		).getLore();
	}
}
