package dev.kyro.pitsim.enchants.tainted.znotcodeduncommon;

import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.misc.PitLoreBuilder;

import java.util.List;

public class Mending extends PitEnchant {
	public static Mending INSTANCE;

	public Mending() {
		super("Mending", false, ApplyType.CHESTPLATES,
				"mending", "mend");
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
