package dev.kyro.pitsim.enchants.tainted.znotcodeduncommon;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;

import java.util.List;

public class Fortify extends PitEnchant {
	public static Fortify INSTANCE;

	public Fortify() {
		super("Fortify", false, ApplyType.CHESTPLATES,
				"fortify");
		isTainted = true;
		INSTANCE = this;
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {

		return new ALoreBuilder(
				"&7I can't be asked to code this"
		).getLore();
	}
}
