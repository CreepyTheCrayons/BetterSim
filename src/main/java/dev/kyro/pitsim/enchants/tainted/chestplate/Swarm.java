package dev.kyro.pitsim.enchants.tainted.chestplate;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.cosmetics.misc.kyrocosmetic.SwarmParticle;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.ManaRegenEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Swarm extends PitEnchant {
	public static Map<Player, List<SwarmParticle>> particleMap = new HashMap<>();
	public static Swarm INSTANCE;

	public Swarm() {
		super("Swarm", true, ApplyType.CHESTPLATES,
				"swarm");
		isTainted = true;
		INSTANCE = this;

		if(!isEnabled()) return;

		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					particleMap.putIfAbsent(onlinePlayer, new ArrayList<>());
					List<SwarmParticle> particleList = particleMap.get(onlinePlayer);

					int enchantLvl = EnchantManager.getEnchantsOnPlayer(onlinePlayer).getOrDefault(INSTANCE, 0);
					int targetParticles = getParticleCount(enchantLvl);

					if(particleList.size() == targetParticles) continue;

					PitPlayer pitPlayer = PitPlayer.getPitPlayer(onlinePlayer);
					if(!pitPlayer.hasManaUnlocked()) continue;

					for(int i = particleList.size(); i < targetParticles; i++) particleList.add(new SwarmParticle(onlinePlayer));
					while(particleList.size() > targetParticles) particleList.remove(0).remove();
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 1L);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if(!particleMap.containsKey(player)) return;
		for(SwarmParticle swarmParticle : particleMap.remove(player)) swarmParticle.remove();
	}

	@EventHandler
	public void onManaRegen(ManaRegenEvent event) {
		Player player = event.getPlayer();
		int enchantLvl = EnchantManager.getEnchantsOnPlayer(player).getOrDefault(INSTANCE, 0);
		if(enchantLvl == 0) return;
		event.multipliers.add(Misc.getReductionMultiplier(getReduction(enchantLvl)));
	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7When equipped, spawn &2" + getParticleCount(enchantLvl) + " swarm particle" +
				(getParticleCount(enchantLvl) == 1 ? "" : "s") + "&7, but regain mana &b" +
				getReduction(enchantLvl) + "% &7slower"
		).getLore();
	}

	@Override
	public String getSummary() {
		return getDisplayName(false, true) + " &7is a &5Darkzone &7enchant that " +
				"summons particles that attacks nearby players and mobs";
	}

	public static int getReduction(int enchantLvl) {
		return 60;
	}

	public static int getParticleCount(int enchantLvl) {
		if(enchantLvl == 0) return 0;
		return enchantLvl + 1;
	}
}
