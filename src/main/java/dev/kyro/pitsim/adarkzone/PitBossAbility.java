package dev.kyro.pitsim.adarkzone;

import dev.kyro.pitsim.PitSim;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class PitBossAbility implements Listener {
	private PitBoss pitBoss;
	private boolean enabled = true;
	private double routineWeight;

	public PitBossAbility() {
		Bukkit.getPluginManager().registerEvents(this, PitSim.INSTANCE);
	}

	public PitBossAbility(double routineWeight) {
		this();
		this.routineWeight = routineWeight;
	}

//	Internal events (override to add functionality)
	public void onRoutineExecute() {}
	public boolean shouldExecuteRoutine() {
		return true;
	}

	public PitBossAbility pitBoss(PitBoss pitBoss) {
		this.pitBoss = pitBoss;
		return this;
	}

	public boolean isAssignedBoss(LivingEntity entity) {
		return getPitBoss().boss == entity;
	}

	public void disable() {
		enabled = false;
		HandlerList.unregisterAll(this);
	}

	public PitBoss getPitBoss() {
		return pitBoss;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public double getRoutineWeight() {
		return routineWeight;
	}
}
