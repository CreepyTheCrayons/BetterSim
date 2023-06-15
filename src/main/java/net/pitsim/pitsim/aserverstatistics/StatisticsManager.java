package net.pitsim.pitsim.aserverstatistics;

import net.pitsim.pitsim.PitSim;
import net.pitsim.pitsim.adarkzone.BossManager;
import net.pitsim.pitsim.adarkzone.DarkzoneManager;
import net.pitsim.pitsim.adarkzone.PitBoss;
import net.pitsim.pitsim.adarkzone.PitMob;
import net.pitsim.pitsim.controllers.MapManager;
import net.pitsim.pitsim.controllers.objects.PitEnchant;
import net.pitsim.pitsim.enchants.overworld.Regularity;
import net.pitsim.pitsim.events.AttackEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StatisticsManager implements Listener {
	public static final long CHUNK_CAPTURE_DURATION = 1000 * 60 * 60;

	private static StatisticDataChunk dataChunk = new StatisticDataChunk();

	public StatisticsManager() {
		new BukkitRunnable() {
			@Override
			public void run() {
				getDataChunk();
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 20L * 60);
	}

	@EventHandler(ignoreCancelled = true)
	public void onAttack(AttackEvent.Post attackEvent) {
		logAttack(attackEvent);
	}

	public static void logAttack(AttackEvent attackEvent) {
		if(PitSim.isDev()) return;
		if(!attackEvent.isAttackerRealPlayer() || attackEvent.isFakeHit() || Regularity.isRegHit(attackEvent.getDefender()) ||
				attackEvent.getAttacker() == attackEvent.getDefender()) return;

		List<StatisticCategory> applicableCategories = new ArrayList<>();
		PitMob defenderMob = DarkzoneManager.getPitMob(attackEvent.getDefender());
		PitBoss defenderBoss = BossManager.getPitBoss(attackEvent.getDefender());
		if(PitSim.status.isOverworld() && attackEvent.isDefenderPlayer() && attackEvent.getAttacker().getWorld() == MapManager.currentMap.world) {
			if(MapManager.currentMap.getMid().distance(attackEvent.getAttacker().getLocation()) < 10) {
				applicableCategories.add(StatisticCategory.OVERWORLD_STREAKING);
			} else {
				applicableCategories.add(StatisticCategory.OVERWORLD_PVP);
			}
		}

		if(PitSim.status.isDarkzone()) {
			if(attackEvent.isDefenderRealPlayer()) {
				applicableCategories.add(StatisticCategory.DARKZONE_VS_PLAYER);
			} else if(defenderMob != null) {
				applicableCategories.add(StatisticCategory.DARKZONE_VS_MOB);
			} else if(defenderBoss != null) {
				applicableCategories.add(StatisticCategory.DARKZONE_VS_BOSS);
			}
		}

		for(StatisticCategory category : applicableCategories) logAttackInCategory(category, attackEvent.getAttackerEnchantMap());
	}

	public static void logAttackInCategory(StatisticCategory category, Map<PitEnchant, Integer> enchantMap) {
		StatisticDataChunk.Record defaultRecord = getDefaultRecord(category);
		defaultRecord.logAttack(enchantMap);
		for(Map.Entry<PitEnchant, Integer> entry : enchantMap.entrySet()) {
			PitEnchant pitEnchant = entry.getKey();
			StatisticDataChunk.Record record = getRecord(pitEnchant, category);
			record.logAttack(enchantMap);
		}
	}

	public static void resetDataChunk() {
		dataChunk = new StatisticDataChunk();
	}

	public static StatisticDataChunk getDataChunk() {
		if(dataChunk.hasExpired()) {
			dataChunk.send();
			dataChunk = new StatisticDataChunk();
		}
		return dataChunk;
	}

	public static StatisticDataChunk.Record getRecord(PitEnchant pitEnchant, StatisticCategory category) {
		StatisticDataChunk dataChunk = getDataChunk();
		for(StatisticDataChunk.Record record : dataChunk.records) {
			if(record.getPitEnchant() != pitEnchant || record.getCategory() != category) continue;
			return record;
		}
		throw new RuntimeException();
	}

	public static StatisticDataChunk.Record getDefaultRecord(StatisticCategory category) {
		StatisticDataChunk dataChunk = getDataChunk();
		for(StatisticDataChunk.Record record : dataChunk.records) {
			if(record.getPitEnchant() != null || record.getCategory() != category) continue;
			return record;
		}
		throw new RuntimeException();
	}
}
