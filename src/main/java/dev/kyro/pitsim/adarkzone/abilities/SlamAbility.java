package dev.kyro.pitsim.adarkzone.abilities;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.RoutinePitBossAbility;
import dev.kyro.pitsim.cosmetics.ParticleOffset;
import dev.kyro.pitsim.cosmetics.PitParticle;
import dev.kyro.pitsim.cosmetics.particles.BlockCrackParticle;
import dev.kyro.pitsim.cosmetics.particles.ExplosionLargeParticle;
import dev.kyro.pitsim.cosmetics.particles.SmokeLargeParticle;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.misc.effects.FallingBlock;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SlamAbility extends RoutinePitBossAbility {
	public int radius;
	public int blockCount;
	public boolean isActive = false;
	List<Player> effectedPlayers = new ArrayList<>();

	public SlamAbility(double routineWeight, int radius, int blockCount) {
		super(routineWeight);
		this.radius = radius;
		this.blockCount = blockCount;
	}

	@Override
	public void onRoutineExecute() {
		isActive = true;
		Location centerLocation = pitBoss.boss.getLocation().clone().subtract(0, 1, 0);

		List<Block> applicableBlocks = new ArrayList<>();

		for(int x = -5; x < 6; x++) {
			for(int z = -5; z < 6; z++) {
				Location blockLocation = centerLocation.clone().add(x, 0, z);

				if(blockLocation.distance(centerLocation) > radius) continue;

				if(blockLocation.getBlock().getType() != Material.AIR && blockLocation.clone().add(0, 1, 0).getBlock().getType() == Material.AIR) {
					applicableBlocks.add(blockLocation.getBlock());
					continue;
				}

				for(int i = -2; i < 3; i++) {
					Location checkPosition = blockLocation.clone().add(0, i, 0);
					if(checkPosition.getBlock().getType() == Material.AIR || checkPosition.clone().add(0, 1, 0).getBlock().getType() != Material.AIR)
						continue;
					applicableBlocks.add(checkPosition.getBlock());
				}
			}
		}

		List<Location> usedLocations = new ArrayList<>();
		Random random = new Random();

		for(int i = 0; i < blockCount; i++) {
			int index = random.nextInt(applicableBlocks.size());
			Location spawnLocation = applicableBlocks.get(index).getLocation();
			if(usedLocations.contains(spawnLocation)) {
				i--;
				continue;
			}
			usedLocations.add(spawnLocation);
		}

		for(int i = 0; i < usedLocations.size(); i++) {
			Block block = usedLocations.get(i).getBlock();
			int delay = i * 2;

			new BukkitRunnable() {
				@Override
				public void run() {
					GravitizedBlock gravitizedBlock = new GravitizedBlock(block);
					gravitizedBlock.slamAfter(20 - delay);
				}
			}.runTaskLater(PitSim.INSTANCE, delay);
		}
	}

	public class GravitizedBlock {
		public Block block;
		public Location initialLocation;
		public FallingBlock fallingBlock;
		
		public BukkitTask runnable;

		public GravitizedBlock(Block block) {
			this.block = block;
			this.initialLocation = block.getLocation().add(0, 1, 0);
			spawnBlock();
		}

		public void spawnBlock() {
			fallingBlock = new FallingBlock(block.getType(), block.getData(), initialLocation);
			fallingBlock.setViewers(getViewers());
			fallingBlock.spawnBlock();

			new BukkitRunnable() {
				@Override
				public void run() {
					fallingBlock.setVelocity(new Vector(0, 0.7, 0));
				}
			}.runTaskLater(PitSim.INSTANCE, 1);


			runnable = new BukkitRunnable() {
				@Override
				public void run() {
					fallingBlock.setVelocity(new Vector(0, 0.1, 0));
				}
			}.runTaskTimer(PitSim.INSTANCE, 10, 1);
		}

		public void slamAfter(int ticks) {
			new BukkitRunnable() {
				@Override
				public void run() {
					slam();
				}
			}.runTaskLater(PitSim.INSTANCE, ticks);
		}

		public void slam() {
			runnable.cancel();
			Vector vector = new Vector(0, -0.5, 0);
			fallingBlock.setVelocity(vector);
			fallingBlock.removeAfter(10);
		}
	}

	public List<Player> getViewers() {
		List<Player> viewers = new ArrayList<>();
		for(Entity entity : pitBoss.boss.getNearbyEntities(50, 50, 50)) {
			if(!(entity instanceof Player)) continue;
			Player player = Bukkit.getPlayer(entity.getUniqueId());
			if(player != null) viewers.add(player);
		}
		return viewers;
	}
}

