package dev.kyro.pitsim.perks;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitPerk;
import dev.kyro.pitsim.events.AttackEvent;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class JewelHunter extends PitPerk {

	public static JewelHunter INSTANCE;

	public JewelHunter() {
		super("Jewel Hunter", "jewelhunter", new ItemStack(Material.GOLD_SWORD), 21, false, "", INSTANCE, false);
		INSTANCE = this;
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.attackerIsPlayer || !attackEvent.defenderIsPlayer) return;
		if(!playerHasUpgrade(attackEvent.attacker)) return;

		if(MapManager.currentMap.lobbies.contains(attackEvent.defenderPlayer.getWorld()) &&
				MapManager.currentMap.getMid(attackEvent.defender.getWorld()).distance(attackEvent.defenderPlayer.getLocation()) < getRange()) {
			return;
		}

		double damageIncrease = 0;

		ItemStack heldItem = attackEvent.defender.getEquipment().getItemInHand();
		if(EnchantManager.isJewel(heldItem)) damageIncrease += getDamageIncrease() / 100.0;

		ItemStack pantsItem = attackEvent.defender.getEquipment().getLeggings();
		if(EnchantManager.isJewel(pantsItem)) damageIncrease += getDamageIncrease() / 100.0;

		attackEvent.increasePercent += damageIncrease;
	}

	@Override
	public List<String> getDescription() {
		return new ALoreBuilder("&7Outside &emiddle&7, Deal &c+" + getDamageIncrease() + "% &7damage for",
				"&7each jewel your opponent has", "&7(holding or wearing)").getLore();
	}

	public int getDamageIncrease() {
		return 30;
	}

	public static int getRange() {
		return 12;
	}
}
