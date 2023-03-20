package dev.kyro.pitsim.adarkzone.altar.pedestals;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.altar.AltarManager;
import dev.kyro.pitsim.adarkzone.altar.AltarPedestal;
import dev.kyro.pitsim.cosmetics.particles.RedstoneParticle;
import dev.kyro.pitsim.misc.Sounds;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;

public class AltarAnimation {

	public static final int STREAM_TICKS = 40;

	public Player player;
	public int totalSouls;

	public List<AltarPedestal> activatedPedestals;
	public BukkitRunnable onComplete;

	public final Map<AltarPedestal, BukkitTask> streamRunnables = new HashMap<>();
	public final List<EntityItem> soulItems = new ArrayList<>();
	public final List<PedestalSpin> pedestalSpins = new ArrayList<>();

	public AltarAnimation(Player player, int totalSouls, List<AltarPedestal> activatedPedestals, BukkitRunnable onComplete) {
		this.player = player;
		this.totalSouls = totalSouls;
		this.activatedPedestals = activatedPedestals;
		this.onComplete = onComplete;

		playSoulAnimation();
//		drawPedestalStreams();
	}

	public void playSoulAnimation() {
		Location location = AltarManager.TEXT_LOCATION.clone().add(0, 1, 0);

		new BukkitRunnable() {
			public final double RADIUS = 1.25;
			public final int degree_interval = 15;
			public int ticks = 0;

			@Override
			public void run() {
				double x = Math.cos(Math.toRadians(ticks * degree_interval)) * RADIUS;
				double z = Math.sin(Math.toRadians(ticks * degree_interval)) * RADIUS;

				World world = ((CraftWorld) (player.getWorld())).getHandle();
				EntityItem entityItem = new EntityItem(world);
				entityItem.setPosition(location.getX() + x, location.getY(), location.getZ() + z);
				entityItem.setItemStack(CraftItemStack.asNMSCopy(getItemStack()));

				PacketPlayOutSpawnEntity spawn = new PacketPlayOutSpawnEntity(entityItem, 2, 1);
				((CraftPlayer) player).getHandle().playerConnection.sendPacket(spawn);

				PacketPlayOutEntityMetadata meta = new PacketPlayOutEntityMetadata(entityItem.getId(), entityItem.getDataWatcher(), true);
				((CraftPlayer) player).getHandle().playerConnection.sendPacket(meta);

				PacketPlayOutEntityVelocity velocity = new PacketPlayOutEntityVelocity(entityItem.getId(), Math.random() * 0.02, 0, Math.random() * 0.02);
				((CraftPlayer) player).getHandle().playerConnection.sendPacket(velocity);

				ticks ++;
				soulItems.add(entityItem);
				Sounds.SOUL_DROP.play(player, 1f, Math.min(0.05f * ticks, 2f));

				if(soulItems.size() >= (totalSouls / 5) + 4) {
					cancel();
					drawPedestalStreams();
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0, 1);
	}

	public void drawPedestalStreams() {
		int delay = 0;
		for(AltarPedestal pedestal : activatedPedestals) {
			new BukkitRunnable() {
				@Override
				public void run() {
					drawPedestalStream(pedestal);
					pedestalSpins.add(new PedestalSpin(player, pedestal));
				}
			}.runTaskLater(PitSim.INSTANCE, delay);
			delay += 40;
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				for(BukkitTask value : streamRunnables.values()) {
					value.cancel();
				}
			}
		}.runTaskLater(PitSim.INSTANCE, delay + 20);

		new BukkitRunnable() {
			@Override
			public void run() {
				endAnimation();
			}
		}.runTaskLater(PitSim.INSTANCE, delay + 50);
	}

	public void drawPedestalStream(AltarPedestal pedestal) {
		RedstoneParticle particle = new RedstoneParticle();

		streamRunnables.put(pedestal, new BukkitRunnable() {
			public int ticks = 0;
			public final int PARTICLES_PER_TICK = 1;

			public final List<Location> previousLocations = new ArrayList<>();

			@Override
			public void run() {
				PedestalSpin pedestalSpin = null;
				for(PedestalSpin spin : pedestalSpins) {
					if(spin.pedestal == pedestal) {
						pedestalSpin = spin;
					}
				}

				double y = pedestalSpin == null ? 0 : pedestalSpin.getLocationY();
				Vector vector = AltarManager.ALTAR_CENTER.clone().subtract(0, 0.25, 0).toVector().subtract(pedestal.location.clone().add(0, 1.75 + y, 0).toVector());

				for(int i = 0; i < PARTICLES_PER_TICK; i++) {
					double percent = (double) ticks / (double) STREAM_TICKS;
					Location location = pedestal.location.clone().add(0, 1.75, 0).add(vector.clone().multiply(percent));
					previousLocations.add(location);
					for(Location previousLocation : previousLocations) {
						particle.display(player, previousLocation.clone().add(0, y, 0), pedestal.getParticleColor());
					}
					Sounds.BEAM_CONNECT.play(player, 0.1f, Math.min(0.05f * ticks, 2f));
				}

				if(ticks >= STREAM_TICKS) {
					for(Location previousLocation : previousLocations) {
						particle.display(player, previousLocation.clone().add(0, y, 0), pedestal.getParticleColor());
					}
					return;
				}

				ticks++;
			}
		}.runTaskTimer(PitSim.INSTANCE, 0, 1));
	}

	public void endAnimation() {
		if(onComplete != null) onComplete.runTask(PitSim.INSTANCE);
		for(EntityItem soulItem : soulItems) {
			PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(soulItem.getId());
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(destroy);
		}

		for(BukkitTask streamRunnable : streamRunnables.values()) {
			streamRunnable.cancel();
		}

		for(PedestalSpin pedestalSpin : pedestalSpins) {
			pedestalSpin.stop();
		}

		AltarManager.animations.remove(this);
	}

	public static ItemStack getItemStack() {
		ItemStack item = new ItemStack(org.bukkit.Material.GHAST_TEAR);
		ItemMeta meta = item.getItemMeta();
		meta.setLore(Collections.singletonList(UUID.randomUUID().toString()));
		item.setItemMeta(meta);
		return item;
	}
}
