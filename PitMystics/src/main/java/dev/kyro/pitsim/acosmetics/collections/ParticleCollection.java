package dev.kyro.pitsim.acosmetics.collections;

import dev.kyro.pitsim.RedstoneColor;
import dev.kyro.pitsim.acosmetics.ParticleOffset;
import dev.kyro.pitsim.acosmetics.PitParticle;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParticleCollection {
	public Map<String, List<ParticleData>> particleCollectionMap = new HashMap<>();

	public static class ParticleData {
		public PitParticle pitParticle;
		public ParticleOffset particleOffset;

		public ParticleData(PitParticle pitParticle, ParticleOffset particleOffset) {
			this.pitParticle = pitParticle;
			this.particleOffset = particleOffset;
		}
	}

	public ParticleCollection addParticle(int ref, PitParticle pitParticle, ParticleOffset particleOffset) {
		return addParticle(String.valueOf(ref), pitParticle, particleOffset);
	}

	public ParticleCollection addParticle(String ref, PitParticle pitParticle, ParticleOffset particleOffset) {
		particleCollectionMap.putIfAbsent(ref, new ArrayList<>());
		particleCollectionMap.get(ref).add(new ParticleData(pitParticle, particleOffset));
		return this;
	}

	public ParticleCollection addParticles(String ref, List<ParticleData> particleDataList) {
		for(ParticleData particleData : particleDataList) addParticle(ref, particleData.pitParticle, particleData.particleOffset);
		return this;
	}

	public void display(int ref, EntityPlayer entityPlayer, Location location) {
		display(String.valueOf(ref), entityPlayer, location, null);
	}

	public void display(String ref, EntityPlayer entityPlayer, Location location) {
		display(ref, entityPlayer, location, null);
	}

	public void display(int ref, EntityPlayer entityPlayer, Location location, RedstoneColor redstoneColor) {
		display(String.valueOf(ref), entityPlayer, location, redstoneColor);
	}

	public void display(String ref, EntityPlayer entityPlayer, Location location, RedstoneColor redstoneColor) {
		for(ParticleData particleData : particleCollectionMap.get(ref)) {
			particleData.pitParticle.display(entityPlayer, location, particleData.particleOffset, redstoneColor);
		}
	}

	public void displayAll(EntityPlayer entityPlayer, Location location) {
		displayAll(entityPlayer, location, null);
	}

	public void displayAll(EntityPlayer entityPlayer, Location location, RedstoneColor redstoneColor) {
		for(Map.Entry<String, List<ParticleData>> entry : particleCollectionMap.entrySet()) {
			for(ParticleData particleData : entry.getValue()) {
				particleData.pitParticle.display(entityPlayer, location, particleData.particleOffset, redstoneColor);
			}
		}
	}
}
