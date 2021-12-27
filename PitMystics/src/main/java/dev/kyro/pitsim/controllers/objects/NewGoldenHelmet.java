package dev.kyro.pitsim.controllers.objects;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.HelmetSystem;
import dev.kyro.pitsim.controllers.NonManager;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.enchants.ComboVenom;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.events.AttackEvent;
import dev.kyro.pitsim.events.DoubleSneakEvent;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.helmetabilities.*;
import dev.kyro.pitsim.inventories.HelmetGUI;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.*;

public class NewGoldenHelmet implements Listener {

	public static Map<Player, HelmetAbility> abilities = new HashMap<>();
	public static List<Player> toggledPlayers = new ArrayList<>();
	public static DecimalFormat formatter = new DecimalFormat("#,###.#");
	private final List<Material> armorMaterials = Collections.singletonList(Material.GOLD_HELMET);

	@EventHandler
	public void onDoubleSneak(DoubleSneakEvent event) {

		Player player = event.getPlayer();
		if(getHelmet(player) == null) return;
		ItemStack helmet = getHelmet(player);

		if(!abilities.containsKey(player)) generateAbility(player);

		if(abilities.get(player) == null) {
			AOutput.error(player, "&6&lGOLDEN HELMET! &cNo ability selected!");
			Sounds.NO.play(player);
			return;
		}

		if(!abilities.get(player).refName.equals(Objects.requireNonNull(getAbility(player, getHelmet(player))).refName)) generateAbility(player);

		HelmetAbility ability = abilities.get(player);
		if(!ability.shouldActivate()) return;

		if(ability.isTogglable) {
			if(toggledPlayers.contains(player)) {
				ability.onDeactivate();
				ability.isActive = true;
				toggledPlayers.remove(player);
			} else {
				ability.onActivate();
				ability.isActive = false;
				toggledPlayers.add(player);
			}

		} else ability.onProc();
	}

	public static ItemStack getHelmet(Player player) {
		if(Misc.isAirOrNull(player.getInventory().getHelmet())) return null;
		NBTItem nbtItem = new NBTItem(player.getInventory().getHelmet());
		if(!nbtItem.hasKey(NBTTag.GHELMET_UUID.getRef())) return null;

		return player.getInventory().getHelmet();
	}

	public static int getUsedHelmetGold(Player player) {
		ItemStack helmet = getHelmet(player);
		if(helmet == null) return 0;

		NBTItem nbtItem = new NBTItem(helmet);
		return nbtItem.getInteger(NBTTag.GHELMET_GOLD.getRef());
	}

	public static int getHelmetGold(ItemStack helmet) {
		if(helmet == null) return 0;

		NBTItem nbtItem = new NBTItem(helmet);
		return nbtItem.getInteger(NBTTag.GHELMET_GOLD.getRef());
	}

	public static void generateAbility(Player player) {
		ItemStack helmet = getHelmet(player);
		if(helmet == null) return;

		NBTItem nbtItem = new NBTItem(helmet);

		abilities.put(player, generateInstance(player, nbtItem.getString(NBTTag.GHELMET_ABILITY.getRef())));
	}

	public static HelmetAbility getAbility(Player player, ItemStack helmet) {
		if(helmet == null) return null;

		NBTItem nbtItem = new NBTItem(helmet);
		return generateInstance(player, nbtItem.getString(NBTTag.GHELMET_ABILITY.getRef()));
	}

	public static UUID getUUID(Player player) {
		ItemStack helmet = getHelmet(player);
		if(helmet == null) return null;

		NBTItem nbtItem = new NBTItem(helmet);
		return UUID.fromString(nbtItem.getString(NBTTag.GHELMET_UUID.getRef()));
	}

	public void depositGold(Player player, ItemStack helmet, int gold) {
		NBTItem nbtItem = new NBTItem(helmet);
		nbtItem.setInteger(NBTTag.GHELMET_GOLD.getRef(), (getHelmetGold(helmet) + gold));

		if(getHelmet(player) == helmet) {
			player.getInventory().setHelmet(nbtItem.getItem());
		} else player.setItemInHand(nbtItem.getItem());

		setLore(player, helmet);
	}

	public static boolean withdrawGold(Player player, ItemStack helmet, int gold) {
		NBTItem nbtItem = new NBTItem(helmet);
		int helmetGold = nbtItem.getInteger(NBTTag.GHELMET_GOLD.getRef());

		if(helmetGold < gold) return false;
		else {
			if(HelmetSystem.getLevel(helmetGold - gold) < HelmetSystem.getLevel(helmetGold)) {
				AOutput.send(player, "&6&lGOLDEN HELMET! &7Helmet level reduced to &f" +
						HelmetSystem.getLevel(helmetGold - gold) + "&7. (&6" + formatter.format(helmetGold - gold) + "g&7)");
				Sounds.HELMET_DOWNGRADE.play(player);
			}
			helmetGold -= gold;
			nbtItem.setInteger(NBTTag.GHELMET_GOLD.getRef(), helmetGold);

			if(getHelmet(player) == helmet) {
				player.getInventory().setHelmet(nbtItem.getItem());
			} else player.setItemInHand(nbtItem.getItem());

			setLore(player, helmet);
		}
		return true;
	}

