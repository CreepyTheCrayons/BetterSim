package dev.kyro.pitsim.adarkzone;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.notdarkzone.PitEquipment;
import dev.kyro.pitsim.misc.Misc;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.npc.ai.CitizensNavigator;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public abstract class PitBoss {

//	Player related
	public Player summoner;
	public Map<UUID, Double> damageMap = new HashMap<>();
	public DropPool dropPool;

//	Boss related
	public NPC npcBoss;
	public Player boss;
	public SubLevel subLevel;
	public TargetingSystem targetingSystem;
	public PitEquipment equipment;

//	Ability Related
	public List<PitBossAbility> abilities = new ArrayList<>();
	public Map<PitBossAbility, Double> routineAbilityMap = new HashMap<>();
	public double skipRoutineChance = 0;
	public long lastRoutineExecuteTick;
	public int routineAbilityCooldownTicks = 20 * 5;
	public BukkitTask routineRunnable;

	public PitBoss(Player summoner) {
		this.summoner = summoner;
		this.targetingSystem = new TargetingSystem(this);
	}

	public abstract Class<? extends SubLevel> assignSubLevel();
	public abstract String getName();
	public abstract String getSkinName();
	public abstract int getMaxHealth();
	public abstract double getReach();
	public abstract double getReachRanged();

//	Internal events (override to add functionality)
	public void onSpawn() {}
	public void onDeath() {}

	public PitBoss abilities(PitBossAbility... pitBossAbilities) {
		abilities = Arrays.asList(pitBossAbilities);
		for(PitBossAbility ability : abilities) {
			ability.pitBoss(this);
			if(!(ability instanceof RoutinePitBossAbility)) continue;
			RoutinePitBossAbility routineAbility = (RoutinePitBossAbility) ability;
			routineAbilityMap.put(ability, routineAbility.getRoutineWeight());
		}
		return this;
	}

//	Where chance is a percent chance 0-100
	public PitBoss routineAbilitySkip(double chance) {
		skipRoutineChance = chance;
		return this;
	}

	public void delayNextRoutine(int ticks) {
		lastRoutineExecuteTick += ticks;
	}

	public PitBossAbility getRoutineAbility() {
		Map<PitBossAbility, Double> routineAbilityMap = new HashMap<>(this.routineAbilityMap);
		for(Map.Entry<PitBossAbility, Double> entry : new ArrayList<>(routineAbilityMap.entrySet()))
			if(!entry.getKey().shouldExecuteRoutine()) routineAbilityMap.remove(entry.getKey());
		return Misc.weightedRandom(routineAbilityMap);
	}

	public void spawn() {
		npcBoss = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, getName());
		npcBoss.setProtected(false);
		npcBoss.spawn(subLevel.getBossSpawnLocation());
		boss = (Player) npcBoss.getEntity();

		CitizensNavigator navigator = (CitizensNavigator) npcBoss.getNavigator();
		navigator.getDefaultParameters()
				.attackDelayTicks(10)
				.attackRange(getReach());
		targetingSystem.pickTarget();

		new BukkitRunnable() {
			@Override
			public void run() {
				for(PitBoss pitBoss : pitBosses) {
					if(pitBoss.lastRoutineExecuteTick + pitBoss.routineAbilityCooldownTicks > PitSim.currentTick) return;
					if(pitBoss.skipRoutineChance != 0 && Math.random() * 100 < pitBoss.skipRoutineChance) return;
					pitBoss.lastRoutineExecuteTick = PitSim.currentTick;

					PitBossAbility routineAbility = pitBoss.getRoutineAbility();
					routineAbility.onRoutineExecute();
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 20);
	}
}
