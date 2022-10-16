package dev.kyro.pitsim.acosmetics.particles;

import dev.kyro.pitsim.RedstoneColor;
import dev.kyro.pitsim.acosmetics.PitCosmetic;
import dev.kyro.pitsim.acosmetics.PitParticle;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Location;

public class RedstoneParticle extends PitParticle {
	public RedstoneColor redstoneColor;

	public RedstoneParticle(PitCosmetic pitCosmetic, RedstoneColor redstoneColor) {
		super(pitCosmetic);
		this.redstoneColor = redstoneColor;
	}

	@Override
	public void display(EntityPlayer entityPlayer, Location location) {
		entityPlayer.playerConnection.sendPacket(new PacketPlayOutWorldParticles(
				EnumParticle.REDSTONE, true, (float) location.getX(), (float) location.getY(), (float) location.getZ(),
				redstoneColor.red, redstoneColor.green, redstoneColor.blue, redstoneColor.brightness, 0
		));
	}
}
