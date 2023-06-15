package net.pitsim.pitsim.adarkzone.abilities.minion;

import net.pitsim.pitsim.PitSim;
import net.pitsim.pitsim.adarkzone.SubLevelType;
import net.pitsim.pitsim.events.AttackEvent;
import org.bukkit.event.EventHandler;

public class DefensiveMinionAbility extends MinionAbility {

	public int spawnAmount;
	public long cooldownTicks;
	public long lastSpawn = 0;

	public DefensiveMinionAbility(SubLevelType type, int spawnAmount, int maxMobs, long cooldownTicks) {
		super(type, maxMobs);

		this.spawnAmount = spawnAmount;
		this.cooldownTicks = cooldownTicks;
	}

	@EventHandler
	public void onHit(AttackEvent.Apply event) {
		if(event.getDefender() != getPitBoss().getBoss()) return;
		if(lastSpawn + cooldownTicks > PitSim.currentTick) return;
		lastSpawn = PitSim.currentTick;

		spawnMobs(getPitBoss().getBoss().getLocation().add(0, 2, 0), spawnAmount);
	}
}
