package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.enums.NonState;
import dev.kyro.pitsim.enums.NonTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.npc.ai.CitizensNavigator;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import net.citizensnpcs.util.NMS;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Non {

	public NPC npc;
	public Player non;
	public Player target;

	public List<NonTrait> traits = new ArrayList<>();
	public double persistence;
	public NonState nonState = NonState.RESPAWNING;
	public int count = 0;

	public Non(String name) {

		this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, name);
		spawn();
		this.non = (Player) npc.getEntity();
		NonManager.nons.add(this);

		CitizensNavigator navigator = (CitizensNavigator) npc.getNavigator();
		navigator.getDefaultParameters()
				.attackDelayTicks((int) (Math.random() * 4 + 3))
				.attackRange(4);
		npc.setProtected(false);

		pickTraits();

		persistence = (Math.random() * 3 + 94) / 100D;
		if(traits.contains(NonTrait.IRON_STREAKER)) persistence -= 100 - persistence;

		respawn();
	}

	public void tick() {

		non = (Player) npc.getEntity();
		if(npc.isSpawned() && non.getLocation().getY() <= 42) {
			Location teleportLoc = non.getLocation().clone();
			teleportLoc.setY(43.2);
			non.teleport(teleportLoc);
			return;
		}

		if(nonState != NonState.FIGHTING) {
			npc.getNavigator().setTarget(null, true);
			return;
		}

		pickTarget();
		npc.getNavigator().setTarget(target, true);

		if(traits.contains(NonTrait.IRON_STREAKER))
				Misc.applyPotionEffect(non, PotionEffectType.DAMAGE_RESISTANCE, 9999, 2, true, false);

		if(target == null) return;

		if(count % 3 == 0 && (!traits.contains(NonTrait.NO_JUMP)) || Math.random() < 0.05) {

			Block underneath = non.getLocation().clone().subtract(0, 0.2, 0).getBlock();
			if(underneath.getType() != Material.AIR) {

				int rand = (int) (Math.random() * 2);
				Location rotLoc = non.getLocation().clone();
				rotLoc.setYaw(non.getLocation().getYaw() + (rand == 0 ? -90 : 90));

				double distance = target.getLocation().distance(non.getLocation());
				Vector sprintVelo = target.getLocation().toVector().subtract(non.getLocation().toVector())
						.normalize();

				if(distance < Math.random() * 1.5 + 1.5) sprintVelo.multiply(-0.16).setY(0.4); else sprintVelo.multiply(0.4).setY(0.4);
				non.setVelocity(sprintVelo);
			}
		}

		count++;
	}

	public void pickTarget() {

//		if(target != null && Math.random() < persistence) return;

		Player closest = null;
		double closestDistance = 100;
		Location midLoc = new Location(Bukkit.getWorld("pit"), -119, 43, 205);
		for(Entity nearbyEntity : non.getWorld().getNearbyEntities(midLoc, 10, 10, 10)) {

			if(!(nearbyEntity instanceof Player) || nearbyEntity.getUniqueId().equals(non.getUniqueId())) continue;
			double targetDistanceFromMid = Math.sqrt(Math.pow(nearbyEntity.getLocation().getX() - midLoc.getX(), 2) +
					Math.pow(nearbyEntity.getLocation().getZ() - midLoc.getZ(), 2));
			if(targetDistanceFromMid > 9) continue;

			double distance = nearbyEntity.getLocation().distance(non.getLocation());
			if(distance >= closestDistance) continue;

			closest = (Player) nearbyEntity;
			closestDistance = distance;
		}
		target = closest;
	}

	public void spawn() {
		Location spawnLoc = new Location(Bukkit.getWorld("pit"), -119, 86, 211, -180, 60);
		npc.spawn(spawnLoc);
//		skin(npc, "wiji1", npc.getStoredLocation());
	}

	public void respawn() {

		nonState = NonState.RESPAWNING;
		Location spawnLoc = new Location(Bukkit.getWorld("pit"), -119, 86, 211, -180, 60);
//		npc.teleport(spawnLoc, PlayerTeleportEvent.TeleportCause.PLUGIN);
		npc.despawn();
		npc.spawn(spawnLoc);
//		skin(npc, "wiji1", npc.getStoredLocation());

		non.setHealth(non.getMaxHealth());

		Equipment equipment = npc.getTrait(Equipment.class);
		if(traits.contains(NonTrait.IRON_STREAKER)) {

			equipment.set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.DIAMOND_SWORD));
			equipment.set(Equipment.EquipmentSlot.HELMET, new ItemStack(Material.IRON_HELMET));
			equipment.set(Equipment.EquipmentSlot.CHESTPLATE, new ItemStack(Material.IRON_CHESTPLATE));
			equipment.set(Equipment.EquipmentSlot.LEGGINGS, new ItemStack(Material.IRON_LEGGINGS));
			equipment.set(Equipment.EquipmentSlot.BOOTS, new ItemStack(Material.IRON_BOOTS));
		} else {
			equipment.set(Equipment.EquipmentSlot.HAND, new ItemStack(Material.IRON_SWORD));
			equipment.set(Equipment.EquipmentSlot.CHESTPLATE, new ItemStack(Material.CHAINMAIL_CHESTPLATE));
			equipment.set(Equipment.EquipmentSlot.LEGGINGS, new ItemStack(Material.CHAINMAIL_LEGGINGS));
			equipment.set(Equipment.EquipmentSlot.BOOTS, new ItemStack(Material.CHAINMAIL_BOOTS));

			int rand = (int) (Math.random() * 3);
			switch(rand) {
				case 0:
					equipment.set(Equipment.EquipmentSlot.CHESTPLATE, new ItemStack(Material.IRON_CHESTPLATE));
					break;
				case 1:
					equipment.set(Equipment.EquipmentSlot.LEGGINGS, new ItemStack(Material.IRON_LEGGINGS));
					break;
				case 2:
					equipment.set(Equipment.EquipmentSlot.BOOTS, new ItemStack(Material.IRON_BOOTS));
					break;
			}
		}

		new BukkitRunnable() {
			@Override
			public void run() {

				Vector velo = non.getLocation().getDirection().normalize().multiply(0.7);
				velo.setY(0.35);
				non.setVelocity(velo);

				new BukkitRunnable() {
					@Override
					public void run() {
						nonState = NonState.FIGHTING;
					}
				}.runTaskLater(PitSim.INSTANCE, 55L);
			}
		}.runTaskLater(PitSim.INSTANCE, (long) (Math.random() * 20 + 20));
	}

	public void pickTraits() {


		if(Math.random() < 0.7) {

			traits.add(NonTrait.NO_JUMP);
		}
		if(Math.random() < 0.12) {

			traits.add(NonTrait.IRON_STREAKER);
		}
	}

	public void rewardKill() {

		non.setHealth(Math.min(non.getHealth() + 3, non.getMaxHealth()));
		EntityPlayer nmsPlayer = ((CraftPlayer) non).getHandle();
		if(nmsPlayer.getAbsorptionHearts() < 8) {
			nmsPlayer.setAbsorptionHearts(Math.min(nmsPlayer.getAbsorptionHearts() + 3, 5));
		}
	}

	public void remove() {

		NonManager.nons.remove(this);
		npc.destroy();
	}

	public void skin(NPC npc, String name, Location loc) {
		npc.data().set(NPC.PLAYER_SKIN_UUID_METADATA, name);
		npc.data().set(NPC.PLAYER_SKIN_USE_LATEST, false);
		if (npc.isSpawned()) {
			SkinnableEntity skinnable = (SkinnableEntity) npc.getEntity();
			if (skinnable != null) {
				skinnable.setSkinName(name);
			}
		}else {
			npc.spawn(loc);
			SkinnableEntity skinnable = (SkinnableEntity) npc.getEntity();
			if (skinnable != null) {
				skinnable.setSkinName(name);
			}
		}
	}
}
