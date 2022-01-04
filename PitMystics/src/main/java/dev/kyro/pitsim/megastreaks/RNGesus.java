package dev.kyro.pitsim.megastreaks;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.*;
import dev.kyro.pitsim.controllers.objects.Megastreak;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.HealEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.inventories.MegastreakPanel;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.misc.particles.HomeParticle;
import me.clip.placeholderapi.PlaceholderAPI;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.DecimalFormat;
import java.util.*;

public class RNGesus extends Megastreak {
	public static int INSTABILITY_THRESHOLD = 1000;

	public List<Reality> generatedRealityOrder = new ArrayList<>();
	public Map<Reality, RealityInfo> realityMap = new HashMap<>();
	public Reality reality = Reality.NONE;
	public BukkitTask runnable;

	public int getXP(double progression) {
		return (int) progression;
	}

	public double getGold(double progression) {
		return progression;
	}

	public double getDamage(double progression) {
		return progression * 0.1;
	}

	public float getAbsorption(double progression) {
		return (float) progression;
	}

	@Override
	public String getName() {
		return reality.prefix;
	}

	@Override
	public String getRawName() {
		return "RNGesus";
	}

	@Override
	public String getPrefix() {
		return "&eRNGesus";
	}

	@Override
	public List<String> getRefNames() {
		return Arrays.asList("rngesus");
	}

	@Override
	public int getRequiredKills() {
		return 100;
	}

	@Override
	public int guiSlot() {
		return 16;
	}

