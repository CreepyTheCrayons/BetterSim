package dev.kyro.pitsim.enchants.tainted.common;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;

import java.util.List;

public class NocturnalPredator extends PitEnchant {
	public static NocturnalPredator INSTANCE;

	public NocturnalPredator() {
		super("Nocturnal Predator", false, ApplyType.SCYTHES,
				"nocturnalpredator", "nocturnal", "predator");
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
