package net.pitsim.pitsim.enchants.tainted.common;

import net.pitsim.pitsim.adarkzone.PitMob;
import net.pitsim.pitsim.adarkzone.mobs.PitIronGolem;
import net.pitsim.pitsim.adarkzone.mobs.PitWolf;
import net.pitsim.pitsim.controllers.objects.BasicDarkzoneEnchant;
import net.pitsim.pitsim.enums.ApplyType;

import java.util.Arrays;
import java.util.List;

public class Territorial extends BasicDarkzoneEnchant {
	public static Territorial INSTANCE;

	public Territorial() {
		super("Territorial", false, ApplyType.CHESTPLATES,
				"territorial", "territory");
		isTainted = true;
		INSTANCE = this;
	}

	@Override
	public int getBaseStatPercent(int enchantLvl) {
		return enchantLvl * 5 + 3;
	}

	@Override
	public boolean isOffensive() {
		return false;
	}

	@Override
	public List<Class<? extends PitMob>> getApplicableMobs() {
		return Arrays.asList(PitWolf.class, PitIronGolem.class);
	}
}
