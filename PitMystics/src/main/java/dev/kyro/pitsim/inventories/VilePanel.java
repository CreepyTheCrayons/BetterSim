package dev.kyro.pitsim.inventories;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VilePanel extends AGUIPanel {

	public Map<Integer, Integer> slots = new HashMap<>();
	public VileGUI vileGUI;

	public VilePanel(AGUI gui) {
		super(gui);
		vileGUI = (VileGUI) gui;

	}

	@Override
	public String getName() {
		return "Choose an item to repair";
	}

	@Override
	public int getRows() {
		return 4;
	}

	@Override
	public void onClick(InventoryClickEvent event) {

		if(Misc.isAirOrNull(event.getCurrentItem())) {
			player.closeInventory();
			return;
		}

		ItemStack itemStack = player.getItemInHand();
		if(Misc.isAirOrNull(itemStack)) {
			player.closeInventory();
			return;
		}

		NBTItem held = new NBTItem(player.getItemInHand());
		if(!held.hasKey(NBTTag.IS_VILE.getRef())) {
			player.closeInventory();
			return;
		}

		int slot = event.getSlot();
		if(event.getClickedInventory().getHolder() == this) {

			if(!slots.containsKey(slot)) return;
			int invSlot = slots.get(slot);

			for(int i = 0; i < player.getInventory().getSize(); i++) {
				if(i == invSlot) {
					NBTItem nbtItem = new NBTItem(player.getInventory().getItem(i));
					nbtItem.setInteger(NBTTag.CURRENT_LIVES.getRef(), nbtItem.getInteger(NBTTag.CURRENT_LIVES.getRef()) + 1);
					EnchantManager.setItemLore(nbtItem.getItem(), player);
					player.getInventory().setItem(i, nbtItem.getItem());
					player.closeInventory();

					TextComponent message = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&5WITHERCRAFT! &7Repaired "));
					message.addExtra(Misc.createItemHover(nbtItem.getItem()));
					message.addExtra(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&7!")));
					player.sendMessage(message);

					Sounds.WITHERCRAFT_1.play(player);
					Sounds.WITHERCRAFT_2.play(player);

					int itemsToRemove = 1;
					for(int j = 0; j < player.getInventory().getContents().length; j++) {
						if(!Misc.isAirOrNull(player.getInventory().getItem(j))) {
							NBTItem nbtItem2 = new NBTItem(player.getInventory().getItem(j));
							if(nbtItem2.hasKey(NBTTag.IS_VILE.getRef())) {
								int preAmount = player.getInventory().getItem(j).getAmount();
								int newAmount = Math.max(0, preAmount - itemsToRemove);
								itemsToRemove = Math.max(0, itemsToRemove - preAmount);
								nbtItem2.getItem().setAmount(newAmount);
								player.getInventory().setItem(j, nbtItem2.getItem());
								if(itemsToRemove == 0) {
									break;
								}
							}
						}
					}
				}
			}
		}
		updateInventory();
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		int slot = 0;

		for(int i = 0; i < player.getInventory().getSize(); i++) {
			ItemStack item = player.getInventory().getItem(i);

			if(Misc.isAirOrNull(item)) continue;

			NBTItem nbtItem = new NBTItem(item);
			if(nbtItem.hasKey(NBTTag.ITEM_JEWEL_ENCHANT.getRef())) {
				if(nbtItem.getInteger(NBTTag.CURRENT_LIVES.getRef()).equals(nbtItem.getInteger(NBTTag.MAX_LIVES.getRef())))
					continue;
				ItemMeta meta = nbtItem.getItem().getItemMeta();
				List<String> lore = meta.getLore();
				lore.add("");
				lore.add(ChatColor.YELLOW + "Click to repair!");
				meta.setLore(lore);
				nbtItem.getItem().setItemMeta(meta);
				getInventory().setItem(slot, nbtItem.getItem());
				slots.put(slot, i);
				slot++;
			}

		}
	}

	@Override
	public void onClose(InventoryCloseEvent event) {
	}

}
