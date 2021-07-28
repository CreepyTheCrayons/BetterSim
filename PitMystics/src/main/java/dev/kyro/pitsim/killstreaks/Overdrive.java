package dev.kyro.pitsim.killstreaks;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.controllers.objects.Megastreak;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Overdrive extends Megastreak {

	public BukkitTask runnable;

	@Override
	public String getName() {
		return "&c&lOVRDRV";
	}

	@Override
	public String getRawName() {
		return "Overdrive";
	}

	@Override
	public String getPrefix() {
		return "&c&lOVRDRV";
	}

	@Override
	public List<String> getRefNames() {
		return Arrays.asList("overdrive");
	}

	@Override
	public int getRequiredKills() {
		return 20;
	}

	@Override
	public int guiSlot() {
		return 11;
	}

	@Override
	public int levelReq() {
		return 0;
	}

	@Override
	public ItemStack guiItem() {
		ItemStack item = new ItemStack(Material.BLAZE_POWDER);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7Triggers on: &c50 kills"));
		lore.add("");
		lore.add(ChatColor.GRAY + "On trigger:");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&a\u25a0 &7Perma &eSpeed I&7."));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&a\u25a0 &7Immune to &9Slowness&7."));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&a\u25a0 &eSpeed &7effects cannot be removed."));
		lore.add("");
		lore.add(ChatColor.GRAY + "BUT:");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&c\u25a0 &7Passively receive &c+10% &7damage."));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&c\u25a0 &7Receive &c+1% &7damage per kill over 50."));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7(Damage tripled for bots)"));
		lore.add("");
		lore.add(ChatColor.GRAY + "On death:");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&e\u25a0 &7Earn between &61000 &7and &65000 gold&7."));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public Overdrive(PitPlayer pitPlayer) {
		super(pitPlayer);
	}

	@EventHandler
	public void onHit(AttackEvent.Apply attackEvent) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(attackEvent.defender);
		if(pitPlayer != this.pitPlayer) return;
		if(pitPlayer.megastreak.getClass() == Overdrive.class) {
			attackEvent.increasePercent += 10 / 100D;
		}
		if(pitPlayer.megastreak.isOnMega() && pitPlayer.megastreak.getClass() == Overdrive.class) {
			int ks = pitPlayer.getKills();
//            attackEvent.increasePercent += ((ks / 5)  / 100D) * 8;
			if(NonManager.getNon(attackEvent.attacker) == null) {
				attackEvent.increasePercent += (ks - 50) / 100D;
			} else {
				attackEvent.increasePercent += ((ks - 50) * 3) / 100D;
			}
//            Bukkit.broadcastMessage(attackEvent.getFinalDamage() + "");
		}
	}

	@Override
	public void proc() {

		pitPlayer.player.getWorld().playSound(pitPlayer.player.getLocation(), Sound.WITHER_SPAWN, 1000, 1);
		runnable = new BukkitRunnable() {
			@Override
			public void run() {
				Misc.applyPotionEffect(pitPlayer.player, PotionEffectType.SPEED, 200, 0, true, false);
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 60L);
	}

	@Override
	public void reset() {

		int randomNum = ThreadLocalRandom.current().nextInt(1000, 5000 + 1);
		AOutput.send(pitPlayer.player, "&c&lOVERDRIVE! &7Earned &6+" + randomNum + "&6g &7from megastreak!");
		PitSim.VAULT.depositPlayer(pitPlayer.player, randomNum);

		if(runnable != null) runnable.cancel();
	}

	@Override
	public void kill() {

		if(!isOnMega()) return;
	}
}
