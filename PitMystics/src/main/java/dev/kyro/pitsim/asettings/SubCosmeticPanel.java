package dev.kyro.pitsim.asettings;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.arcticapi.gui.AGUIPanel;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.RedstoneColor;
import dev.kyro.pitsim.acosmetics.ColorableCosmetic;
import dev.kyro.pitsim.acosmetics.CosmeticManager;
import dev.kyro.pitsim.acosmetics.CosmeticType;
import dev.kyro.pitsim.acosmetics.PitCosmetic;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SubCosmeticPanel extends AGUIPanel {
	public SettingsGUI settingsGUI;

	public static ItemStack previousPageItem;
	public static ItemStack nextPageItem;
	public static ItemStack backItem;

	public CosmeticType cosmeticType;
	public int slot;

	public int page;
	public static List<Integer> cosmeticSlots = new ArrayList<>();
	public Map<Integer, PitCosmetic> cosmeticMap = new HashMap<>();

	static {
		previousPageItem = new AItemStackBuilder(Material.PAPER)
				.setName("&f&lPrevious Page")
				.setLore(new ALoreBuilder(
						"&7Click to view the previous page"
				))
				.getItemStack();

		nextPageItem = new AItemStackBuilder(Material.PAPER)
				.setName("&f&lNext Page")
				.setLore(new ALoreBuilder(
						"&7Click to view the next page"
				))
				.getItemStack();

		backItem = new AItemStackBuilder(Material.BARRIER)
				.setName("&c&lBack")
				.setLore(new ALoreBuilder(
						"&7Click to go to the previous screen"
				))
				.getItemStack();

		for(int i = 9; i < 45; i++) {
			if(i % 9 == 0 || (i + 1) % 9 == 0) continue;
			cosmeticSlots.add(i);
		}
	}

	public SubCosmeticPanel(AGUI gui, CosmeticType cosmeticType) {
		super(gui, true);
		settingsGUI = (SettingsGUI) gui;
		this.cosmeticType = cosmeticType;
		this.slot = cosmeticType.getSettingsGUISlot();
		buildInventory();

		inventoryBuilder.createBorder(Material.STAINED_GLASS_PANE, 7);

		getInventory().setItem(getRows() * 9 - 5, backItem);
		if(getPages() != 1) {
			getInventory().setItem(getRows() * 9 - 9, previousPageItem);
			getInventory().setItem(getRows() * 9 - 1, nextPageItem);
		}

		for(int i = 10; i < settingsGUI.getRows(this) * 9 - 9; i++) {
			if(i % 9 == 0 || (i + 1) % 9 == 0) continue;
			cosmeticMap.put(i, null);
		}

		setPage(1);
	}

	public void deselectCosmetic(CosmeticType cosmeticType) {
		PitPlayer.EquippedCosmeticData cosmeticData = settingsGUI.pitPlayer.equippedCosmeticMap.get(cosmeticType.name());
		if(cosmeticData != null) {
			PitCosmetic pitCosmetic = CosmeticManager.getCosmetic(cosmeticData.refName);
			if(pitCosmetic != null) pitCosmetic.onDisable(settingsGUI.pitPlayer);
		}
		settingsGUI.pitPlayer.equippedCosmeticMap.put(cosmeticType.name(), null);
	}

	public boolean selectCosmetic(PitCosmetic pitCosmetic) {
		return selectCosmetic(pitCosmetic, null);
	}

	public boolean selectCosmetic(PitCosmetic pitCosmetic, RedstoneColor redstoneColor) {
		if(settingsGUI.pitPlayer.equippedCosmeticMap.containsKey(cosmeticType.name())) {
			PitPlayer.EquippedCosmeticData cosmeticData = settingsGUI.pitPlayer.equippedCosmeticMap.get(cosmeticType.name());
			if(cosmeticData != null) {
				if(!pitCosmetic.isColorCosmetic) {
					if(pitCosmetic.refName.equals(cosmeticData.refName)) {
						Sounds.NO.play(player);
						return false;
					}
					deselectCosmetic(pitCosmetic.cosmeticType);
				} else {
					if(pitCosmetic.refName.equals(cosmeticData.refName)) {
						if(redstoneColor == cosmeticData.redstoneColor) {
							Sounds.NO.play(player);
							return false;
						}
						ColorableCosmetic colorableCosmetic = (ColorableCosmetic) pitCosmetic;
						colorableCosmetic.setRedstoneColor(player, redstoneColor);
					}
				}
			}
		}
		settingsGUI.pitPlayer.equippedCosmeticMap.put(cosmeticType.name(), new PitPlayer.EquippedCosmeticData(pitCosmetic.refName, redstoneColor));
		return true;
	}

	public void setPage(int page) {
		this.page = page;

		List<PitCosmetic> unlockedCosmetics = CosmeticManager.getUnlockedCosmetics(settingsGUI.pitPlayer, cosmeticType);
		int cosmeticListSize = unlockedCosmetics.size();
		for(int i = cosmeticSlots.size() * (page - 1); i < cosmeticSlots.size() * page; i++) {
			int slot = cosmeticSlots.get(i % cosmeticSlots.size());
			if(i >= cosmeticListSize) {
				cosmeticMap.put(slot, null);
				continue;
			}

			PitCosmetic pitCosmetic = unlockedCosmetics.get(i);
			cosmeticMap.put(slot, pitCosmetic);
//			TODO: Add text if color, add enchant glint if equipped
			getInventory().setItem(slot, pitCosmetic.getDisplayItem(pitCosmetic.isEquipped(settingsGUI.pitPlayer)));
		}
	}

	@Override
	public String getName() {
		return cosmeticType.getPanelName();
	}

	@Override
	public int getRows() {
		SettingsGUI settingsGUI = (SettingsGUI) gui;
		return settingsGUI.getRows(this);
	}

	public int getPages() {
		return settingsGUI.getPages(this);
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		if(event.getClickedInventory().getHolder() != this) return;
		int slot = event.getSlot();

		if(cosmeticMap.get(slot) != null) {
			PitCosmetic pitCosmetic = cosmeticMap.get(slot);
			if(pitCosmetic.isColorCosmetic) {
				openPanel(new ColorCosmeticPanel(gui, this, pitCosmetic));
			} else {
				boolean success = selectCosmetic(pitCosmetic);
				if(success) {
					player.closeInventory();
					Sounds.SUCCESS.play(player);
					AOutput.send(player, "&7Equipped your " + pitCosmetic.getDisplayName());
				}
			}
		} else if(slot == getRows() * 9 - 9) {
			if(page > 1) {
				setPage(page - 1);
			} else {
				Sounds.NO.play(player);
			}
		} else if(slot == getRows() * 9 - 5) {
			openPreviousGUI();
		} else if(slot == getRows() * 9 - 1) {
			if(page < settingsGUI.getPages(this)) {
				setPage(page + 1);
			} else {
				Sounds.NO.play(player);
			}
		}
//		TODO: Remove button
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {}

	@Override
	public void onClose(InventoryCloseEvent event) {}
}
