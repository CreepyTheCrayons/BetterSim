package dev.kyro.pitsim.enchants.tainted.common;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;

import java.util.List;

public class Territorial extends PitEnchant {
	public static Territorial INSTANCE;

	public Territorial() {
		super("Territorial", false, ApplyType.CHESTPLATES,
				"territorial", "territory");
		isTainted = true;
		INSTANCE = this;
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {

		return new ALoreBuilder(
				"&7A basic tainted enchant"
		).getLore();
	}
}
