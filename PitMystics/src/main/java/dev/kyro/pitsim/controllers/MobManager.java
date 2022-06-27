package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.brewing.BrewingManager;
import dev.kyro.pitsim.brewing.PotionManager;
import dev.kyro.pitsim.controllers.objects.GoldenHelmet;
import dev.kyro.pitsim.controllers.objects.PitBoss;
import dev.kyro.pitsim.controllers.objects.PitMob;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enchants.tainted.CleaveSpell;
import dev.kyro.pitsim.enums.SubLevel;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.mobs.PitEnderman;
import dev.kyro.pitsim.mobs.PitIronGolem;
import dev.kyro.pitsim.mobs.PitMagmaCube;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class MobManager implements Listener {
	public static List<PitMob> mobs = new ArrayList<>();
	public static Map<UUID, ArmorStand> nameTags = new HashMap<>();
	public static Map<ArmorStand, Location> locs = new HashMap<>();
	public static Map<ArmorStand, Location> oldLocs = new HashMap<>();


	static {
		new BukkitRunnable() {

			@Override
			public void run() {
				clearMobs();
				for(SubLevel level : SubLevel.values()) {


					int currentMobs = 0;
					for(PitMob mob : mobs) {
						if(mob.subLevel == level.level) currentMobs++;
					}

					if(currentMobs >= level.maxMobs) continue;


					Random xRand = new Random();
					int xLoc = xRand.nextInt(level.radius - (-1 * level.radius) + 1) + (-1 * level.radius);

					Random zRand = new Random();
					int zLoc = zRand.nextInt(level.radius - (-1 * level.radius) + 1) + (-1 * level.radius);

					Random rand = new Random();
					Class randClass = level.mobs.get(rand.nextInt(level.mobs.size()));
					try {

						Class[] cArg = new Class[1];
						cArg[0] = Location.class;

						Location loc = new Location(Bukkit.getWorld("darkzone"), xLoc + level.middle.getX() + 0.5, level.middle.getY(), zLoc + level.middle.getZ() + 0.5);
						while(loc.getBlock().getType() != Material.AIR) {
							loc.setY(loc.getY() + 1);
							if(loc.getY() >= level.middle.getY() + 10) continue;
						}

						randClass.getDeclaredConstructor(cArg).newInstance(loc);

//						randMob = (PitMob) randClass.newInstance();
					} catch(InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ignored) {
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 10, 10);

		new BukkitRunnable() {
			@Override
			public void run() {
				List<PitMob> toRemove = new ArrayList<>();
				for (PitMob mob : mobs) {

					assert SubLevel.getLevel(mob.subLevel) != null;
					if(mob.entity.getLocation().distance(SubLevel.getLevel(mob.subLevel).middle) <= SubLevel.getLevel(mob.subLevel).radius) {
						if(!(mob.entity instanceof Monster)) continue;
						if(((Monster) mob.entity).getTarget() != null) continue;
						if(mob.entity.getNearbyEntities(1, 1, 1).size() <= 1) continue;
					}
					nameTags.get(mob.entity.getUniqueId()).remove();
					mob.entity.remove();
					toRemove.add(mob);
				}

				for (PitMob pitMob : toRemove) {
					mobs.remove(pitMob);
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 20 * 20, 20 * 20);

		new BukkitRunnable() {
			@Override
			public void run() {
				List<PitMob> toRemove = new ArrayList<>();
				for(PitMob mob : mobs) {
					if(mob.entity.isDead()) {
						nameTags.get(mob.entity.getUniqueId()).remove();
						toRemove.add(mob);
					}
				}
				for(PitMob pitMob : toRemove) {
					mobs.remove(pitMob);
				}
			}

		}.runTaskTimer(PitSim.INSTANCE, 20 * 5, 20 * 5);

		new BukkitRunnable() {
			@Override
			public void run() {
				clearMobs();
			}
		}.runTaskLater(PitSim.INSTANCE, 10);

		new BukkitRunnable() {
			@Override
			public void run() {
				for(PitMob mob : mobs) {
					if(!(mob instanceof PitIronGolem) && (!(mob instanceof PitEnderman))) continue;
					for (Entity nearbyEntity : mob.entity.getNearbyEntities(5, 5, 5)) {
						if(nearbyEntity instanceof Player && !PitBoss.isPitBoss((Player) nearbyEntity)) {
							((Creature) mob.entity).setTarget((LivingEntity) nearbyEntity);
						}
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 20, 20);
		}

	public static void makeTag(LivingEntity mob, String name) {
		Location op = mob.getLocation();
		ArmorStand stand = (ArmorStand) op.getWorld().spawnEntity(op, EntityType.ARMOR_STAND);
		stand.setGravity(false);
		stand.setVisible(true);
		stand.setCustomNameVisible(true);
		stand.setRemoveWhenFarAway(false);
		stand.setVisible(false);
		stand.setSmall(true);
		mob.setPassenger(stand);
		stand.setCustomName(ChatColor.translateAlternateColorCodes('&', name));

		nameTags.put(mob.getUniqueId(), stand);

//		nameTags.put(mob, stand);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onKill(KillEvent event) {
		if(event.deadIsPlayer) return;
		clearMobs();
		List<PitMob> toRemove = new ArrayList<>();
		for(PitMob mob : mobs) {
			if(mob.entity.getUniqueId().equals(event.dead.getUniqueId())) {
				for (Entity entity : Bukkit.getWorld("darkzone").getEntities()) {
					if(entity.getUniqueId().equals(nameTags.get(mob.entity.getUniqueId()).getUniqueId())) {
						entity.remove();
					}
				}
				toRemove.add(mob);

				Map<ItemStack, Integer> drops = mob.getDrops();

				ItemStack helmet = GoldenHelmet.getHelmet(event.killerPlayer);

				int level = 0;
				double chance = 0;
				if(helmet != null) level = HelmetSystem.getLevel(GoldenHelmet.getUsedHelmetGold(event.deadPlayer));
				if(helmet != null) chance = 7.5 * HelmetSystem.getTotalStacks(HelmetSystem.Passive.DAMAGE_REDUCTION, level - 1);

				for (Map.Entry<ItemStack, Integer> entry : drops.entrySet()) {
					Random r = new Random();
					int low = 1;
					int high = 100;
					int result = r.nextInt(high-low) + low;

					result += result * (chance * 0.01);

					if(result > entry.getValue()) continue;
					event.dead.getWorld().dropItemNaturally(event.dead.getLocation(), entry.getKey());
				}
			}
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				for(PitMob pitMob : toRemove) {
					mobs.remove(pitMob);
				}
			}
		}.runTaskLater(PitSim.INSTANCE, 1);
	}
//
//	@EventHandler
//	public void onSpawn(EntitySpawnEvent event) {
//		if(event.getLocation().getWorld() == Bukkit.getWorld("darkzone") && event.getEntity() instanceof Enderman &&
//				!PitMob.isPitMob((LivingEntity) event.getEntity())) event.setCancelled(true);
//	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHit(AttackEvent.Pre event) {
		for (NPC value : BossManager.clickables.values()) {
			if(event.defender.getUniqueId().equals(value.getUniqueId())) event.setCancelled(true);
		}

		if(!(event.defender instanceof ArmorStand)) return;

		for(ArmorStand value : nameTags.values()) {
			if(event.defender.getUniqueId().equals(value.getUniqueId())) event.setCancelled(true);
		}
	}

	@EventHandler
	public void onMobAttack(EntityDamageByEntityEvent event) {
		if(event.getDamager() instanceof Player) return;
		if(!(event.getDamager() instanceof LivingEntity)) return;
		PitMob mob = PitMob.getPitMob((LivingEntity) event.getDamager());
		if(mob == null) return;

		if(mob instanceof PitMagmaCube) return;

		if(event.getDamage() > 0) event.setDamage(EntityDamageEvent.DamageModifier.BASE, mob.damage);

	}


	@EventHandler
	public void onMagmaCubeAttack(AttackEvent.Apply event) {
		if(event.attacker instanceof Player) return;
		PitMob mob = PitMob.getPitMob(event.attacker);
		if(mob == null) return;

		if(!(mob instanceof PitMagmaCube)) return;

		if(event.fakeHit) return;

		event.increase = mob.damage;

	}

	@EventHandler
	public void onBlockPickup(EntityChangeBlockEvent event) {
		if(event.getEntity() instanceof Enderman) event.setCancelled(true);
	}

	@EventHandler
	public void onTeleport(EntityTeleportEvent event) {
		if(event.getEntity() instanceof Enderman) event.setCancelled(true);
	}

	@EventHandler
	public void onTarget(EntityTargetLivingEntityEvent event) {
		try {
			if(event.getTarget().getLocation().distance(event.getEntity().getLocation()) > 5) event.setCancelled(true);
		} catch (Exception ignored) { }
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHit(EntityDamageByEntityEvent event) {
		if(event.getDamager() instanceof Arrow) return;
		if(event.getDamager() instanceof Fireball) return;
		if(NonManager.getNon((LivingEntity) event.getDamager()) != null) return;

		for (NPC value : BossManager.clickables.values()) {
			if(event.getEntity().getUniqueId().equals(value.getUniqueId())) event.setCancelled(true);
		}

		if(!(event.getEntity() instanceof ArmorStand)) return;


		for(ArmorStand value : nameTags.values()) {
			if(event.getEntity().getUniqueId().equals(value.getUniqueId())) event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEquip(PlayerArmorStandManipulateEvent event) {
		if(event.getRightClicked() == null) return;
		event.setCancelled(true);

		for(ArmorStand value : nameTags.values()) {
			if(event.getRightClicked().getUniqueId().equals(value.getUniqueId())) event.setCancelled(true);
		}
	}

	@EventHandler
	public void onSpawn(SpawnerSpawnEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onExplode(ExplosionPrimeEvent event) {
		Entity entity = event.getEntity();
		if (!(entity instanceof Creeper)) return;

		PitMob mob = PitMob.getPitMob((LivingEntity) entity);
		if(mob == null) return;
		mobs.remove(mob);
		nameTags.get(mob.entity.getUniqueId()).remove();
		nameTags.remove(mob.entity.getUniqueId());
		event.setRadius(0);

		for (Entity player : entity.getNearbyEntities(5, 5, 5)) {
			if(!(player instanceof Player)) continue;

			PitPlayer.getPitPlayer((Player) player).damage(5, (LivingEntity) entity);
		}
	}

	public static void clearMobs() {
		main:
		for (Entity entity : Bukkit.getWorld("darkzone").getEntities()) {

			if(entity instanceof Player) continue;
			if(CitizensAPI.getNPCRegistry().isNPC(entity)) continue;


			for (PitMob mob : mobs) {
				if(mob.entity.getUniqueId().equals(entity.getUniqueId())) continue main;
				if(nameTags.get(mob.entity.getUniqueId()).getUniqueId().equals(entity.getUniqueId())) continue main;
			}

			if(entity instanceof ArmorStand && entity.getLocation().distance(AuctionManager.spawnLoc) < 50 && entity.getCustomName() == null) continue;

			if(entity.getUniqueId().equals(TaintedWell.wellStand.getUniqueId())) continue;
//			if(entity.getUniqueId().equals(TaintedWell.removeStand.getUniqueId())) continue;
			for (ArmorStand value : TaintedWell.enchantStands.values()) {
				if(value.getUniqueId().equals(entity.getUniqueId())) continue main;
			}
			for (ArmorStand value : TaintedWell.removeStands.values()) {
				if(value.getUniqueId().equals(entity.getUniqueId())) continue main;
			}
			for (ArmorStand value : BrewingManager.brewingStands) {
				if(value.getUniqueId().equals(entity.getUniqueId())) continue main;
			}
			for (ArmorStand value : CleaveSpell.stands.values()) {
				if(value.getUniqueId().equals(entity.getUniqueId())) continue main;
			}
			for (UUID pedestalArmorStand : AuctionDisplays.pedestalArmorStands) {
				if(pedestalArmorStand.equals(entity.getUniqueId())) continue main;
			}
			for (UUID pedestalArmorStand : AuctionDisplays.highestBidderStands) {
				if(pedestalArmorStand.equals(entity.getUniqueId())) continue main;
			}
			for (UUID pedestalArmorStand : AuctionDisplays.highestBidStands) {
				if(pedestalArmorStand.equals(entity.getUniqueId())) continue main;
			}
			for (UUID pedestalArmorStand : AuctionDisplays.rightClickStands) {
				if(pedestalArmorStand.equals(entity.getUniqueId())) continue main;
			}
			for (Entity potion : PotionManager.potions) {
				if(potion.getUniqueId().equals(entity.getUniqueId())) continue main;
			}
			if(entity.getUniqueId().equals(AuctionDisplays.timerStandUUID)) continue;

			if(entity.getUniqueId().equals(TaintedWell.textLine1.getUniqueId())) continue;
			if(entity.getUniqueId().equals(TaintedWell.textLine2.getUniqueId())) continue;
			if(entity.getUniqueId().equals(TaintedWell.textLine3.getUniqueId())) continue;
			if(entity.getUniqueId().equals(TaintedWell.textLine4.getUniqueId())) continue;
			if(entity instanceof Item) continue;
			if(entity instanceof Arrow) continue;
			if(entity instanceof Wither) continue;
			if(entity instanceof Villager) continue;
			if(entity instanceof Fireball) continue;
			if(entity instanceof Slime && !(entity instanceof MagmaCube)) continue;

			entity.remove();
		}
	}




}
