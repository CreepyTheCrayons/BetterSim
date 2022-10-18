package dev.kyro.pitsim.acosmetics.particles;

import dev.kyro.pitsim.ParticleColor;
import dev.kyro.pitsim.acosmetics.PitCosmetic;
import dev.kyro.pitsim.acosmetics.PitParticle;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Location;

public class SpellMobAmbientParticle extends PitParticle {
	public SpellMobAmbientParticle(PitCosmetic pitCosmetic) {
		super(pitCosmetic);
	}

	@Override
	public void display(EntityPlayer entityPlayer, Location location, ParticleColor particleColor) {
		entityPlayer.playerConnection.sendPacket(new PacketPlayOutWorldParticles(
				EnumParticle.SPELL_MOB_AMBIENT, true, (float) location.getX(), (float) location.getY(), (float) location.getZ(),
				particleColor.red, particleColor.green, particleColor.blue, particleColor.brightness, 0
		));
	}
}
