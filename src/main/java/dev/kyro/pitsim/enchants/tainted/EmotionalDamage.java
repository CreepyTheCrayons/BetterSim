package dev.kyro.pitsim.enchants.tainted;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enchants.ComboVenom;
import dev.kyro.pitsim.enums.ApplyType;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class EmotionalDamage extends PitEnchant {
	public static EmotionalDamage INSTANCE;

	public EmotionalDamage() {
		super("Emotional Damage", true, ApplyType.CHESTPLATES,
				"aoe");
		isTainted = true;
		INSTANCE = this;
		meleOnly = true;
	}

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					if(!MapManager.inDarkzone(player)) continue;
					int level = EnchantManager.getEnchantLevel(player, INSTANCE);
					if(ComboVenom.isVenomed(player) || level == 0) continue;

					for(Entity nearbyEntity : player.getNearbyEntities(5, 5, 5)) {
						if(!(nearbyEntity instanceof LivingEntity) || nearbyEntity instanceof Player) continue;
						if(!shouldAdd(level)) continue;
						if(nearbyEntity instanceof ArmorStand || nearbyEntity instanceof Villager) continue;
						LivingEntity livingEntity = (LivingEntity) nearbyEntity;
						double newHealth = livingEntity.getHealth() - 5;
						if(newHealth <= 0) {
							livingEntity.damage(1000, player);
						} else {
							livingEntity.damage(0);
							livingEntity.setHealth(newHealth);
						}
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 20L);
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {

		return new ALoreBuilder("&7Deal area damage to mobs around", "&7you", "&d&o-" + reduction(enchantLvl) + "% Mana Regen").getLore();
	}

	public static boolean shouldAdd(int level) {
		return level * 0.2 > Math.random();
	}

	public static int reduction(int enchantLvl) {
		return 80 - (20 * enchantLvl);
	}
}
