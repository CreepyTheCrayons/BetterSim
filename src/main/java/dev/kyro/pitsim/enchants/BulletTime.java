package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Effect;
import org.bukkit.event.EventHandler;

import java.util.List;

public class BulletTime extends PitEnchant {

	public BulletTime() {
		super("Bullet Time", false, ApplyType.SWORDS,
				"bullettime", "bullet-time", "bullet", "bt");
		isUncommonEnchant = true;
	}

	@EventHandler
	public void cancel(AttackEvent.Pre attackEvent) {
		if(!attackEvent.isDefenderPlayer()) return;
		if(!canApply(attackEvent)) return;

		int enchantLvl = attackEvent.getDefenderEnchantLevel(this);
		if(enchantLvl == 0 || attackEvent.getArrow() == null || !(attackEvent.getDefenderPlayer().isBlocking())) return;

		Sounds.BULLET_TIME.play(attackEvent.getDefender());
		attackEvent.getArrow().getWorld().playEffect(attackEvent.getArrow().getLocation(), Effect.EXPLOSION, 0, 30);

		if(enchantLvl > 1) {
			PitPlayer pitDefender = attackEvent.getDefenderPitPlayer();
			pitDefender.heal(getHealing(enchantLvl));
		}
		attackEvent.setCancelled(true);
		attackEvent.getArrow().remove();
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		if(enchantLvl == 1) {
			return new ALoreBuilder("&7Blocking destroys arrows that hit", "&7you").getLore();
		} else {
			return new ALoreBuilder("&7Blocking destroys arrows that hit", "&7you. Destroying arrows this way",
					"&7heals &c" + Misc.getHearts(getHealing(enchantLvl))).getLore();
		}
	}

	public double getHealing(int enchantLvl) {
		return enchantLvl * 0.4 + 0.4;
	}
}