	@Override
	public int prestigeReq() {
		return 50;
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
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7Triggers on: &c100 kills"));
		lore.add("");
		lore.add(ChatColor.GRAY + "On trigger:");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&a\u25a0 &7Earn &b+50% XP &7from kills"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&a\u25a0 &7Earn &6+100% gold &7from kills"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&a\u25a0 &7Permanent &eSpeed I&7"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&a\u25a0 &7Immune to &9Slowness&7"));
		lore.add("");
		lore.add(ChatColor.GRAY + "BUT:");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&c\u25a0 &7Receive &c+" + Misc.getHearts(0.2) + " &7very true"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7damage per 10 kills (only from nons)"));
		lore.add("");
		lore.add(ChatColor.GRAY + "On death:");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&e\u25a0 &7Earn between &61000 &7and &65000 gold&7"));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public RNGesus(PitPlayer pitPlayer) {
		super(pitPlayer);
		for(Reality value : Reality.values()) realityMap.put(value, new RealityInfo(value));
		generateRealityOrder();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onHit(AttackEvent.Apply attackEvent) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(attackEvent.attacker);
		if(pitPlayer != this.pitPlayer || pitPlayer.megastreak.getClass() != RNGesus.class) return;
		if(NonManager.getNon(attackEvent.attacker) != null) return;

		if(pitPlayer.getKills() + 1 >= INSTABILITY_THRESHOLD) {
			attackEvent.multiplier.clear();
			attackEvent.increaseCalcDecrease.clear();
			attackEvent.increase = 0;
			attackEvent.increasePercent = 0;
			attackEvent.decreasePercent = 0;
			double damage = getDamage(realityMap.get(Reality.DAMAGE).getLevel());
			attackEvent.increase += damage;

			List<Entity> entities = attackEvent.defender.getNearbyEntities(20, 20, 20);
			Collections.shuffle(entities);
			int count = 0;
			for(Entity entity : entities) {
				if(count++ >= 5) break;
				if(!(entity instanceof Player)) continue;
				Player target = (Player) entity;
				if(NonManager.getNon(target) == null) continue;

				BukkitRunnable callback = new BukkitRunnable() {
					@Override
					public void run() {
						Map<PitEnchant, Integer> attackerEnchant = EnchantManager.getEnchantsOnPlayer(attackEvent.attacker);
						Map<PitEnchant, Integer> defenderEnchant = new HashMap<>();
						EntityDamageByEntityEvent ev = new EntityDamageByEntityEvent(attackEvent.attacker, target, EntityDamageEvent.DamageCause.CUSTOM, 0);
						AttackEvent attackEvent = new AttackEvent(ev, attackerEnchant, defenderEnchant, false);

						if(target.getHealth() <= damage) {
							DamageManager.fakeKill(attackEvent, attackEvent.attacker, target, false);
						} {
							target.setHealth(target.getHealth() - damage);
						}
					}
				};

				new HomeParticle(attackEvent.attacker, attackEvent.defender.getLocation().add(0, 1, 0), target, 0.4, callback);
			}
		}
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void kill(KillEvent killEvent) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(killEvent.killer);
		if(pitPlayer != this.pitPlayer) return;
		if(pitPlayer.getKills() >= INSTABILITY_THRESHOLD && pitPlayer.megastreak.getClass() == RNGesus.class) {
			killEvent.xpMultipliers.clear();
			killEvent.xpReward = 0;
			killEvent.xpCap = 0;
			killEvent.xpReward += getXP(realityMap.get(Reality.XP).getLevel() * 2);
			killEvent.xpCap += getXP(realityMap.get(Reality.XP).getLevel());

			killEvent.goldMultipliers.clear();
			killEvent.goldReward = 0;
			killEvent.goldReward += getGold(realityMap.get(Reality.GOLD).getLevel());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void kill2(KillEvent killEvent) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(killEvent.killer);
		if(pitPlayer != this.pitPlayer) return;
		if(pitPlayer.megastreak.getClass() == RNGesus.class) {
			if((pitPlayer.getKills() + 1) % 100 == 0 && pitPlayer.getKills() < INSTABILITY_THRESHOLD) {
				shiftReality();
			}
			if(pitPlayer.getKills() + 1 == INSTABILITY_THRESHOLD) destabilize();

			if(reality == Reality.XP) {
				realityMap.get(reality).progression += killEvent.getFinalXp();
			} else if(reality == Reality.GOLD) {
				realityMap.get(reality).progression += killEvent.getFinalGold();
			}
			setXPBar();
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void attack(AttackEvent.Apply attackEvent) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(attackEvent.attacker);
		if(pitPlayer != this.pitPlayer) return;
		if(pitPlayer.megastreak.isOnMega() && pitPlayer.megastreak.getClass() == RNGesus.class) {

			if(reality == Reality.DAMAGE) {
				realityMap.get(reality).progression += attackEvent.getFinalDamage();
			} else if(reality == Reality.ABSORPTION) {
				realityMap.get(reality).progression += attackEvent.trueDamage;
			}
			setXPBar();
		}
	}

	@EventHandler
	public void onHeal(HealEvent event) {
		Player player = event.getPlayer();
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		if(pitPlayer != this.pitPlayer) return;
		if(pitPlayer.getKills() + 1 < INSTABILITY_THRESHOLD || pitPlayer.megastreak.getClass() != RNGesus.class) return;
		event.multipliers.add(0.0);
	}

	@Override
	public void proc() {
		MegastreakPanel.rngsusCooldown(pitPlayer.player);
		Sounds.MEGA_RNGESUS.play(pitPlayer.player.getLocation());

		String message = "%luckperms_prefix%";
		if(pitPlayer.megastreak.isOnMega()) {
			pitPlayer.prefix = pitPlayer.megastreak.getName() + " &7" + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		} else {
			pitPlayer.prefix = PrestigeValues.getPlayerPrefixNameTag(pitPlayer.player) + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		}

		pitPlayer.megastreak = this;
		for(Player player : Bukkit.getOnlinePlayers()) {
			PitPlayer pitPlayer2 = PitPlayer.getPitPlayer(player);
			if(pitPlayer2.streaksDisabled) continue;
			String streakMessage = ChatColor.translateAlternateColorCodes('&',
					"&c&lMEGASTREAK! %luckperms_prefix%" + pitPlayer.player.getDisplayName() + " &7activated &c&lOVERDRIVE&7!");
			AOutput.send(player, PlaceholderAPI.setPlaceholders(pitPlayer.player, streakMessage));
		}

		runnable = new BukkitRunnable() {
			@Override
			public void run() {
				if(pitPlayer.megastreak.getClass() != RNGesus.class) return;
				if(pitPlayer.getKills() < INSTABILITY_THRESHOLD && isOnMega()) {
					DecimalFormat decimalFormat = new DecimalFormat("#,####,##0");
					switch(reality) {
						case NONE:
							String realityString = "Reality appears normal";
							char[] chars = realityString.toCharArray();
							String finalString = "";
							for(int i = realityString.length() - 1; i >= 0; i--) {
								if(Math.random() > 0.2) {
									finalString = chars[i] + finalString;
								} else {
									finalString = "&k" + chars[i] + "&7" + finalString;
								}
							}
							sendActionBar(pitPlayer.player, "&7" + finalString);
							break;
						case XP:
							double xp = realityMap.get(Reality.XP).progression;
							sendActionBar(pitPlayer.player, "&bXP: " + decimalFormat.format(xp));
							break;
						case GOLD:
							double gold = realityMap.get(Reality.GOLD).progression;
							sendActionBar(pitPlayer.player, "&6Gold: " + decimalFormat.format(gold));
							break;
						case DAMAGE:
							double damage = realityMap.get(Reality.DAMAGE).progression;
							sendActionBar(pitPlayer.player, "&cDamage: " + decimalFormat.format(damage));
							break;
						case ABSORPTION:
							double absorption = realityMap.get(Reality.ABSORPTION).progression;
							sendActionBar(pitPlayer.player, "&9True Damage: " + decimalFormat.format(absorption));
					}
				} else {
					EntityPlayer nmsPlayer = ((CraftPlayer) pitPlayer.player).getHandle();
					if(nmsPlayer.getAbsorptionHearts() > 0) {
						nmsPlayer.setAbsorptionHearts(Math.max(nmsPlayer.getAbsorptionHearts() - 0.2F, 0));
					} else if(pitPlayer.player.getHealth() > 0.2) {
						pitPlayer.player.setHealth(pitPlayer.player.getHealth() - 0.2);
					} else {
						UUID attackerUUID = pitPlayer.lastHitUUID;
						for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
							if(onlinePlayer.getUniqueId().equals(attackerUUID)) {
								Map<PitEnchant, Integer> attackerEnchant = new HashMap<>();
								Map<PitEnchant, Integer> defenderEnchant = new HashMap<>();
								EntityDamageByEntityEvent ev = new EntityDamageByEntityEvent(onlinePlayer, pitPlayer.player, EntityDamageEvent.DamageCause.CUSTOM, 0);
								AttackEvent attackEvent = new AttackEvent(ev, attackerEnchant, defenderEnchant, false);

								DamageManager.kill(attackEvent, onlinePlayer, pitPlayer.player, false);
								return;
							}
						}
						DamageManager.death(pitPlayer.player);
					}
				}
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 20L);

		if(pitPlayer.stats != null) pitPlayer.stats.timesOnOverdrive++;
	}

	@Override
	public void reset() {
		generateRealityOrder();
		realityMap.clear();
		for(Reality value : Reality.values()) realityMap.put(value, new RealityInfo(value));
		reality = Reality.NONE;

		String message = "%luckperms_prefix%";
		if(pitPlayer.megastreak.isOnMega()) {
			pitPlayer.prefix = pitPlayer.megastreak.getName() + " &7" + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		} else {
			pitPlayer.prefix = PrestigeValues.getPlayerPrefixNameTag(pitPlayer.player) + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		}

		if(runnable != null) runnable.cancel();
	}

	@Override
	public void stop() {
		HandlerList.unregisterAll(this);
	}

	public void shiftReality() {
		reality = generatedRealityOrder.remove(0);
		if(reality == null) return;
		AOutput.send(pitPlayer.player, "&e&lRNGESUS! &7Reality Shift: " + reality.displayName + "&7!");
		ASound.play(pitPlayer.player, Sound.FIZZ, 1000, 0.5F);
		Misc.applyPotionEffect(pitPlayer.player, PotionEffectType.BLINDNESS, 40, 0, true, false);

		String message = "%luckperms_prefix%";
		if(pitPlayer.megastreak.isOnMega()) {
			pitPlayer.prefix = pitPlayer.megastreak.getName() + " &7" + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		} else {
			pitPlayer.prefix = PrestigeValues.getPlayerPrefixNameTag(pitPlayer.player) + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		}
	}

	public void destabilize() {
		reality = Reality.NONE;
		Sounds.RNGESUS_DESTABILIZE.play(pitPlayer.player);

		EntityPlayer nmsPlayer = ((CraftPlayer) pitPlayer.player).getHandle();
		nmsPlayer.setAbsorptionHearts(getAbsorption(realityMap.get(Reality.ABSORPTION).getLevel()));

		AOutput.send(pitPlayer.player, "&bXP &7increased by &b" + getDamage(realityMap.get(Reality.XP).getLevel()));
		AOutput.send(pitPlayer.player, "&6Gold &7increased by &6" + getDamage(realityMap.get(Reality.GOLD).getLevel()));
		AOutput.send(pitPlayer.player, "&cDamage &7increased by &c" + getDamage(realityMap.get(Reality.DAMAGE).getLevel()));
		AOutput.send(pitPlayer.player, "&6Absorption &7increased by &9" + getDamage(realityMap.get(Reality.ABSORPTION).getLevel()));

		String message = "%luckperms_prefix%";
		if(pitPlayer.megastreak.isOnMega()) {
			pitPlayer.prefix = pitPlayer.megastreak.getName() + " &7" + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		} else {
			pitPlayer.prefix = PrestigeValues.getPlayerPrefixNameTag(pitPlayer.player) + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		}
	}

	public void generateRealityOrder() {
		generatedRealityOrder.clear();
		for(Reality value : Reality.values()) {
			if(value == Reality.NONE) continue;
			generatedRealityOrder.add(value);
		}
		for(int i = generatedRealityOrder.size(); i < 8; i++) {
			List<Reality> randomRealities = new ArrayList<>(Arrays.asList(Reality.values()));
			randomRealities.remove(0);
			Collections.shuffle(randomRealities);
			generatedRealityOrder.add(randomRealities.get(0));
		}
		Collections.shuffle(generatedRealityOrder);
		generatedRealityOrder.add(0, Reality.NONE);
	}

	public void setXPBar() {
		RealityInfo realityInfo = realityMap.get(reality);

		int level = realityInfo.getLevel();
		float currentAmount = (float) realityInfo.progression;
		float currentTier = (float) realityInfo.getProgression(level);
		float nextTier = (float) realityInfo.getProgression(level + 1);

		pitPlayer.player.setLevel(level);
		float ratio = (currentAmount - currentTier) / (nextTier - currentTier);
		pitPlayer.player.setExp(ratio);
	}

	public enum Reality {
		NONE("&eNormal?", "&e&lRNGSUS", 1),
		XP("&bXP", "&b&lRNG&e&lSUS", 3),
		GOLD("&6Gold", "&6&lRNG&e&lSUS", 3),
		DAMAGE("&cDamage", "&c&lRNG&e&lSUS", 3),
		ABSORPTION("&6Absorption", "&9&lRNG&e&lSUS", 3);

		public String displayName;
		public String prefix;
		public int baseMultiplier;

		Reality(String displayName, String prefix, int baseMultiplier) {
			this.displayName = displayName;
			this.prefix = prefix;
			this.baseMultiplier = baseMultiplier;
		}
	}

	public static class RealityInfo {
		public Reality reality;
		public double progression;

		public RealityInfo(Reality reality) {
			this.reality = reality;
		}

		public int getLevel() {
			double modifiableProgression = progression / reality.baseMultiplier;
			int level = 0;
			while(modifiableProgression >= level + 1) {
				modifiableProgression -= level + 1;
				level++;
			}
			return level;
		}

		public double getProgression(int level) {
			int progression = 0;
			for(int i = 0; i < level + 1; i++) {
				progression += i;
			}
			return progression * reality.baseMultiplier;
		}
	}

	public static void sendActionBar(Player player, String message) {
		PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" +
				ChatColor.translateAlternateColorCodes('&', message) + "\"}"), (byte) 2);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}
}
