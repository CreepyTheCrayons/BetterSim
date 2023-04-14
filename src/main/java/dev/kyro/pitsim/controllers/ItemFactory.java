package dev.kyro.pitsim.controllers;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemFactory {
	public static List<PitItem> pitItems = new ArrayList<>();

	public static void registerItem(PitItem pitItem) {
		pitItems.add(pitItem);
	}

	@SuppressWarnings("unchecked")
	public static <T extends PitItem> T getItem(Class<T> clazz) {
		for(PitItem pitItem : pitItems) if(pitItem.getClass() == clazz) return (T) pitItem;
		throw new RuntimeException();
	}

	public static boolean isPitItem(ItemStack itemStack) {
		return getItem(itemStack) != null;
	}

	public static PitItem getItem(ItemStack itemStack) {
		if(Misc.isAirOrNull(itemStack)) return null;
		NBTItem nbtItem = new NBTItem(itemStack);
		if(!nbtItem.hasKey(NBTTag.CUSTOM_ITEM.getRef())) return null;
		String itemTag = nbtItem.getString(NBTTag.CUSTOM_ITEM.getRef());
		for(PitItem pitItem : pitItems) if(pitItem.getNBTID().equals(itemTag)) return pitItem;
		return null;
	}

	public static boolean isThisItem(ItemStack itemStack, Class<? extends PitItem> clazz) {
		PitItem pitItem = getItem(itemStack);
		if(pitItem == null) return false;
		return pitItem.getClass() == clazz;
	}


	public static void setTutorialItem(ItemStack itemStack, boolean tutorialItem) {
		if(Misc.isAirOrNull(itemStack)) throw new RuntimeException("ItemStack cannot be null or air");
		NBTItem nbtItem = new NBTItem(itemStack, true);
		if(tutorialItem) nbtItem.setBoolean(NBTTag.IS_TUTORIAL_ITEM.getRef(), true);
		else nbtItem.removeKey(NBTTag.IS_TUTORIAL_ITEM.getRef());
		EnchantManager.setItemLore(itemStack, null);
	}

	public static boolean isTutorialItem(ItemStack itemStack) {
		if(getItem(itemStack) == null) return false;
		NBTItem nbtItem = new NBTItem(itemStack);
		return nbtItem.hasKey(NBTTag.IS_TUTORIAL_ITEM.getRef());
	}
}
