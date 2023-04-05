package dev.kyro.pitsim.megastreaks;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.aitems.MysticFactory;
import dev.kyro.pitsim.aitems.misc.ChunkOfVile;
import dev.kyro.pitsim.aitems.misc.FunkyFeather;
import dev.kyro.pitsim.aitems.diamond.ProtBoots;
import dev.kyro.pitsim.aitems.diamond.ProtChestplate;
import dev.kyro.pitsim.aitems.diamond.ProtHelmet;
import dev.kyro.pitsim.aitems.diamond.ProtLeggings;
import dev.kyro.pitsim.battlepass.quests.CompleteUbersQuest;
import dev.kyro.pitsim.battlepass.quests.daily.DailyMegastreakQuest;
import dev.kyro.pitsim.controllers.*;
import dev.kyro.pitsim.controllers.objects.Megastreak;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.cosmetics.CosmeticManager;
import dev.kyro.pitsim.cosmetics.CosmeticType;
import dev.kyro.pitsim.cosmetics.PitCosmetic;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.HealEvent;
import dev.kyro.pitsim.events.IncrementKillsEvent;
import dev.kyro.pitsim.misc.CustomSerializer;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import dev.kyro.pitsim.upgrades.VentureCapitalist;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.*;

public class Uberstreak extends Megastreak {
	public static double SHARD_MULTIPLIER = 2;

	public List<UberEffect> uberEffects = new ArrayList<>();

	public Uberstreak(PitPlayer pitPlayer) {
		super(pitPlayer);
	}

	@EventHandler
	public void onPreAttack(AttackEvent.Pre attackEvent) {
		if(!attackEvent.isAttackerPlayer()) return;
		PitPlayer pitAttacker = attackEvent.getAttackerPitPlayer();
		if(pitAttacker != this.pitPlayer || !(pitAttacker.megastreak instanceof Uberstreak) || pitAttacker.megastreak.isOnMega())
			return;

		Map<PitEnchant, Integer> attackerEnchantMap = attackEvent.getAttackerEnchantMap();

		PitEnchant exe = EnchantManager.getEnchant("executioner");
		if(uberEffects.contains(UberEffect.EXE_SUCKS) && attackerEnchantMap.containsKey(exe))
			attackerEnchantMap.put(exe,
					Math.max(0, attackerEnchantMap.get(exe) - 1));

		PitEnchant perun = EnchantManager.getEnchant("perun");
		if(uberEffects.contains(UberEffect.PERUN_SUCKS) && attackerEnchantMap.containsKey(perun))
			attackerEnchantMap.put(perun,
					Math.max(0, attackerEnchantMap.get(perun) - 1));
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(!attackEvent.isDefenderPlayer()) return;
		PitPlayer pitDefender = PitPlayer.getPitPlayer(attackEvent.getDefender());
		if(pitDefender == this.pitPlayer && pitDefender.megastreak instanceof Uberstreak) {
			if(uberEffects.contains(UberEffect.TAKE_MORE_DAMAGE)) attackEvent.multipliers.add(1.25);
			if(uberEffects.contains(UberEffect.TAKE_LESS_DAMAGE)) attackEvent.multipliers.add(0.9);
		}

		if(!attackEvent.isAttackerPlayer()) return;
		PitPlayer pitAttacker = PitPlayer.getPitPlayer(attackEvent.getAttacker());
		if(pitAttacker != this.pitPlayer || !(pitAttacker.megastreak instanceof Uberstreak)) return;
		if(pitAttacker.megastreak.isOnMega()) {
			if(NonManager.getNon(attackEvent.getDefender()) != null) attackEvent.multipliers.add(0.5);
		}
	}

	@EventHandler
	public void onHeal(HealEvent event) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(event.player);
		if(pitPlayer != this.pitPlayer) return;

		if(uberEffects.contains(UberEffect.HEAL_LESS)) event.multipliers.add(0.75);

