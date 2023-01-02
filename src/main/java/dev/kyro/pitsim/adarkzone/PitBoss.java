package dev.kyro.pitsim.adarkzone;

import dev.kyro.pitsim.adarkzone.notdarkzone.PitEquipment;
import dev.kyro.pitsim.misc.Misc;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;

import java.util.*;

public abstract class PitBoss {

//	Player related
	public Player summoner;
	public Map<UUID, Double> damageMap = new HashMap<>();
	public DropPool dropPool;

//	Boss related
	public NPC npcBoss;
	public Player boss;
	public TargetingSystem targetingSystem;
	public PitEquipment equipment;

//	Ability Related
	public List<PitBossAbility> abilities = new ArrayList<>();
	public Map<PitBossAbility, Double> routineAbilityMap = new HashMap<>();
	public double skipRoutineChance = 0;
	public long lastRoutineExecuteTick;
	public int routineAbilityCooldownTicks = 20 * 5;

	public PitBoss(Player summoner) {
		this.summoner = summoner;
	}

	public abstract int getMaxHealth();
	public abstract double getReach();
	public abstract double getReachRanged();

//	Internal events (override to add functionality)
	public void onSpawn() {}
	public void onDeath() {}

	public PitBoss abilities(PitBossAbility... pitBossAbilities) {
		abilities = Arrays.asList(pitBossAbilities);
		for(PitBossAbility ability : abilities) {
			if(!ability.runsOnRoutine) continue;
			routineAbilityMap.put(ability, ability.routineWeight);
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
}
