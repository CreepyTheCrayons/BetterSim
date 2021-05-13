package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.DamageEvent;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DiamondStomp extends PitEnchant {

	public DiamondStomp() {
		super("Diamond Stomp", false, ApplyType.SWORDS,
				"stomp", "ds", "dstomp", "diamond-stomp");
	}

	@Override
	public DamageEvent onDamage(DamageEvent damageEvent) {

		int enchantLvl = damageEvent.getEnchantLevel(this);
		if(enchantLvl == 0) return damageEvent;

		for(ItemStack armorContent : damageEvent.defender.getInventory().getArmorContents()) {
			if(!(armorContent.getType() == Material.DIAMOND_HELMET || armorContent.getType() == Material.DIAMOND_CHESTPLATE
			|| armorContent.getType() == Material.DIAMOND_LEGGINGS || armorContent.getType() == Material.DIAMOND_BOOTS))
				return damageEvent;
		}

		damageEvent.increasePercent += getDamage(enchantLvl) / 100D;

		return damageEvent;
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Deal &c+" + getDamage(enchantLvl) + "% &7damage vs. players",
				"&7wearing diamond armor").getLore();
	}

	//	TODO: Sharp damage calculation
	
	public int getDamage(int enchantLvl) {

		switch(enchantLvl) {
			case 1:
				return 6;
			case 2:
				return 12;
			case 3:
				return 25;

		}

		return 0;
	}
}
