package dev.kyro.pitsim.killstreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.Killstreak;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class AuraOfProtection extends Killstreak {
	public static AuraOfProtection INSTANCE;
	public static List<LivingEntity> rewardPlayers = new ArrayList<>();

	public AuraOfProtection() {
		super("Aura of Protection", "AuraOfProtection", 15, 6);
		INSTANCE = this;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHit(AttackEvent.Apply event) {
		if(rewardPlayers.contains(event.getDefender())) event.trueDamage /= 2.0;
	}

	@Override
	public void proc(Player player) {
		if(!rewardPlayers.contains(player)) rewardPlayers.add(player);
		Sounds.SoundMoment soundMoment = new Sounds.SoundMoment(3);
		soundMoment.add(Sound.ZOMBIE_UNFECT, 2, 0.79);
		soundMoment.add(Sound.ZOMBIE_UNFECT, 2, 0.84);
		soundMoment.add(Sound.ZOMBIE_UNFECT, 2, 0.88);
		soundMoment.add(Sound.ZOMBIE_UNFECT, 2, 0.93);
		soundMoment.play(player);

		new BukkitRunnable() {
			@Override
			public void run() {
				rewardPlayers.remove(player);
			}
		}.runTaskLater(PitSim.INSTANCE, 15 * 20L);
	}

	@Override
	public void reset(Player player) {
		rewardPlayers.remove(player);
	}

	@Override
	public ItemStack getDisplayItem(Player player) {
		AItemStackBuilder builder = new AItemStackBuilder(Material.SLIME_BALL)
				.setName("&e" + displayName)
				.setLore(new ALoreBuilder(
						"&7Every: &c" + killInterval + " kills",
						"",
						"&7Take &950% &7less true damage for",
						"&715 seconds."
				));

		return builder.getItemStack();
	}

	@Override
	public String getSummary() {
		return "&eAura of Protection&7 is a killstreak that makes you take less &9true damage&7 for a short " +
				"period of time excluded every &c15 kills";
	}
}
