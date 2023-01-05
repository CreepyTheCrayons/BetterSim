package dev.kyro.pitsim.adarkzone;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.bosses.PitZombieBoss;
import dev.kyro.pitsim.adarkzone.mobs.PitZombie;
import dev.kyro.pitsim.adarkzone.notdarkzone.PitEquipment;
import dev.kyro.pitsim.brewing.ingredients.RottenFlesh;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.misc.Sounds;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DarkzoneManager implements Listener {
	public static List<SubLevel> subLevels = new ArrayList<>();
	public static List<Hologram> holograms = new ArrayList<>();

	static {

		SubLevel zombieSublevel = new SubLevel(
				SubLevelType.ZOMBIE, PitZombieBoss.class, PitZombie.class,
				new Location(MapManager.getDarkzone(), 327, 67, -143),
				20, 17, 12, "%pitsim_zombie_cave%");
		ItemStack zombieSpawnItem = RottenFlesh.INSTANCE.getItem();
		zombieSublevel.setSpawnItem(zombieSpawnItem);
		zombieSublevel.addMobDrop(RottenFlesh.INSTANCE.getItem(), 1);

		registerSubLevel(zombieSublevel);
		registerHolograms();

		new BukkitRunnable() {
			@Override
			public void run() {
				for(SubLevel subLevel : subLevels) subLevel.tick();
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 5);
	}


	/**
	 * Called when a player interacts with a block, checks if all the spawn conditions are met for a boss to
	 * spawn, and if so, spawns it.
	 * @param event
	 */
	@EventHandler
	public void onClick(PlayerInteractEvent event) {


		if(event.getPlayer() == null) return;
		if(event.getPlayer().getItemInHand() == null) return;
		if(event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;



		ItemStack item = event.getItem();
		Location location = event.getClickedBlock().getLocation();

		for(SubLevel subLevel : subLevels) {
			if(subLevel.isBossSpawned()) continue;
			if (subLevel.getSpawnItem() == null) {
				continue;
			}
			System.out.println("3");
			if (subLevel.getSpawnItem().isSimilar(item)) {
				System.out.println("4");
				if(subLevel.getMiddle().equals(location)) {
					System.out.println("5");

					subLevel.setCurrentDrops(subLevel.getCurrentDrops() + 1);
					item.setAmount(item.getAmount() - 1);
					if(item.getAmount() == 1) {
						event.getPlayer().setItemInHand(null);
					}

					System.out.println("Current drops: " + subLevel.getCurrentDrops());

					if(subLevel.getCurrentDrops() >= subLevel.getRequiredDropsToSpawn()) {
						subLevel.getMiddle().getWorld().playEffect(subLevel.getMiddle(), Effect.EXPLOSION_HUGE, 100);
						Sounds.PRESTIGE.play(subLevel.getMiddle());
						subLevel.disableMobs();
						subLevel.spawnBoss(event.getPlayer());

						subLevel.setCurrentDrops(0);
						//decrese the item stack in the players hand by 1

					}
				}
			}
		}
	}


	/**
	 * Cancels all suffocation and fall damage in the darkzone
	 * @param event
	 **/
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if(event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION || event.getCause() == EntityDamageEvent.DamageCause.FALL)
			event.setCancelled(true);
	}

	/**
	 * Called when an entity is killed, checks if boss was killed, if so resets the sublevel to normal state and
	 * distrubutes rewards
	 * @param killEvent
	 */
	@EventHandler
	public static void onEntityDeath(KillEvent killEvent) {

		LivingEntity entity = killEvent.getDead();

		PitBoss killedBoss = BossManager.getPitBoss(entity);
		if(killedBoss != null) {
			killedBoss.kill();
			return;
		}

		Player killer = killEvent.getKillerPlayer();
		if (killer == null) {
			return;
		}

		for(SubLevel subLevel : subLevels) {
			if(subLevel.isBossSpawned()) continue;
			if(subLevel.isPitMob(entity)) {
				AUtil.giveItemSafely(killer, subLevel.getMobDropPool().getRandomDrop());
			}
		}

	}



	public static PitEquipment getDefaultEquipment() {
		return new PitEquipment()
				.held(new ItemStack(Material.DIAMOND_SWORD))
				.helmet(new ItemStack(Material.DIAMOND_SWORD))
				.chestplate(new ItemStack(Material.DIAMOND_SWORD))
				.leggings(new ItemStack(Material.DIAMOND_SWORD))
				.leggings(new ItemStack(Material.DIAMOND_SWORD))
				.boots(new ItemStack(Material.DIAMOND_SWORD));
	}


	/**
	 * Adds a sublevel to the list of sublevels
	 * @param subLevel
	 */
	public static void registerSubLevel(SubLevel subLevel) {
		subLevels.add(subLevel);
	}


	/**
	 * Gets a sublevel by its type
	 * @param type
	 * @return SubLevel
	 */
	public static SubLevel getSublevel(SubLevelType type) {
		for(SubLevel subLevel : subLevels) {
			if(subLevel.getSubLevelType() == type) {
				return subLevel;
			}
		}
		return null;
	}


	/**
	 * Registers all the holograms for the darkzone
	 */
	public static void registerHolograms() {
		for(Hologram hologram : HologramsAPI.getHolograms(PitSim.INSTANCE)) {
			hologram.delete();
		}

		for(SubLevel subLevel : subLevels) {

			Hologram hologram = HologramsAPI.createHologram(PitSim.INSTANCE, new Location(
					subLevel.getMiddle().getWorld(),
					subLevel.getMiddle().getX() + 0.5,
					subLevel.getMiddle().getY() + 1.6,
					subLevel.getMiddle().getZ() + 0.5));
			hologram.setAllowPlaceholders(true);
			hologram.appendTextLine(ChatColor.RED + "Place " + ChatColor.translateAlternateColorCodes('&',
					"&a" + subLevel.getSpawnItem().getItemMeta().getDisplayName()));
			hologram.appendTextLine("{fast}" + subLevel.getPlaceholder() + " ");
			holograms.add(hologram);
		}
	}
}
