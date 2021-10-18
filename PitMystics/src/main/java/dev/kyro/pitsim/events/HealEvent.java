package dev.kyro.pitsim.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;

public class HealEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	public Player player;
	private final double initialHeal;
	public HealType healType;
	public List<Double> multipliers = new ArrayList<>();

	public HealEvent(Player player, double initialHeal, HealType healType) {
		this.player = player;
		this.initialHeal = initialHeal;
		this.healType = healType;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public double getFinalHeal() {

		double finalHeal = initialHeal;
		for(Double multiplier : multipliers) finalHeal *= multiplier;
		return finalHeal;
	}

	public enum HealType {
		HEALTH,
		ABSORPTION
	}
}
