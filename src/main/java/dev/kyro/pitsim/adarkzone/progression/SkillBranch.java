package dev.kyro.pitsim.adarkzone.progression;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.adarkzone.notdarkzone.UnlockState;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.UIColor;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class SkillBranch implements Listener {
	public static List<Integer> pathRows = Arrays.asList(1, 3);

	public UIColor uiColor;

	public MajorProgressionUnlock firstUnlock;
	public MajorProgressionUnlock lastUnlock;
	public MajorProgressionUnlock firstPathUnlock;
	public MajorProgressionUnlock secondPathUnlock;
	public Path firstPath;
	public Path secondPath;

	public SkillBranch(UIColor uiColor) {
		this.uiColor = uiColor;

		this.firstUnlock = createFirstUnlock();
		this.firstUnlock.position = MajorUnlockPosition.FIRST;
		this.lastUnlock = createLastUnlock();
		this.lastUnlock.position = MajorUnlockPosition.LAST;
		this.firstPathUnlock = createFirstPathUnlock();
		this.firstPathUnlock.position = MajorUnlockPosition.FIRST_PATH;
		this.secondPathUnlock = createSecondPathUnlock();
		this.secondPathUnlock.position = MajorUnlockPosition.SECOND_PATH;
		this.firstPath = createFirstPath();
		this.secondPath = createSecondPath();

		Bukkit.getPluginManager().registerEvents(this, PitSim.INSTANCE);
	}

	public abstract String getDisplayName();
	public abstract String getRefName();
	public abstract ItemStack getBaseStack();

	public abstract MajorProgressionUnlock createFirstUnlock();
	public abstract MajorProgressionUnlock createLastUnlock();
	public abstract MajorProgressionUnlock createFirstPathUnlock();
	public abstract MajorProgressionUnlock createSecondPathUnlock();
	public abstract Path createFirstPath();
	public abstract Path createSecondPath();

//	This is for the main gui
	public ItemStack getMainDisplayStack(PitPlayer pitPlayer, MainProgressionUnlock unlock, UnlockState unlockState) {
		int cost = ProgressionManager.getUnlockCost(pitPlayer, unlock);
		ItemStack baseStack = getBaseStack();
		ALoreBuilder loreBuilder = new ALoreBuilder();
		if(getBaseStack().getItemMeta().hasLore()) loreBuilder.addLore(getBaseStack().getItemMeta().getLore()).addLore("");

		ProgressionManager.addPurchaseCostLore(loreBuilder, unlockState, pitPlayer.taintedSouls, cost, false);
		if(unlockState == UnlockState.UNLOCKED) Misc.addEnchantGlint(baseStack);

		return new AItemStackBuilder(baseStack)
				.setName(getDisplayName())
				.setLore(loreBuilder)
				.getItemStack();
	}

	public enum MajorUnlockPosition {
		FIRST,
		LAST,
		FIRST_PATH,
		SECOND_PATH
	}

	public abstract class MajorProgressionUnlock {
		public SkillBranch skillBranch;
		public MajorUnlockPosition position;

		public abstract String getDisplayName();
		public abstract String getRefName();
		public abstract ItemStack getBaseStack();
		public abstract int getCost();

		public MajorProgressionUnlock() {
			this.skillBranch = SkillBranch.this;
		}

		public ItemStack getDisplayStack(PitPlayer pitPlayer) {
			UnlockState unlockState = ProgressionManager.getUnlockState(pitPlayer, this);
			ItemStack baseStack = getBaseStack();
			ALoreBuilder loreBuilder = new ALoreBuilder();
			if(getBaseStack().getItemMeta().hasLore()) loreBuilder.addLore(getBaseStack().getItemMeta().getLore()).addLore("");

			ProgressionManager.addPurchaseCostLore(loreBuilder, unlockState, pitPlayer.taintedSouls, getCost(), true);
			if(unlockState == UnlockState.UNLOCKED) Misc.addEnchantGlint(baseStack);

			return new AItemStackBuilder(baseStack)
					.setName(unlockState.chatColor + getDisplayName())
					.setLore(loreBuilder)
					.getItemStack();
		}

		public Path getAssociatedPath() {
			if(this == firstPathUnlock) return firstPath;
			if(this == secondPathUnlock) return secondPath;
			throw new RuntimeException();
		}
	}

	public abstract class Path {
		public SkillBranch skillBranch;

		public List<EffectData> effectData = new ArrayList<>();

		public Path() {
			this.skillBranch = SkillBranch.this;
			addEffects();
		}

		public abstract String getDisplayName();
		public abstract String getRefName();
		public abstract int getCost(int level);
		public abstract void addEffects();

		public void addEffect(EffectData data) {
			effectData.add(data);
		}

		public ItemStack getDisplayItem(PitPlayer pitPlayer, int level) {
			UnlockState unlockState = ProgressionManager.getUnlockState(pitPlayer, this, level);
			ItemStack baseStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) unlockState.data);
			ALoreBuilder loreBuilder = new ALoreBuilder();

			for(EffectData data : effectData) {
				loreBuilder.addLore(data.formatting.replaceAll("%value%", String.valueOf(data.values[level - 1])));
			}
			loreBuilder.addLore("");

			ProgressionManager.addPurchaseCostLore(loreBuilder, unlockState, pitPlayer.taintedSouls, getCost(level), true);
			if(unlockState == UnlockState.UNLOCKED) Misc.addEnchantGlint(baseStack);

			return new AItemStackBuilder(baseStack)
					.setName(unlockState.chatColor + "Path Unlock")
					.setLore(loreBuilder)
					.getItemStack();
		}

		public MajorProgressionUnlock getAssociatedUnlock() {
			if(this == firstPath) return firstPathUnlock;
			if(this == secondPath) return secondPathUnlock;
			throw new RuntimeException();
		}

		public class EffectData {
			public String formatting;
			public Double[] values = new Double[6];

			public EffectData(String formatting, double level1, double level2, double level3, double level4, double level5, double level6) {
				this.formatting = formatting;
				values[0] = level1;
				values[1] = level2;
				values[2] = level3;
				values[3] = level4;
				values[4] = level5;
				values[5] = level6;
			}
		}
	}
}