	public static void deactivate(Player player) {
		if(!abilities.containsKey(player)) return;
		HelmetAbility ability = abilities.get(player);

		ability.onDeactivate();
		ability.isActive = false;
		toggledPlayers.remove(player);
	}

	public static HelmetAbility generateInstance(Player player, String refName) {
		if(refName.equals("leap")) return new LeapAbility(player);
		if(refName.equals("pitblob")) return new BlobAbility(player);
		if(refName.equals("goldrush")) return new GoldRushAbility(player);
		if(refName.equals("hermit")) return new HermitAbility(player);
		if(refName.equals("judgement")) return new JudgementAbility(player);
		if(refName.equals("phoenix")) return new PhoenixAbility(player);
		return null;
	}

	public static void setLore(Player player, ItemStack helmet) {

		ALoreBuilder loreBuilder = new ALoreBuilder();
		loreBuilder.addLore("");
		HelmetAbility ability = getAbility(player, helmet);
		int gold = getHelmetGold(helmet);

		if(ability != null) {
			loreBuilder.addLore("&7Ability: &9" + ability.name);
			loreBuilder.addLore(ability.getDescription());
		}
		else loreBuilder.addLore("&7Ability: &cNONE");
		loreBuilder.addLore("", "&7Passives:");
		int passives = 0;
		for(HelmetSystem.Passive passive : HelmetSystem.Passive.values()) {
			int level = HelmetSystem.getLevel(gold);
			int passiveLevel = HelmetSystem.getTotalStacks(passive,level - 1);

			if(passiveLevel == 0) continue;
			passives++;

			if(passive.name().equals("DAMAGE_REDUCTION")) {
				loreBuilder.addLore(passive.color + "-" + passiveLevel * passive.baseUnit + "% " + passive.refName);
				continue;
			}
			if(passive.name().equals("SHARD_CHANCE"))  {
				loreBuilder.addLore(passive.color + "+" + passiveLevel * (passive.baseUnit / 10) + "% " + passive.refName);
				continue;
			}
			loreBuilder.addLore(passive.color + "+" + passiveLevel * passive.baseUnit + "% " + passive.refName);
		}
		if(passives == 0) loreBuilder.addLore("&cNONE");
		loreBuilder.addLore("", "&7Gold: &6" + formatter.format(gold) + "g", "", "&eHold and right-click to modify!");

		ItemMeta meta = helmet.getItemMeta();
		meta.setLore(loreBuilder.getLore());
		helmet.setItemMeta(meta);

		NBTItem nbtItem = new NBTItem(helmet);
		nbtItem.setInteger(NBTTag.GHELMET_GOLD.getRef(), gold);
		if(ability != null) nbtItem.setString(NBTTag.GHELMET_ABILITY.getRef(), ability.refName);
		helmet.setAmount(1);

		if(getHelmet(player) == helmet) {
			player.getInventory().setHelmet(nbtItem.getItem());
		} else player.setItemInHand(nbtItem.getItem());
	}

	public List<Player> crouchPlayers = new ArrayList<>();

	@EventHandler
	public void onCrouch(PlayerToggleSneakEvent event) {
		Player player = event.getPlayer();
		if(!event.isSneaking()) return;
		if(ComboVenom.isVenomed(player)) return;

		if(!crouchPlayers.contains(player)) {
			crouchPlayers.add(player);
			new BukkitRunnable() {
				@Override
				public void run() {

					crouchPlayers.remove(player);
				}
			}.runTaskLater(PitSim.INSTANCE, 7L);
			return;
		}
		crouchPlayers.remove(player);
		DoubleSneakEvent newEvent = new DoubleSneakEvent(player);
		Bukkit.getPluginManager().callEvent(newEvent);

	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		crouchPlayers.remove(event.getPlayer());

		if(abilities.get(event.getPlayer()) != null) {
			abilities.get(event.getPlayer()).onDeactivate();
		}
		abilities.remove(event.getPlayer());
		toggledPlayers.remove(event.getPlayer());
	}