		if(pitPlayer.getKills() < 500) return;
		event.multipliers.add(0D);
	}

	@EventHandler
	public void onKill(IncrementKillsEvent event) {
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(event.player);
		if(pitPlayer != this.pitPlayer) return;
		if(pitPlayer.megastreak.isOnMega() && pitPlayer.megastreak instanceof Uberstreak) {
			double current = event.kills;

			if(current == 200) {
				Sounds.UBER_200.play(pitPlayer.player);
				UberEffect uberEffect = UberEffect.getRandom(uberEffects);
				if(uberEffects.size() < 1) uberEffects.add(uberEffect);
				if(uberEffect == UberEffect.SKIP_100) zoom();
				pitPlayer.updateMaxHealth();
				AOutput.send(pitPlayer.player, "&d&lUBERSTREAK!&7 Random Effect: " + uberEffect.description);
			}
			if(current == 300) {
				Sounds.UBER_300.play(pitPlayer.player);
				UberEffect uberEffect = UberEffect.getRandom(uberEffects);
				if(uberEffects.size() < 2) uberEffects.add(uberEffect);
				if(uberEffect == UberEffect.SKIP_100) zoom();
				pitPlayer.updateMaxHealth();
				AOutput.send(pitPlayer.player, "&d&lUBERSTREAK!&7 Random Effect: " + uberEffect.description);
			}
			if(current == 400) {
				Sounds.UBER_400.play(pitPlayer.player);
				UberEffect uberEffect = UberEffect.getRandom(uberEffects);
				if(uberEffects.size() < 3) uberEffects.add(uberEffect);
				if(uberEffect == UberEffect.SKIP_100) zoom();
				pitPlayer.updateMaxHealth();
				AOutput.send(pitPlayer.player, "&d&lUBERSTREAK!&7 Random Effect: " + uberEffect.description);
			}
			if(current == 500) {
				Sounds.UBER_500.play(pitPlayer.player);
				pitPlayer.updateMaxHealth();
				AOutput.send(pitPlayer.player, "&d&lUBERSTREAK!&c Cannot heal");
			}
		}
	}

	public void zoom() {
		new BukkitRunnable() {
			int count = 0;

			@Override
			public void run() {
				if(count++ == 50) {
					cancel();
					return;
				}

				pitPlayer.incrementKills();

				PitCosmetic botKill = CosmeticManager.getEquippedCosmetic(pitPlayer, CosmeticType.BOT_KILL_EFFECT);
				Misc.playKillSound(pitPlayer, botKill);
			}
		}.runTaskTimer(PitSim.INSTANCE, 0L, 3L);
	}

	@Override
	public void proc() {
		String message = "%luckperms_prefix%";
		if(pitPlayer.megastreak.isOnMega()) {
			pitPlayer.prefix = pitPlayer.megastreak.getName() + " &7" + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		} else {
			pitPlayer.prefix = PrestigeValues.getPlayerPrefixNameTag(pitPlayer.player) + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		}

		Sounds.UBER_100.play(pitPlayer.player);
		for(Player player : Bukkit.getOnlinePlayers()) {
			PitPlayer pitPlayer2 = PitPlayer.getPitPlayer(player);
			if(pitPlayer2.streaksDisabled) continue;
			String message2 = ChatColor.translateAlternateColorCodes('&',
					"&c&lMEGASTREAK! %luckperms_prefix%" + pitPlayer.player.getDisplayName() + "&7 activated &d&lUBERSTREAK&7!");

			player.sendMessage(PlaceholderAPI.setPlaceholders(pitPlayer.player, message2));
		}
		pitPlayer.updateMaxHealth();
		AOutput.send(pitPlayer.player, "&d&lUBERSTREAK!&c Deal -50% damage to nons");
	}

	@Override
	public void reset() {
		pitPlayer.updateMaxHealth();
		uberEffects.clear();
		if(pitPlayer.getKills() < 500) return;
		if(!isOnMega()) return;

		if(pitPlayer.uberReset == 0) {
			pitPlayer.uberReset = System.currentTimeMillis() + 1000 * 60 * 60 * 20;
		}
		pitPlayer.dailyUbersLeft = pitPlayer.dailyUbersLeft - 1;
		ChatTriggerManager.sendUberInfo(pitPlayer);

		if(pitPlayer.dailyUbersLeft <= 0) {
			pitPlayer.megastreak = new NoMegastreak(pitPlayer);
			ChatTriggerManager.sendPerksInfo(pitPlayer);
			stop();
		}

		String message = "%luckperms_prefix%";
		if(pitPlayer.megastreak.isOnMega()) {
			pitPlayer.prefix = pitPlayer.megastreak.getName() + " &7" + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		} else {
			pitPlayer.prefix = PrestigeValues.getPlayerPrefixNameTag(pitPlayer.player) + PlaceholderAPI.setPlaceholders(pitPlayer.player, message);
		}

		Uberdrop uberdrop = Uberdrop.getRandom();
		uberdrop.give(pitPlayer);

		if(pitPlayer.stats != null) pitPlayer.stats.ubersCompleted++;
		CompleteUbersQuest.INSTANCE.onUberComplete(pitPlayer);
		DailyMegastreakQuest.INSTANCE.onMegastreakComplete(pitPlayer);
	}

	public static void sendUberMessage(String displayName, ItemStack itemStack) {
		TextComponent message = new TextComponent(ChatColor.translateAlternateColorCodes('&',
				"&d&lUBERDROP!&7 " + displayName + " &7obtained an &dUberdrop: &7"));
		message.addExtra(Misc.createItemHover(itemStack));
		for(Player onlinePlayer : Bukkit.getOnlinePlayers()) onlinePlayer.sendMessage(message);
	}

	@Override
	public void stop() {
		HandlerList.unregisterAll(this);
	}

	@Override
	public void kill() {

		if(!isOnMega()) return;
	}

	@Override
	public String getName() {
		return "&d&lUBER";
	}

	@Override
	public String getRawName() {
		return "Uberstreak";
	}

	@Override
	public String getPrefix() {
		return "&dUberstreak";
	}

	@Override
	public List<String> getRefNames() {
		return Arrays.asList("uberstreak");
	}

	@Override
	public int guiSlot() {
		return 14;
	}

	@Override
	public int prestigeReq() {
		return 20;
	}

	@Override
	public int initialLevelReq() {
		return 100;
	}

	@Override
	public int getRequiredKills() {
		return 100;
	}

	@Override
	public ItemStack guiItem() {
		DecimalFormat decimalFormat = new DecimalFormat("0.#");

		ItemStack item = new ItemStack(Material.GOLD_SWORD);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7Triggers on: &c100 kills"));
		lore.add("");
		lore.add(ChatColor.GRAY + "On trigger:");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&a\u25a0 &7Immune to enchants that &emove &7you"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&a\u25a0 &d" + decimalFormat.format(SHARD_MULTIPLIER) + "x &7chance to find &aAncient Gem Shards"));
		lore.add("");
		lore.add(ChatColor.GRAY + "BUT:");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&c\u25a0 &7Deal &c-50% &7damage to nons"));
		lore.add("");
		lore.add(ChatColor.GRAY + "During the streak:");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&d\u25a0 &7200 kills: Random &dbuff &7or &cdebuff"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&d\u25a0 &7300 kills: Random &dbuff &7or &cdebuff"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&d\u25a0 &7400 kills: Random &dbuff &7or &cdebuff"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&d\u25a0 &7500 kills: &cNo longer gain health"));
		lore.add("");
		lore.add(ChatColor.GRAY + "On death:");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&e\u25a0 &7Earn a random &dUberdrop&7"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7(If streak is at least 500)"));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	@Override
	public String getSummary() {
		return "&d&lUBERSTREAK&7 grants you immunity to enchants that move you, double chance to find &agem shards&7, " +
				"gain random &abuffs&7 or &cdebuffs&7 every 100 kills, gain a reward at &c500 streak&7, but deal a " +
				"lot less damage to bots and only have five &cUberstreaks&7 daily";
	}

	public enum UberEffect {
		NO_SPEED("&CYou cannot gain speed"),
		TAKE_MORE_DAMAGE("&CTake 25% more damage"),
		EXE_SUCKS("&cExecutioner level -1"),
		PERUN_SUCKS("&cCombo: Perun level -1"),
		LOSE_MAX_HEALTH("&c-" + Misc.getHearts(4) + " max hp"),
		HEAL_LESS("&cHeal 25% less from all sources"),

		NONE("&7Nothing happens... Yay?"),

		TAKE_LESS_DAMAGE("&dTake 10% less damage"),
		SKIP_100("&dZoooom");

		public String description;

		UberEffect(String description) {
			this.description = description;
		}

		public static UberEffect getRandom(List<UberEffect> uberEffects) {
			List<UberEffect> possible = new ArrayList<>();
			for(UberEffect value : values()) {
				if(!uberEffects.contains(value)) possible.add(value);
			}

			Collections.shuffle(possible);
			return possible.get(0);
		}
	}

	public static List<Uberdrop> weightedDropList = new ArrayList<>();

	public enum Uberdrop {
		JEWEL_SWORD(5),
		JEWEL_BOW(5),
		JEWEL_PANTS(5),
		JEWEL_BUNDLE(2),
		FEATHER_1(3),
		FEATHER_2(2),
		FEATHER_3(1),
		VILE_2(6),
		VILE_3(4),
		VILE_5(2),
		P1_HELMET(5),
		P1_CHESTPLATE(5),
		P1_LEGGINGS(7),
		P1_BOOTS(5);

		public int weight;

		Uberdrop(int weight) {
			this.weight = weight;
			for(int i = 0; i < weight; i++) weightedDropList.add(this);
		}

		public static Uberdrop getRandom() {
			List<Uberdrop> tempDropList = new ArrayList<>(weightedDropList);
			Collections.shuffle(tempDropList);
			return tempDropList.get(0);
		}

		public void give(PitPlayer pitPlayer) {
			Player player = pitPlayer.player;
			String displayName = Misc.getDisplayName(player);
			ItemStack displayStack = null;
			if(this == JEWEL_SWORD) {
				ItemStack jewelSword = MysticFactory.getJewelItem(MysticType.SWORD);
				AUtil.giveItemSafely(player, jewelSword);
				displayStack = getDisplayStack("&eHidden Jewel Sword", jewelSword);
			} else if(this == JEWEL_BOW) {
				ItemStack jewelBow = MysticFactory.getJewelItem(MysticType.BOW);
				AUtil.giveItemSafely(player, jewelBow);
				displayStack = getDisplayStack("&bHidden Jewel Bow", jewelBow);
			} else if(this == JEWEL_PANTS) {
				ItemStack jewelPants = MysticFactory.getJewelItem(MysticType.PANTS);
				AUtil.giveItemSafely(player, jewelPants);
				displayStack = getDisplayStack("&3Hidden Jewel Pants", jewelPants);
			} else if(this == JEWEL_BUNDLE) {
				ItemStack jbsword = MysticFactory.getJewelItem(MysticType.SWORD);
				AUtil.giveItemSafely(player, jbsword);

				ItemStack jbbow = MysticFactory.getJewelItem(MysticType.BOW);
				AUtil.giveItemSafely(player, jbbow);

				ItemStack jbpants = MysticFactory.getJewelItem(MysticType.PANTS);
				AUtil.giveItemSafely(player, jbpants);

				displayStack = new AItemStackBuilder(Material.STORAGE_MINECART)
						.setName("&3Hidden Jewel Bundle")
						.setLore(new ALoreBuilder(
								"",
								"&7Contents:",
								"&31x Hidden Jewel Pants",
								"&e1x Hidden Jewel Sword",
								"&b1x Hidden Jewel Bow"
						)).getItemStack();
			} else if(this == FEATHER_1) {
				ItemFactory.getItem(FunkyFeather.class).giveItem(player, 1);
				displayStack = getDisplayStack("&31x Funky Feather", ItemFactory.getItem(FunkyFeather.class).getItem(1));
			} else if(this == FEATHER_2) {
				ItemFactory.getItem(FunkyFeather.class).giveItem(player, 2);
				displayStack = getDisplayStack("&32x Funky Feather", ItemFactory.getItem(FunkyFeather.class).getItem(2));
			} else if(this == FEATHER_3) {
				ItemFactory.getItem(FunkyFeather.class).giveItem(player, 3);
				displayStack = getDisplayStack("&33x Funky Feather", ItemFactory.getItem(FunkyFeather.class).getItem(3));
			} else if(this == VILE_2) {
				ItemFactory.getItem(ChunkOfVile.class).giveItem(player, 2);
				displayStack = getDisplayStack("&52x Chunk of Vile", ItemFactory.getItem(ChunkOfVile.class).getItem(2));
			} else if(this == VILE_3) {
				ItemFactory.getItem(ChunkOfVile.class).giveItem(player, 3);
				displayStack = getDisplayStack("&53x Chunk of Vile", ItemFactory.getItem(ChunkOfVile.class).getItem(3));
			} else if(this == VILE_5) {
				ItemFactory.getItem(ChunkOfVile.class).giveItem(player, 5);
				displayStack = getDisplayStack("&55x Chunk of Vile", ItemFactory.getItem(ChunkOfVile.class).getItem(5));
			} else if(this == P1_HELMET) {
				ItemFactory.getItem(ProtHelmet.class).giveItem(player, 1);
				displayStack = getDisplayStack("&bProtection I Diamond Helmet", ItemFactory.getItem(ProtHelmet.class).getItem(1));
			} else if(this == P1_CHESTPLATE) {
				ItemFactory.getItem(ProtChestplate.class).giveItem(player, 1);
				displayStack = getDisplayStack("&bProtection I Diamond Chestplate", ItemFactory.getItem(ProtChestplate.class).getItem(1));
			} else if(this == P1_LEGGINGS) {
				ItemFactory.getItem(ProtLeggings.class).giveItem(player, 1);
				displayStack = getDisplayStack("&bProtection I Diamond Leggings", ItemFactory.getItem(ProtLeggings.class).getItem(1));
			} else if(this == P1_BOOTS) {
				ItemFactory.getItem(ProtBoots.class).giveItem(player, 1);
				displayStack = getDisplayStack("&bProtection I Diamond Boots", ItemFactory.getItem(ProtBoots.class).getItem(1));
			}

			sendUberMessage(Misc.getDisplayName(player), displayStack);

			new PluginMessage()
					.writeString("UBERDROP")
					.writeString(PitSim.serverName)
					.writeString(Misc.getDisplayName(player))
					.writeString(CustomSerializer.serialize(displayStack))
					.send();
		}
	}

	public static ItemStack getDisplayStack(String displayName, ItemStack displayStack) {
		return new AItemStackBuilder(displayStack).setName(displayName).getItemStack();
	}

	public static int getMaxUbers(Player player) {
		return 5 + VentureCapitalist.getUberIncrease(player);
	}

	public static void checkUberReset(PitPlayer pitPlayer) {
		if(System.currentTimeMillis() > pitPlayer.uberReset) {
			pitPlayer.uberReset = 0;
			pitPlayer.dailyUbersLeft = Uberstreak.getMaxUbers(pitPlayer.player);
			ChatTriggerManager.sendUberInfo(pitPlayer);
		}
	}
}
