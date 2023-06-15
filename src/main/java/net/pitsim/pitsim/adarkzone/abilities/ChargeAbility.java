package net.pitsim.pitsim.adarkzone.abilities;

import net.pitsim.pitsim.adarkzone.PitBossAbility;
import net.pitsim.pitsim.cosmetics.ParticleOffset;
import net.pitsim.pitsim.cosmetics.particles.ExplosionLargeParticle;
import net.pitsim.pitsim.cosmetics.particles.FlameParticle;
import net.pitsim.pitsim.cosmetics.particles.SmokeLargeParticle;
import net.pitsim.pitsim.misc.Sounds;
import org.bukkit.entity.Player;

public class ChargeAbility extends PitBossAbility {
	public ChargeAbility(double routineWeight) {
		super(routineWeight);
	}

	@Override
	public void onRoutineExecute() {
		Player target = getPitBoss().getBossTargetingSystem().target;
		if(target == null) return;

		Sounds.CHARGE.play(getPitBoss().getBoss().getLocation(), 30);

		SmokeLargeParticle smoke = new SmokeLargeParticle();
		FlameParticle flame = new FlameParticle();
		ExplosionLargeParticle explosion = new ExplosionLargeParticle();

		for(Player viewer : getViewers()) {
			for(int i = 0; i < 50; i++) {
				smoke.display(viewer, getPitBoss().getBoss().getLocation(), new ParticleOffset(1, 2, 1, 5, 4, 5));
				flame.display(viewer, getPitBoss().getBoss().getLocation(), new ParticleOffset(1, 2, 1, 5, 4, 5));
			}
			explosion.display(viewer, getPitBoss().getBoss().getLocation());
		}

		getPitBoss().getBoss().setVelocity(target.getLocation().toVector()
				.subtract(getPitBoss().getBoss().getLocation().toVector()).normalize().setY(0.2).normalize().multiply(1.8));
	}

	@Override
	public boolean shouldExecuteRoutine() {
		Player target = getPitBoss().getBossTargetingSystem().target;
		if(target == null) return false;

		return target.getLocation().distance(getPitBoss().getBoss().getLocation()) >= 5;
	}
}
