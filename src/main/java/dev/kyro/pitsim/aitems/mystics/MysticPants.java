package dev.kyro.pitsim.aitems.mystics;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.aitems.StaticPitItem;
import dev.kyro.pitsim.aitems.TemporaryItem;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.AuctionCategory;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.enums.PantColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MysticPants extends StaticPitItem implements TemporaryItem {

	public MysticPants() {
		hasUUID = true;
		hideExtra = true;
		unbreakable = true;
		isMystic = true;
		auctionCategory = AuctionCategory.OVERWORLD_GEAR;
	}

	@Override
	public String getNBTID() {
		return "mystic-pants";
	}

	@Override
	public List<String> getRefNames() {
		return new ArrayList<>(Arrays.asList("pants", "mysticpants"));
	}

	@Override
	public Material getMaterial() {
		return Material.LEATHER_LEGGINGS;
	}

	public String getName(PantColor pantColor) {
		return pantColor.chatColor + "Fresh " + pantColor.displayName + " Pants";
	}

	@Override
	public String getName() {
		throw new RuntimeException();
	}

	public List<String> getLore(PantColor pantColor) {
		return new ALoreBuilder(
				pantColor.chatColor + "Used in the mystic well.",
				pantColor.chatColor + "Also a fashion statement",
				"",
				"&7Kept on death"
		).getLore();
	}

	@Override
	public List<String> getLore() {
		throw new RuntimeException();
	}

	@Override
	public void updateItem(ItemStack itemStack) {
		if(!isThisItem(itemStack)) throw new RuntimeException();

		NBTItem nbtItem = new NBTItem(itemStack);
		Integer enchantNum = nbtItem.getInteger(NBTTag.ITEM_ENCHANT_NUM.getRef());
		if(enchantNum == 0) {
			PantColor pantColor = PantColor.getPantColor(itemStack);
			assert pantColor != null;
			new AItemStackBuilder(itemStack)
					.setName(getName(pantColor))
					.setLore(getLore(pantColor));
			return;
		}
		EnchantManager.setItemLore(itemStack, null);
	}

	public ItemStack getItem(PantColor pantColor) {
		ItemStack itemStack = new AItemStackBuilder(Material.LEATHER_LEGGINGS)
				.setName(getName(pantColor))
				.setLore(new ALoreBuilder(getLore(pantColor)))
				.getItemStack();
		itemStack = buildItem(itemStack);
		itemStack = PantColor.setPantColor(itemStack, pantColor);

		NBTItem nbtItem = new NBTItem(itemStack);
		nbtItem.addCompound(NBTTag.MYSTIC_ENCHANTS.getRef());
		return nbtItem.getItem();
	}

	@Override
	public ItemStack getItem() {
		return getItem(PantColor.getNormalRandom());
	}

	@Override
	public ItemStack getItem(int amount) {
		if(amount == 1) return getItem();
		throw new RuntimeException();
	}

	@Override
	public ItemStack getReplacementItem(PitPlayer pitPlayer, ItemStack itemStack, NBTItem nbtItem) {
		ItemStack newItemStack = new ItemStack(getMaterial(), 1);
		newItemStack = buildItem(newItemStack);
		NBTItem newNBTItem = new NBTItem(newItemStack);

		newNBTItem.addCompound(NBTTag.MYSTIC_ENCHANTS.getRef());
		NBTCompound newItemEnchants = newNBTItem.getCompound(NBTTag.MYSTIC_ENCHANTS.getRef());
		NBTCompound itemEnchants = nbtItem.getCompound(NBTTag.MYSTIC_ENCHANTS.getRef());
		for(String enchantKey : itemEnchants.getKeys()) {
			int enchantLvl = itemEnchants.getInteger(enchantKey);
			if(enchantLvl == 0) continue;
			newItemEnchants.setInteger(enchantKey, enchantLvl);
		}

		if(nbtItem.hasKey(NBTTag.ITEM_ENCHANT_NUM.getRef()))
			newNBTItem.setInteger(NBTTag.ITEM_ENCHANT_NUM.getRef(), nbtItem.getInteger(NBTTag.ITEM_ENCHANT_NUM.getRef()));
		if(nbtItem.hasKey(NBTTag.ITEM_TOKENS.getRef()))
			newNBTItem.setInteger(NBTTag.ITEM_TOKENS.getRef(), nbtItem.getInteger(NBTTag.ITEM_TOKENS.getRef()));
		if(nbtItem.hasKey(NBTTag.ITEM_RTOKENS.getRef()))
			newNBTItem.setInteger(NBTTag.ITEM_RTOKENS.getRef(), nbtItem.getInteger(NBTTag.ITEM_RTOKENS.getRef()));
		if(nbtItem.hasKey(NBTTag.CURRENT_LIVES.getRef()))
			newNBTItem.setInteger(NBTTag.CURRENT_LIVES.getRef(), nbtItem.getInteger(NBTTag.CURRENT_LIVES.getRef()));
		if(nbtItem.hasKey(NBTTag.MAX_LIVES.getRef()))
			newNBTItem.setInteger(NBTTag.MAX_LIVES.getRef(), nbtItem.getInteger(NBTTag.MAX_LIVES.getRef()));
		if(nbtItem.hasKey(NBTTag.JEWEL_KILLS.getRef()))
			newNBTItem.setInteger(NBTTag.JEWEL_KILLS.getRef(), nbtItem.getInteger(NBTTag.JEWEL_KILLS.getRef()));

		if(nbtItem.hasKey(NBTTag.MYSTIC_ENCHANT_ORDER.getRef()))
			newNBTItem.getStringList(NBTTag.MYSTIC_ENCHANT_ORDER.getRef()).addAll(nbtItem.getStringList(NBTTag.MYSTIC_ENCHANT_ORDER.getRef()));
		if(nbtItem.hasKey(NBTTag.ITEM_JEWEL_ENCHANT.getRef()))
			newNBTItem.setString(NBTTag.ITEM_JEWEL_ENCHANT.getRef(), nbtItem.getString(NBTTag.ITEM_JEWEL_ENCHANT.getRef()));

		if(nbtItem.hasKey(NBTTag.IS_GEMMED.getRef()))
			newNBTItem.setBoolean(NBTTag.IS_GEMMED.getRef(), nbtItem.getBoolean(NBTTag.IS_GEMMED.getRef()));
		if(nbtItem.hasKey(NBTTag.IS_VENOM.getRef()))
			newNBTItem.setBoolean(NBTTag.IS_VENOM.getRef(), nbtItem.getBoolean(NBTTag.IS_VENOM.getRef()));
		if(nbtItem.hasKey(NBTTag.IS_JEWEL.getRef()))
			newNBTItem.setBoolean(NBTTag.IS_JEWEL.getRef(), nbtItem.getBoolean(NBTTag.IS_JEWEL.getRef()));
		newItemStack = newNBTItem.getItem();

		new AItemStackBuilder(newItemStack)
				.setName(itemStack.getItemMeta().getDisplayName());

		return newItemStack;
	}

	@Override
	public boolean isLegacyItem(ItemStack itemStack, NBTItem nbtItem) {
		return nbtItem.hasKey(NBTTag.ITEM_UUID.getRef()) && itemStack.getType() == Material.LEATHER_LEGGINGS;
	}

	@Override
	public TemporaryType getTemporaryType() {
		return TemporaryType.LOOSES_LIVES_ON_DEATH;
	}
}
