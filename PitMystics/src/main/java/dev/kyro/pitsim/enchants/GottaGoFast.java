package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GottaGoFast extends PitEnchant {
	public static Map<Player, Integer> gtgfMap = new HashMap<>();
	public static GottaGoFast INSTANCE;

	public GottaGoFast() {
		super("Gotta go fast", false, ApplyType.PANTS,
				"gotta-go-fast", "gottagofast", "gtgf", "gotta", "fast");
		INSTANCE = this;
		isUncommonEnchant = true;
	}

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					Map<PitEnchant, Integer> enchantMap = EnchantManager.getEnchantsOnPlayer(player);
					int enchantLvl = enchantMap.getOrDefault(INSTANCE, 0);
					int oldEnchantLvl = gtgfMap.getOrDefault(player, 0);

					if(enchantLvl != oldEnchantLvl) {
						gtgfMap.put(player, enchantLvl);
						player.setWalkSpeed(getWalkSpeed(enchantLvl));
					}
				}
			}
		}.runTaskTimerAsynchronously(PitSim.INSTANCE, 0L, 20);
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Move &e" + Misc.roundString(getWalkSpeedLore(enchantLvl)) + "&e% faster &7at all times").getLore();
	}

	public static float getWalkSpeed(int enchantLvl) {

		return 0.2F + (0.2F * (getWalkSpeedLore(enchantLvl) / 100));
	}

	public static float getWalkSpeedLore(int enchantLvl) {

		return enchantLvl * 5 + 5;
	}
}
