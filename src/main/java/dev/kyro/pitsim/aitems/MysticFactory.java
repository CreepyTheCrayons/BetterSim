package dev.kyro.pitsim.aitems;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.aitems.mystics.*;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.enums.PantColor;
import dev.kyro.pitsim.misc.Constant;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MysticFactory {
	public static ItemStack getFreshItem(Player player, String type) {
		type = type.toLowerCase();
		PantColor pantColor = PantColor.getPantColor(type);
		if(type.equals("sword")) {
			return getFreshItem(MysticType.SWORD, null);
		} else if(type.equals("bow")) {
			return getFreshItem(MysticType.BOW, null);
		} else if(type.equals("chestplate")) {
			return getFreshItem(MysticType.TAINTED_CHESTPLATE, null);
		} else if(type.equals("scythe") || type.equals("sythe") || type.equals("scyth")) {
			return getFreshItem(MysticType.TAINTED_SCYTHE, null);
		} else if(pantColor != null && pantColor.isDefault) {
			return getFreshItem(MysticType.PANTS, pantColor);
		}

		return null;
	}

	public static ItemStack getFreshItem(MysticType type, PantColor pantColor) {
		return createMystic(type, pantColor, false);
	}

	public static ItemStack getJewelItem(MysticType type) {
		return createMystic(type, PantColor.JEWEL, true);
	}

	private static ItemStack createMystic(MysticType type, PantColor pantColor, boolean isJewel) {
		ItemStack mysticStack;
		if(type == MysticType.SWORD) {
			mysticStack = ItemFactory.getItem(MysticSword.class).getItem();
		} else if(type == MysticType.BOW) {
			mysticStack = ItemFactory.getItem(MysticBow.class).getItem();
		} else if(type == MysticType.TAINTED_SCYTHE) {
			mysticStack = ItemFactory.getItem(TaintedScythe.class).getItem();
		} else if(type == MysticType.TAINTED_CHESTPLATE) {
			mysticStack = ItemFactory.getItem(TaintedChestplate.class).getItem();
		} else {
			mysticStack = ItemFactory.getItem(MysticPants.class).getItem(pantColor);
		}

		if(isJewel) {
			NBTItem nbtItem = new NBTItem(mysticStack);
			nbtItem.setBoolean(NBTTag.IS_JEWEL.getRef(), true);
			mysticStack = nbtItem.getItem();

			PitItem pitItem = ItemFactory.getItem(mysticStack);
			assert pitItem != null;
			pitItem.updateItem(mysticStack);
		}
		return mysticStack;
	}

	public static boolean isMystic(ItemStack itemStack) {
		PitItem pitItem = ItemFactory.getItem(itemStack);
		return pitItem != null && pitItem.isMystic;
	}

	public static boolean isFresh(ItemStack itemStack) {
		if(!isMystic(itemStack)) return false;

		NBTItem nbtItem = new NBTItem(itemStack);
		int enchantNum = nbtItem.getInteger(NBTTag.ITEM_ENCHANT_NUM.getRef());
		return enchantNum == 0;
	}

	public static boolean hasLives(ItemStack itemStack) {
		if(!isMystic(itemStack)) return false;
		NBTItem nbtItem = new NBTItem(itemStack);
		return nbtItem.hasKey(NBTTag.CURRENT_LIVES.getRef());
	}

	public static boolean isJewel(ItemStack itemStack, boolean onlyComplete) {
		if(!isMystic(itemStack)) return false;

		NBTItem nbtItem = new NBTItem(itemStack);
		if(onlyComplete && nbtItem.getInteger(NBTTag.ITEM_JEWEL_KILLS.getRef()) < Constant.JEWEL_KILLS) return false;
		return nbtItem.hasKey(NBTTag.IS_JEWEL.getRef());
	}

	public static boolean isImportant(ItemStack itemStack) {
		if(!isMystic(itemStack)) return false;
		PitItem pitItem = ItemFactory.getItem(itemStack);

		return isJewel(itemStack, false) || pitItem instanceof TaintedScythe || pitItem instanceof TaintedChestplate;
	}

	public static boolean isGemmed(ItemStack itemStack) {
		if(!isMystic(itemStack)) return false;

		NBTItem nbtItem = new NBTItem(itemStack);
		return nbtItem.hasKey(NBTTag.IS_GEMMED.getRef());
	}

	public static boolean isBroken(ItemStack itemStack) {
		if(!isMystic(itemStack) || !hasLives(itemStack)) return false;
		PitItem pitItem = ItemFactory.getItem(itemStack);
		assert pitItem != null;
		TemporaryItem temporaryItem = (TemporaryItem) pitItem;
		return temporaryItem.getLives(itemStack) == 0;
	}
}