	@EventHandler
	public void onRemove(InventoryClickEvent event) {

		Player player = (Player) event.getWhoClicked();

		if(event.getClickedInventory() == null || event.getClickedInventory().getType() != InventoryType.PLAYER) return;
		if(Misc.isAirOrNull(player.getInventory().getHelmet())) return;
		if(event.getSlot() == 39 && player.getInventory().getHelmet().getType() == Material.GOLD_HELMET) {
			if(abilities.get(player) != null) {
				abilities.get(player).onDeactivate();
			}
			toggledPlayers.remove(player);
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		if(event.getItemDrop().getItemStack().getType() != Material.GOLD_HELMET) return;
		if(abilities.get(event.getPlayer()) != null) {
			abilities.get(event.getPlayer()).onDeactivate();
		}
		toggledPlayers.remove(event.getPlayer());
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		if(abilities.get(player) != null) {
			abilities.get(player).onDeactivate();
		}
		toggledPlayers.remove(player);
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		if(HelmetGUI.depositPlayers.containsKey(event.getPlayer())){
			event.setCancelled(true);
			ItemStack helmet = HelmetGUI.depositPlayers.get(event.getPlayer());

			if(Misc.isAirOrNull(helmet)) {
				HelmetGUI.depositPlayers.remove(event.getPlayer());
				return;
			}

			int gold = 0;

			try {
				gold = Integer.parseInt(ChatColor.stripColor(event.getMessage()));
				if(gold <= 0) throw new Exception();
			} catch(Exception e) {
				AOutput.send(event.getPlayer(), "&cThat is not a valid number!");
				HelmetGUI.depositPlayers.remove(event.getPlayer());
				Sounds.NO.play(event.getPlayer());
				return;
			}

			double finalBalance = PitSim.VAULT.getBalance((Player) event.getPlayer()) - gold;
			if(finalBalance < 0) {
				AOutput.send(event.getPlayer(), "&cYou do not have enough gold!");
				HelmetGUI.depositPlayers.remove(event.getPlayer());
				Sounds.NO.play(event.getPlayer());
				return;
			}
			PitSim.VAULT.withdrawPlayer((Player) event.getPlayer(), gold);

			if(Misc.isAirOrNull(event.getPlayer().getItemInHand())) return;
			NBTItem nbtItem = new NBTItem(event.getPlayer().getItemInHand());

			if(!nbtItem.hasKey(NBTTag.GHELMET_UUID.getRef())) {
				AOutput.send(event.getPlayer(), "&cUnable to find helmet!");
				HelmetGUI.depositPlayers.remove(event.getPlayer());
				Sounds.NO.play(event.getPlayer());
				return;
			}

			depositGold(event.getPlayer(), helmet, gold);

			AOutput.send(event.getPlayer(), "&aSuccessfully deposited gold!");
			HelmetGUI.depositPlayers.remove(event.getPlayer());
			Sounds.HELMET_DEPOSIT_GOLD.play(event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Action action = event.getAction();
		if ((action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.RIGHT_CLICK_AIR)) && this.armorMaterials.contains(player.getItemInHand().getType())) {
			event.setCancelled(true);
			player.updateInventory();
		}
		if(Misc.isAirOrNull(event.getPlayer().getItemInHand())) return;
		NBTItem nbtItem = new NBTItem(event.getPlayer().getItemInHand());
		if(!nbtItem.hasKey(NBTTag.GHELMET_UUID.getRef())) return;

		if(!UpgradeManager.hasUpgrade(event.getPlayer(), "HELMETRY")) {
			AOutput.error(event.getPlayer(), "&cYou must first unlock &6Helmetry &cfrom the renown shop before using this item!");
			Sounds.NO.play(event.getPlayer());
			return;
		}

		Sounds.HELMET_GUI_OPEN.play(event.getPlayer());
		HelmetGUI helmetGUI = new HelmetGUI(event.getPlayer());
		helmetGUI.open();
	}

	@EventHandler
	public void onAttack(AttackEvent.Apply attackEvent) {
		if(NonManager.getNon(attackEvent.attacker) != null || NonManager.getNon(attackEvent.defender) != null) return;
		ItemStack attackerHelmet = getHelmet(attackEvent.attacker);
		ItemStack defenderHelmet = getHelmet(attackEvent.defender);

		int attackLevel = 0;
		if(attackerHelmet != null) attackLevel = HelmetSystem.getLevel(getUsedHelmetGold(attackEvent.attacker));
		if(attackerHelmet != null) attackEvent.increasePercent += HelmetSystem.getTotalStacks(HelmetSystem.Passive.DAMAGE, attackLevel - 1) / 100D;

		int defenderLevel = 0;
		if(defenderHelmet != null) defenderLevel = HelmetSystem.getLevel(getUsedHelmetGold(attackEvent.defender));
		if(defenderHelmet != null) attackEvent.multiplier.add(Misc.getReductionMultiplier(HelmetSystem.getTotalStacks(HelmetSystem.Passive.DAMAGE_REDUCTION, defenderLevel - 1)));
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {

		Player dead = killEvent.dead;
		if(abilities.get(dead) != null) {
			abilities.get(dead).onDeactivate();
		}
		toggledPlayers.remove(dead);

		if(NonManager.getNon(killEvent.killer) != null) return;
		if(Misc.isAirOrNull(killEvent.killer.getInventory().getHelmet())) return;
		if(killEvent.killer.getInventory().getHelmet().getType() != Material.GOLD_HELMET) return;

		ItemStack helmet = getHelmet(killEvent.killer);
		if(helmet == null) return;

		int level = HelmetSystem.getLevel(getUsedHelmetGold(killEvent.killer));

		killEvent.goldMultipliers.add(1 + HelmetSystem.getTotalStacks(HelmetSystem.Passive.GOLD_BOOST,  level  - 1) / 100D);
		killEvent.xpMultipliers.add(1 + HelmetSystem.getTotalStacks(HelmetSystem.Passive.XP_BOOST, level - 1) / 100D);
	}
}
