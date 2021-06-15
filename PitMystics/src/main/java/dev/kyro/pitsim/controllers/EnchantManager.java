package dev.kyro.pitsim.controllers;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTList;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.Non;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.*;
import dev.kyro.pitsim.exceptions.*;
import dev.kyro.pitsim.misc.Constant;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EnchantManager {

	public static List<PitEnchant> pitEnchants = new ArrayList<>();

	public static void registerEnchant(PitEnchant pitEnchant) {

		pitEnchants.add(pitEnchant);
		PitSim.INSTANCE.getServer().getPluginManager().registerEvents(pitEnchant, PitSim.INSTANCE);
	}

	public static boolean canTypeApply(ItemStack itemStack, PitEnchant pitEnchant) {

		if(pitEnchant.applyType == ApplyType.ALL) return true;

		if(itemStack.getType() == Material.GOLD_SWORD) {
			return pitEnchant.applyType == ApplyType.WEAPONS || pitEnchant.applyType == ApplyType.SWORDS;
		} else if(itemStack.getType() == Material.BOW) {
			return pitEnchant.applyType == ApplyType.WEAPONS || pitEnchant.applyType == ApplyType.BOWS;
		} else if(itemStack.getType() == Material.LEATHER_LEGGINGS) {
			return pitEnchant.applyType == ApplyType.PANTS;
		}

		return false;
	}

	public static String getMysticType(ItemStack itemStack) {

		switch(itemStack.getType()) {
			case GOLD_SWORD:
				return "Sword";
			case BOW:
				return "Bow";
			case LEATHER_LEGGINGS:
				return "Pants";
		}
		return null;
	}

	public static ItemStack addEnchant(ItemStack itemStack, PitEnchant applyEnchant, int applyLvl, boolean safe) throws Exception {
		return addEnchant(itemStack, applyEnchant, applyLvl, safe, false);
	}

	public static ItemStack addEnchant(ItemStack itemStack, PitEnchant applyEnchant, int applyLvl, boolean safe, boolean jewel) throws Exception {
		NBTItem nbtItem = new NBTItem(itemStack);

		NBTList<String> enchantOrder = nbtItem.getStringList(NBTTag.PIT_ENCHANT_ORDER.getRef());
		NBTCompound itemEnchants = nbtItem.getCompound(NBTTag.PIT_ENCHANTS.getRef());
		Integer currentLvl = itemEnchants.getInteger(applyEnchant.refNames.get(0));
		Integer enchantNum = nbtItem.getInteger(NBTTag.ITEM_ENCHANTS.getRef());
		Integer tokenNum = nbtItem.getInteger(NBTTag.ITEM_TOKENS.getRef());
		Integer rTokenNum = nbtItem.getInteger(NBTTag.ITEM_RTOKENS.getRef());

		if(!jewel && safe) {
			if(!EnchantManager.canTypeApply(itemStack, applyEnchant)) {
				throw new MismatchedEnchantException();
			} else if(isJewel(itemStack) && !isJewelComplete(itemStack)) {
				throw new IsJewelException();
			} else if(applyLvl > 3) {
				throw new InvalidEnchantLevelException(true);
			} else if(currentLvl == applyLvl) {
//			throw new InvalidEnchantLevelException(false);
			} else if(applyLvl + tokenNum > 8) {
				throw new MaxTokensExceededException(false);
			} else if(applyEnchant.isRare && applyLvl + rTokenNum > 4) {
				throw new MaxTokensExceededException(true);
			} else if(enchantNum >= 3 && applyLvl != 0) {
				throw new MaxEnchantsExceededException();
			}
		}
		if(nbtItem.getString(NBTTag.ITEM_JEWEL_ENCHANT.getRef()).equals(applyEnchant.refNames.get(0))) jewel = true;
		if(jewel && (safe || applyLvl == 0)) {
			throw new IsJewelException();
		}
		if(enchantNum == 2 && safe && !isJewel(itemStack)) {
			boolean hasCommonEnchant = false;
			for(String enchantString : enchantOrder) {
				PitEnchant pitEnchant = EnchantManager.getEnchant(enchantString);
				if(pitEnchant == null) continue;
				if(pitEnchant.isUncommonEnchant) continue;
				hasCommonEnchant = true;
				break;
			}
			if(!hasCommonEnchant && applyEnchant.isUncommonEnchant) throw new NoCommonEnchantException();
		}

		if(currentLvl == 0) {
			enchantNum++;
			enchantOrder.add(applyEnchant.refNames.get(0));
		}
		if(applyLvl == 0) {
			enchantNum--;
			enchantOrder.remove(applyEnchant.refNames.get(0));
		}
		itemEnchants.setInteger(applyEnchant.refNames.get(0), applyLvl);

		tokenNum += applyLvl - currentLvl;
		if(applyEnchant.isRare && !jewel) rTokenNum += applyLvl - currentLvl;
		if(jewel) nbtItem.setString(NBTTag.ITEM_JEWEL_ENCHANT.getRef(), applyEnchant.refNames.get(0));
		nbtItem.setInteger(NBTTag.ITEM_ENCHANTS.getRef(), enchantNum);
		nbtItem.setInteger(NBTTag.ITEM_TOKENS.getRef(), tokenNum);
		nbtItem.setInteger(NBTTag.ITEM_RTOKENS.getRef(), rTokenNum);

		AItemStackBuilder itemStackBuilder = new AItemStackBuilder(nbtItem.getItem());
		itemStackBuilder.setName("&cTier " + (enchantNum != 0 ? AUtil.toRoman(enchantNum) : 0) + " " + getMysticType(itemStack));

		setItemLore(itemStackBuilder.getItemStack());
		return itemStackBuilder.getItemStack();
	}

	public static boolean isIllegalItem(ItemStack itemStack) {
		if(Misc.isAirOrNull(itemStack)) return false;
		NBTItem nbtItem = new NBTItem(itemStack);
		if(!nbtItem.hasKey(NBTTag.ITEM_UUID.getRef())) return false;

		NBTList<String> enchantOrder = nbtItem.getStringList(NBTTag.PIT_ENCHANT_ORDER.getRef());
		NBTCompound itemEnchants = nbtItem.getCompound(NBTTag.PIT_ENCHANTS.getRef());
		Integer enchantNum = nbtItem.getInteger(NBTTag.ITEM_ENCHANTS.getRef());
		Integer tokenNum = nbtItem.getInteger(NBTTag.ITEM_TOKENS.getRef());
		Integer rTokenNum = nbtItem.getInteger(NBTTag.ITEM_RTOKENS.getRef());

		if(enchantNum > 3 || tokenNum > 8 || rTokenNum > 4) return true;
		for(PitEnchant pitEnchant : EnchantManager.pitEnchants) {
			if(itemEnchants.getInteger(pitEnchant.refNames.get(0)) > 3) return true;
		}
		boolean hasCommonEnchant = false;
		for(String enchantString : enchantOrder) {
			PitEnchant pitEnchant = EnchantManager.getEnchant(enchantString);
			if(pitEnchant == null) continue;
			if(pitEnchant.isUncommonEnchant) continue;
			hasCommonEnchant = true;
			break;
		}
		return !hasCommonEnchant && enchantNum == 3 && !isJewel(itemStack);
	}

	public static void setItemLore(ItemStack itemStack) {

		NBTItem nbtItem = new NBTItem(itemStack);
		NBTList<String> enchantOrder = nbtItem.getStringList(NBTTag.PIT_ENCHANT_ORDER.getRef());
		NBTCompound itemEnchants = nbtItem.getCompound(NBTTag.PIT_ENCHANTS.getRef());
		int playerKills = nbtItem.getInteger(NBTTag.PLAYER_KILLS.getRef());
		int botKills = nbtItem.getInteger(NBTTag.BOT_KILLS.getRef());
		int jewelKills = nbtItem.getInteger(NBTTag.JEWEL_KILLS.getRef());
		boolean isJewel = isJewel(itemStack);

		ALoreBuilder loreBuilder = new ALoreBuilder();

		loreBuilder.addLore("&7Kills: &a" + Misc.getFormattedKills(playerKills) + "&7/" + Misc.getFormattedKills(botKills));
		if(isJewel && !isJewelComplete(itemStack)) {

			loreBuilder.addLore("&7");
			loreBuilder.addLore("&7Kill &c" + Constant.JEWEL_KILLS + " &7players to recycle");
			loreBuilder.addLore("&7into Tier 1 pants with a Tier III");
			loreBuilder.addLore("&7enchant");
			loreBuilder.addLore("&7Kills: &3" + jewelKills);
		} else {

			for(String key : enchantOrder) {

				PitEnchant enchant = EnchantManager.getEnchant(key);
				Integer enchantLvl = itemEnchants.getInteger(key);
				assert enchant != null;
				loreBuilder.addLore("&f");
				loreBuilder.addLore(enchant.getDisplayName() + enchantLevelToRoman(enchantLvl));
				loreBuilder.addLore(enchant.getDescription(enchantLvl));
			}
			if(isJewel) {
				PitEnchant jewelEnchant = getEnchant(nbtItem.getString(NBTTag.ITEM_JEWEL_ENCHANT.getRef()));
				assert jewelEnchant != null;
				loreBuilder.addLore("&f");
				loreBuilder.addLore("&3JEWEL!&9 " + jewelEnchant.getDisplayName());
			}
		}

		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setLore(loreBuilder.getLore());
		itemStack.setItemMeta(itemMeta);
	}

	public static void setDisplayItemLore(ItemStack itemStack) {

		NBTItem nbtItem = new NBTItem(itemStack);
		NBTList<String> enchantOrder = nbtItem.getStringList(NBTTag.PIT_ENCHANT_ORDER.getRef());
		NBTCompound itemEnchants = nbtItem.getCompound(NBTTag.PIT_ENCHANTS.getRef());
		boolean isGemmed = isGemmed(itemStack);

		ALoreBuilder loreBuilder = new ALoreBuilder();
		loreBuilder.addLore("&7&m------------------");
		for(String key : enchantOrder) {

			PitEnchant enchant = EnchantManager.getEnchant(key);
			Integer enchantLvl = itemEnchants.getInteger(key);
			assert enchant != null;
			loreBuilder.addLore(enchant.getDisplayName() + enchantLevelToRoman(enchantLvl));
		}
		loreBuilder.addLore("&7&m------------------");
		if(isGemmed) loreBuilder.addLore("&aGEMMED!");

		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setLore(loreBuilder.getLore());
		itemStack.setItemMeta(itemMeta);
	}

	public static boolean isJewel(ItemStack itemStack) {

		if(Misc.isAirOrNull(itemStack)) return false;
		NBTItem nbtItem = new NBTItem(itemStack);
		return nbtItem.getBoolean(NBTTag.IS_JEWEL.getRef());
	}

	public static boolean isGemmed(ItemStack itemStack) {

		if(Misc.isAirOrNull(itemStack)) return false;
		NBTItem nbtItem = new NBTItem(itemStack);
		return nbtItem.getBoolean(NBTTag.IS_GEMMED.getRef());
	}

	public static boolean isJewelComplete(ItemStack itemStack) {

		if(!isJewel(itemStack)) return false;
		NBTItem nbtItem = new NBTItem(itemStack);
		return nbtItem.getInteger(NBTTag.JEWEL_KILLS.getRef()) >= Constant.JEWEL_KILLS;
	}

	public static ItemStack completeJewel(Player player, ItemStack itemStack) {
		if(Misc.isAirOrNull(itemStack) || !isJewel(itemStack)) return null;
		NBTItem nbtItem = new NBTItem(itemStack);
		String jewelString = nbtItem.getString(NBTTag.ITEM_JEWEL_ENCHANT.getRef());
		int jewelKills = nbtItem.getInteger(NBTTag.JEWEL_KILLS.getRef());
		if(jewelKills < Constant.JEWEL_KILLS || !jewelString.isEmpty()) return null;

		List<PitEnchant> enchantList = EnchantManager.getEnchants(MysticType.getMysticType(itemStack));
		List<PitEnchant> weightedEnchantList = new ArrayList<>();

		for(PitEnchant pitEnchant : enchantList) {

			weightedEnchantList.add(pitEnchant);
			if(pitEnchant.isRare) continue;
			weightedEnchantList.add(pitEnchant);
			weightedEnchantList.add(pitEnchant);
			if(pitEnchant.isUncommonEnchant) continue;
			weightedEnchantList.add(pitEnchant);
			weightedEnchantList.add(pitEnchant);
		}
		Collections.shuffle(weightedEnchantList);
		PitEnchant jewelEnchant = weightedEnchantList.get(0);

//		Collections.shuffle(enchantList);
//		double rand = Math.random();
//		EnchantRarity enchantRarity = EnchantRarity.COMMON;
//		if(rand < 0.1) enchantRarity = EnchantRarity.RARE; else if(rand < 0.35) enchantRarity = EnchantRarity.UNCOMMON;
//
//		PitEnchant jewelEnchant;
//		for(int i = 0;; i++) {
//
//			if(enchantRarity != enchantList.get(i).getRarity()) continue;
//			jewelEnchant = enchantList.get(i);
//			break;
//		}

		PantColor.setPantColor(nbtItem.getItem(), PantColor.getNormalRandom());
		Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes(
				'&', "&3&lJEWEL!&7 " + player.getDisplayName() + " &7found " + jewelEnchant.getDisplayName()));
		ASound.play(player, Sound.ENDERDRAGON_GROWL, 1, 1);
		try {
			return EnchantManager.addEnchant(nbtItem.getItem(), jewelEnchant, 3, false, true);
		} catch(Exception ignored) { }
		return null;
	}

	public static void incrementKills(Player attacker, Player killed) {
		Non non = NonManager.getNon(killed);
		String ref = non == null ? NBTTag.PLAYER_KILLS.getRef() : NBTTag.BOT_KILLS.getRef();

		if(!Misc.isAirOrNull(attacker.getItemInHand())) {
			ItemStack itemStack = attacker.getItemInHand();
			NBTItem nbtItem = new NBTItem(itemStack);
			nbtItem.setInteger(ref, nbtItem.getInteger(ref) + 1);

			if(isJewel(itemStack) && !isJewelComplete(itemStack))
				nbtItem.setInteger(NBTTag.JEWEL_KILLS.getRef(), nbtItem.getInteger(NBTTag.JEWEL_KILLS.getRef()) + 1);

			ItemStack jewelStack = completeJewel(attacker, nbtItem.getItem());
			if(jewelStack != null) nbtItem = new NBTItem(jewelStack);

			setItemLore(nbtItem.getItem());
			attacker.setItemInHand(nbtItem.getItem());

			for(int i = 0; i < 9; i++) {
				if(i == attacker.getInventory().getHeldItemSlot()) continue;

				ItemStack hotbarStack = attacker.getInventory().getItem(i);
				if(Misc.isAirOrNull(hotbarStack) || hotbarStack.getType() != Material.BOW) continue;
				NBTItem hotbarNbtItem = new NBTItem(hotbarStack);

				if(isJewel(hotbarStack) && !isJewelComplete(hotbarStack))
					hotbarNbtItem.setInteger(NBTTag.JEWEL_KILLS.getRef(), hotbarNbtItem.getInteger(NBTTag.JEWEL_KILLS.getRef()) + 1);

				ItemStack hotbarJewelStack = completeJewel(attacker, hotbarNbtItem.getItem());
				if(hotbarJewelStack != null) hotbarNbtItem = new NBTItem(hotbarJewelStack);

				setItemLore(hotbarNbtItem.getItem());
				attacker.getInventory().setItem(i, hotbarNbtItem.getItem());
			}
		}
		if(!Misc.isAirOrNull(attacker.getInventory().getLeggings())) {
			ItemStack itemStack = attacker.getInventory().getLeggings();
			NBTItem nbtItem = new NBTItem(itemStack);
			nbtItem.setInteger(ref, nbtItem.getInteger(ref) + 1);

			if(isJewel(itemStack) && !isJewelComplete(itemStack))
				nbtItem.setInteger(NBTTag.JEWEL_KILLS.getRef(), nbtItem.getInteger(NBTTag.JEWEL_KILLS.getRef()) + 1);

			ItemStack jewelStack = completeJewel(attacker, nbtItem.getItem());
			if(jewelStack != null) nbtItem = new NBTItem(jewelStack);

			setItemLore(nbtItem.getItem());
			attacker.getInventory().setLeggings(nbtItem.getItem());
		}
	}

	public static PitEnchant getEnchant(String refName) {

		if(refName.equals("")) return null;
		for(PitEnchant enchant : pitEnchants) {

			if(!enchant.refNames.contains(refName)) continue;
			return enchant;
		}
		return null;
	}

	public static String enchantLevelToRoman(int enchantLvl) {

		return enchantLvl <= 1 ? "" : " " + AUtil.toRoman(enchantLvl);
	}
	
	public static int getEnchantLevel(Player player, PitEnchant pitEnchant) {

		List<ItemStack> inUse = new ArrayList<>(Arrays.asList(player.getInventory().getArmorContents()));
		inUse.add(player.getItemInHand());

		int finalLevel = 0;
		for(ItemStack itemStack : inUse) {
			int enchantLvl = getEnchantLevel(itemStack, pitEnchant);
			if(pitEnchant.levelStacks) {
				finalLevel += enchantLvl;
			} else {
				if(enchantLvl > finalLevel) finalLevel = enchantLvl;
			}
		}

		return finalLevel;
	}

	public static int getEnchantLevel(ItemStack itemStack, PitEnchant pitEnchant) {

		if(itemStack == null || itemStack.getType() == Material.AIR) return 0;
		NBTItem nbtItem = new NBTItem(itemStack);
		if(!nbtItem.hasKey(NBTTag.ITEM_UUID.getRef())) return 0;

		Map<PitEnchant, Integer> itemEnchantMap = getEnchantsOnItem(itemStack);
		return getEnchantLevel(itemEnchantMap, pitEnchant);
	}

	public static int getEnchantLevel(Map<PitEnchant, Integer> enchantMap, PitEnchant pitEnchant) {

		for(Map.Entry<PitEnchant, Integer> entry : enchantMap.entrySet()) {

			if(entry.getKey() != pitEnchant) continue;
			return entry.getValue();
		}

		return 0;
	}

	public static Map<PitEnchant, Integer> getEnchantsOnPlayer(Player player) {

		List<ItemStack> inUse = new ArrayList<>(Arrays.asList(player.getInventory().getArmorContents()));
		inUse.add(player.getItemInHand());

		return getEnchantsOnPlayer(inUse.toArray(new ItemStack[5]));
	}

	public static Map<PitEnchant, Integer> getEnchantsOnPlayer(ItemStack[] inUseArr) {

		Map<PitEnchant, Integer> itemEnchantMap = new HashMap<>();
		for(ItemStack itemStack : inUseArr) {
			if(itemStack == null || itemStack.getType() == Material.AIR) continue;
			itemEnchantMap.putAll(getEnchantsOnItem(itemStack, itemEnchantMap));
		}

		return itemEnchantMap;
	}

	public static Map<PitEnchant, Integer> getEnchantsOnItem(ItemStack itemStack) {
		return getEnchantsOnItem(itemStack, new HashMap<>());
	}

	@org.jetbrains.annotations.NotNull
	public static Map<PitEnchant, Integer> getEnchantsOnItem(ItemStack itemStack, @NotNull Map<PitEnchant, Integer> currentEnchantMap) {

		Map<PitEnchant, Integer> itemEnchantMap = new HashMap<>();
		if(itemStack == null || itemStack.getType() == Material.AIR) return itemEnchantMap;
		NBTItem nbtItem = new NBTItem(itemStack);
		if(!nbtItem.hasKey(NBTTag.ITEM_UUID.getRef())) return itemEnchantMap;

		NBTCompound itemEnchants = nbtItem.getCompound(NBTTag.PIT_ENCHANTS.getRef());
		Set<String> keys = itemEnchants.getKeys();
		for(String key : keys) {

			PitEnchant pitEnchant = getEnchant(key);
			Integer enchantLvl = itemEnchants.getInteger(key);
			if(pitEnchant == null || enchantLvl == 0) continue;

			if(currentEnchantMap.containsKey(pitEnchant) && currentEnchantMap.get(pitEnchant) >= enchantLvl) continue;

			itemEnchantMap.put(pitEnchant, enchantLvl);

//			if(currentEnchantMap.containsKey(pitEnchant) && currentEnchantMap.get(pitEnchant) >= enchantLvl && !pitEnchant.levelStacks) continue;
//			if(currentEnchantMap.containsKey(pitEnchant) && !pitEnchant.levelStacks) itemEnchantMap.put(pitEnchant, enchantLvl);
//			else itemEnchantMap.put(pitEnchant, (itemEnchantMap.get(pitEnchant) != null ? itemEnchantMap.get(pitEnchant) : 0) + enchantLvl);
		}

		return itemEnchantMap;
	}

	public static List<PitEnchant> getEnchants(ApplyType applyType) {

		List<PitEnchant> applicableEnchants = new ArrayList<>();
		if(applyType == ApplyType.ALL) return pitEnchants;

		for(PitEnchant pitEnchant : pitEnchants) {
			ApplyType enchantApplyType = pitEnchant.applyType;
			if(enchantApplyType == ApplyType.ALL) applicableEnchants.add(pitEnchant);

			switch(applyType) {
				case BOWS:
					if(enchantApplyType == ApplyType.BOWS || enchantApplyType == ApplyType.WEAPONS) applicableEnchants.add(pitEnchant);
					break;
				case PANTS:
					if(enchantApplyType == ApplyType.PANTS) applicableEnchants.add(pitEnchant);
					break;
				case SWORDS:
					if(enchantApplyType == ApplyType.SWORDS || enchantApplyType == ApplyType.WEAPONS) applicableEnchants.add(pitEnchant);
					break;
				case WEAPONS:
					if(enchantApplyType == ApplyType.WEAPONS || enchantApplyType == ApplyType.BOWS
							|| enchantApplyType == ApplyType.SWORDS) applicableEnchants.add(pitEnchant);
					break;
			}
		}
		return applicableEnchants;
	}

	public static List<PitEnchant> getEnchants(MysticType mystictype) {

		List<PitEnchant> applicableEnchants = new ArrayList<>();

		for(PitEnchant pitEnchant : pitEnchants) {
			ApplyType enchantApplyType = pitEnchant.applyType;
			if(enchantApplyType == ApplyType.ALL) applicableEnchants.add(pitEnchant);

			switch(mystictype) {
				case BOW:
					if(enchantApplyType == ApplyType.BOWS || enchantApplyType == ApplyType.WEAPONS) applicableEnchants.add(pitEnchant);
					break;
				case PANTS:
					if(enchantApplyType == ApplyType.PANTS) applicableEnchants.add(pitEnchant);
					break;
				case SWORD:
					if(enchantApplyType == ApplyType.SWORDS || enchantApplyType == ApplyType.WEAPONS) applicableEnchants.add(pitEnchant);
					break;
			}
		}
		return applicableEnchants;
	}
}
