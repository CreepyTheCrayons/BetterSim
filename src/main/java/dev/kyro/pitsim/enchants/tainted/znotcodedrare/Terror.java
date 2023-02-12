package dev.kyro.pitsim.enchants.tainted.znotcodedrare;

import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.misc.PitLoreBuilder;

import java.util.List;

public class Terror extends PitEnchant {
	public static Terror INSTANCE;

	public Terror() {
		super("Terror", true, ApplyType.CHESTPLATES,
				"terror");
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
