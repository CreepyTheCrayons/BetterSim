package dev.kyro.pitremake.misc;

import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.pitremake.PitRemake;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Misc {

	public static double getDistance(Location loc1, Location loc2) {

		double x1 = loc1.getX();
		double y1 = loc1.getY();
		double z1 = loc1.getZ();

		double x2 = loc2.getX();
		double y2 = loc2.getY();
		double z2 = loc2.getZ();

		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) + Math.pow(z1 - z2, 2));
	}

	public static String ordinalWords(int num) {

		switch(num) {
			case 1:
				return "";
			case 2:
				return " second";
			case 3:
				return " third";
			case 4:
				return " fourth";
			case 5:
				return " fifth";
		}
		return "";
	}

	public static void applyPotionEffect(Player player, PotionEffectType type, int duration, int amplifier) {

		for(PotionEffect potionEffect : player.getActivePotionEffects()) {
			if(!potionEffect.getType().equals(type) || potionEffect.getAmplifier() > amplifier) continue;
			if(potionEffect.getAmplifier() == amplifier && potionEffect.getDuration() >= duration) continue;
			player.removePotionEffect(type);
			break;
		}
		player.addPotionEffect(new PotionEffect(type, duration, amplifier, true));
	}

	public static String getHearts(double damage) {

		String string = (damage / 2) % 1 == 0 ? String.valueOf((int) (damage / 2)) : String.valueOf((Math.floor(damage * 50)) / 100);
		return string + "\u2764";
	}

	public static void multiKill(Player player) {

		new BukkitRunnable() {
			int count = 1;
			@Override
			public void run() {

				switch(count) {
					case 0:
//						ASound.play(player, Sound.ORB_PICKUP, 1F, 1.7301587F);
						break;
					case 1:
						ASound.play(player, Sound.ORB_PICKUP, 1F, 1.7936507F);
						break;
					case 2:
						ASound.play(player, Sound.ORB_PICKUP, 1F, 1.8253968F);
						break;
					case 3:
						ASound.play(player, Sound.ORB_PICKUP, 1F, 1.8730159F);
						break;
					case 4:
						ASound.play(player, Sound.ORB_PICKUP, 1F, 1.9047619F);
						break;
					case 5:
						ASound.play(player, Sound.ORB_PICKUP, 1F, 1.9523809F);
						break;
				}

				if(++count > 5) cancel();
			}
		}.runTaskTimer(PitRemake.INSTANCE, 0L, 2L);
	}

	public static void sendActionBar(Player p, String message) {
		PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" +
				ChatColor.translateAlternateColorCodes('&', message) + "\"}"), (byte) 2);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
	}

	public static void broadcastMessage(String message) {
		PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" +
				ChatColor.translateAlternateColorCodes('&', message) + "\"}"), (byte) 2);
		for (Player p : Bukkit.getServer().getOnlinePlayers()) {
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
		}
	}
}
