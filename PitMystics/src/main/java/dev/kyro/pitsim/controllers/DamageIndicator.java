package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.controllers.objects.Non;
import dev.kyro.pitsim.controllers.objects.PitMob;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import me.clip.placeholderapi.PlaceholderAPI;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;


public class DamageIndicator implements Listener {

	//    @EventHandler(priority = EventPriority.MONITOR)
	public static void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isAttackerPlayer()) return;
		if(attackEvent.isFakeHit()) return;

		Player attacker = attackEvent.getAttackerPlayer();
		LivingEntity defender = attackEvent.getDefender();

//        double maxHealth = defender.getMaxHealth() / 2;
//        double currentHealth = defender.getHealth() / 2;
//        double damageTaken = attackEvent.event.getFinalDamage() / 2;
//
//
//        Bukkit.broadcastMessage(String.valueOf("Max Health: " + maxHealth));
//        Bukkit.broadcastMessage(String.valueOf("Current Health: " + currentHealth));
//        Bukkit.broadcastMessage(String.valueOf("Damage Taken: " + damageTaken));
//
//        StringBuilder output = new StringBuilder();
//
//
//
//        for (int i = 0; i < Math.floor(currentHealth - damageTaken); i++) {
//            output.append(ChatColor.DARK_RED).append("\u2764");
//        }
//
//        for (int i = 0; i < Math.ceil(damageTaken); i++) {
//            output.append(ChatColor.RED).append("\u2764");
//        }
//
//        for (int i = 0; i < maxHealth - (Math.floor(currentHealth - damageTaken) + Math.ceil(damageTaken)); i++) {
//            output.append(ChatColor.BLACK).append("\u2764");
//        }
//
//        Misc.sendActionBar(attacker, output.toString());

		EntityPlayer entityPlayer = null;
		if(defender instanceof Player) entityPlayer = ((CraftPlayer) defender).getHandle();
		LivingEntity player = defender;

		int roundedDamageTaken = ((int) attackEvent.getEvent().getFinalDamage()) / getNum(player);

		int originalHealth = ((int) defender.getHealth()) / getNum(player);
		int maxHealth = ((int) defender.getMaxHealth()) / getNum(player);

		int result = Math.max(originalHealth - roundedDamageTaken, 0);

		if((defender.getHealth() - attackEvent.getEvent().getFinalDamage()) % 2 < 1 && attackEvent.getEvent().getFinalDamage() > 1)
			roundedDamageTaken++;

		if(result == 0) {
			roundedDamageTaken = 0;

			for(int i = 0; i < originalHealth; i++) {
				roundedDamageTaken++;
			}
		}

		Non defendingNon = NonManager.getNon(defender);
		StringBuilder output = new StringBuilder();

		String playername = "&7%luckperms_prefix%" + (defendingNon == null ? "%player_name%" : defendingNon.displayName) + " ";
		if(defender instanceof Player)output.append(PlaceholderAPI.setPlaceholders(attackEvent.getDefenderPlayer(), playername));
		else if(PitMob.isPitMob(defender)) output.append(PitMob.getPitMob(defender).displayName).append(" ");
		else output.append(player.getCustomName() + " ");

		for(int i = 0; i < Math.max(originalHealth - roundedDamageTaken, 0); i++) {
			output.append(ChatColor.DARK_RED).append("\u2764");
		}

		if(defender instanceof Player) {
			for(int i = 0; i < roundedDamageTaken - (int) entityPlayer.getAbsorptionHearts() / getNum(player); i++) {
				output.append(ChatColor.RED).append("\u2764");
			}
		} else {
			for(int i = 0; i < roundedDamageTaken; i++) {
				output.append(ChatColor.RED).append("\u2764");
			}
		}


		for(int i = originalHealth; i < maxHealth; i++) {
			output.append(ChatColor.BLACK).append("\u2764");
		}

		if(defender instanceof Player) {
			for(int i = 0; i < (int) entityPlayer.getAbsorptionHearts() / getNum(player); i++) {
				output.append(ChatColor.YELLOW).append("\u2764");
			}
		}

		Misc.sendActionBar(attacker, output.toString());
	}

	public static int getNum(LivingEntity entity) {
		return Math.max(1, (int) (2 * (entity.getMaxHealth() / 20)));
	}
}
