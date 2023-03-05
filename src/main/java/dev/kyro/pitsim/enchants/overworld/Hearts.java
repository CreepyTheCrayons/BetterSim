package dev.kyro.pitsim.enchants.overworld;

import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;

import java.util.List;
import java.util.Map;

public class Hearts extends PitEnchant {
	public static Hearts INSTANCE;

	public Hearts() {
		super("Hearts", false, ApplyType.PANTS,
				"hearts", "heart", "health");
		INSTANCE = this;
	}

	public static int getExtraHealth(Map<PitEnchant, Integer> enchantMap) {
		if(!INSTANCE.isEnabled()) return 0;

		if(!enchantMap.containsKey(INSTANCE)) return 0;
		int enchantLvl = enchantMap.get(INSTANCE);

		return getExtraHealth(enchantLvl);
	}
	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Increase your max health by &c" + Misc.getHearts(getExtraHealth(enchantLvl))
		).getLore();
	}

	public static int getExtraHealth(int enchantLvl) {
		return enchantLvl * 2;
	}
}
