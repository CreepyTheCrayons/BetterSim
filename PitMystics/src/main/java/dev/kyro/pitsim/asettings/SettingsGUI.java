package dev.kyro.pitsim.asettings;

import dev.kyro.arcticapi.gui.AGUI;
import dev.kyro.pitsim.acosmetics.CosmeticManager;
import dev.kyro.pitsim.acosmetics.CosmeticType;
import dev.kyro.pitsim.acosmetics.PitCosmetic;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsGUI extends AGUI {
	public PitPlayer pitPlayer;

	public SettingsPanel settingsPanel;
	public CosmeticPanel cosmeticPanel;
	public ParticlesPanel particlesPanel;
	public Map<CosmeticType, SubCosmeticPanel> cosmeticPanelMap = new HashMap<>();

	public SettingsGUI(Player player) {
		super(player);
		this.pitPlayer = PitPlayer.getPitPlayer(player);

		settingsPanel = new SettingsPanel(this);
		cosmeticPanel = new CosmeticPanel(this);
		particlesPanel = new ParticlesPanel(this);

		Class<? extends SubCosmeticPanel>[] constructorParameters = new Class[] {AGUI.class};
		for(CosmeticType cosmeticType : CosmeticType.values()) {
			Class<? extends SubCosmeticPanel> clazz = cosmeticType.panelClazz;
			if(clazz == null) continue;
			SubCosmeticPanel panel;
			try {
				Constructor<? extends SubCosmeticPanel> constructor = clazz.getConstructor(constructorParameters);
				panel = constructor.newInstance(this);
			} catch(Exception exception) {
				exception.printStackTrace();
				continue;
			}
			cosmeticPanelMap.put(cosmeticType, panel);
		}

		setHomePanel(settingsPanel);
	}

//	For cosmetic panel
	public int getPages(SubCosmeticPanel panel) {
		return (getItemsToDisplay(panel) - 1) / SubCosmeticPanel.cosmeticSlots.size() + 1;
	}

//	For cosmetic panel
	public int getRows(SubCosmeticPanel panel) {
		return (getItemsToDisplay(panel) - 1) / 7 + 3;
	}

//	For cosmetic panel
	public int getItemsToDisplay(SubCosmeticPanel panel) {
		List<PitCosmetic> unlockedCosmetics = CosmeticManager.getUnlockedCosmetics(pitPlayer, panel.cosmeticType);
		return Math.min(unlockedCosmetics.size() - SubCosmeticPanel.cosmeticSlots.size() * (panel.page - 1), SubCosmeticPanel.cosmeticSlots.size());
	}
}
