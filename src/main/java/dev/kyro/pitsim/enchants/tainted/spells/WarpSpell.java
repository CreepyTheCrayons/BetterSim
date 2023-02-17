package dev.kyro.pitsim.enchants.tainted.spells;

import dev.kyro.pitsim.controllers.Cooldown;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.PitPlayerAttemptAbilityEvent;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.PitLoreBuilder;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Set;

public class WarpSpell extends PitEnchant {

	public WarpSpell() {
		super("Warp", true, ApplyType.SCYTHES, "warp", "teleport", "tp");
		isTainted = true;
		isRare = true;
	}

	@EventHandler
	public void onUse(PitPlayerAttemptAbilityEvent event) {
		int enchantLvl = event.getEnchantLevel(this);
		if(enchantLvl == 0) return;

		Player player = event.getPlayer();
		Block lookBlock = player.getTargetBlock((Set<Material>) null, 15); ;

		Location teleportLoc = lookBlock.getLocation();

		for(Entity entity : player.getNearbyEntities(15, 15, 15)) {
			Vector direction = player.getLocation().getDirection();
			Vector towardsEntity = entity.getLocation().subtract(player.getLocation()).toVector().normalize();

			if(direction.distance(towardsEntity) < 0.3) {
				teleportLoc = entity.getLocation();
			}
		}

		if(teleportLoc.getBlock().getType() != Material.AIR && teleportLoc.clone().add(0, 1, 0).getBlock().getType() == Material.AIR)
			teleportLoc.add(0, 1, 0);
		if(teleportLoc.clone().add(0, 1, 0).getBlock().getType() != Material.AIR) {
			Sounds.ERROR.play(player);
			return;
		}

		Cooldown cooldown = getCooldown(event.getPlayer(), 10);
		if(cooldown.isOnCooldown()) return;

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(event.getPlayer());
		if(!pitPlayer.useMana(getManaCost(enchantLvl))) {
			Sounds.NO.play(event.getPlayer());
			return;
		}

		cooldown.restart();

		float pitch = player.getLocation().getPitch();
		float yaw = player.getLocation().getYaw();
		teleportLoc.setPitch(pitch);
		teleportLoc.setYaw(yaw);

		player.teleport(teleportLoc.add(0.5, 0, 0.5));
		player.getWorld().playEffect(player.getLocation(), Effect.ENDER_SIGNAL, 1);
		Misc.applyPotionEffect(player, PotionEffectType.SPEED, 40, 3, false, false);
		Sounds.WARP.play(player);

	}

	@Override
	public List<String> getNormalDescription(int enchantLvl) {
		return new PitLoreBuilder(
				"&7Teleports you forward Grants &eSpeed IV &7(2s) &d&o-" + getManaCost(enchantLvl) + " Mana"
		).getLore();
	}

	public static int getManaCost(int enchantLvl) {
		return 30 * (4 - enchantLvl);
	}
}
