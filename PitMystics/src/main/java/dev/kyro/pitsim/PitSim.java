package dev.kyro.pitsim;

import dev.kyro.arcticapi.ArcticAPI;
import dev.kyro.arcticapi.commands.ABaseCommand;
import dev.kyro.arcticapi.data.AData;
import dev.kyro.arcticapi.hooks.AHook;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.commands.*;
import dev.kyro.pitsim.commands.market.AuctionCommand;
import dev.kyro.pitsim.commands.market.ListCommand;
import dev.kyro.pitsim.commands.market.MarketCommand;
import dev.kyro.pitsim.controllers.*;
import dev.kyro.pitsim.controllers.market.MarketManager;
import dev.kyro.pitsim.enchants.*;
import dev.kyro.pitsim.perks.*;
import dev.kyro.pitsim.placeholders.CombatTimerPlaceholder;
import dev.kyro.pitsim.placeholders.GladiatorPlaceholder;
import dev.kyro.pitsim.placeholders.LevelBracketPlaceholder;
import dev.kyro.pitsim.placeholders.StrengthChainingPlaceholder;
import me.liwk.karhu.api.KarhuAPI;
import me.liwk.karhu.api.event.KarhuListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class PitSim extends JavaPlugin {

	public static PitSim INSTANCE;
	public static Economy VAULT = null;
	public static AData playerList;

	@Override
	public void onEnable() {

		INSTANCE = this;
		if (!setupEconomy()) {
			AOutput.log(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
		} else {
			AOutput.log(String.format("Could not find PlaceholderAPI! This plugin is required."));
			Bukkit.getPluginManager().disablePlugin(this);
		}




		ArcticAPI.setupPlaceholderAPI("pitsim");
		AHook.registerPlaceholder(new LevelBracketPlaceholder());
		AHook.registerPlaceholder(new StrengthChainingPlaceholder());
		AHook.registerPlaceholder(new GladiatorPlaceholder());
		AHook.registerPlaceholder(new CombatTimerPlaceholder());

		loadConfig();

		ArcticAPI.configInit(this, "prefix", "error-prefix");
		playerList = new AData("player-list", "", false);

		CooldownManager.init();

		registerCommands();
		registerListeners();
		registerEnchants();
		registerUpgrades();
	}

	@Override
	public void onDisable() {

		List<Non> copyList = new ArrayList<>(NonManager.nons);
		for(Non non : copyList) {

			non.remove();
		}

		for(PitEnchant pitEnchant : EnchantManager.pitEnchants) pitEnchant.onDisable();
	}

	private void registerEnchants() {

		EnchantManager.registerEnchant(new Billionaire());
		EnchantManager.registerEnchant(new Gamble());
		EnchantManager.registerEnchant(new Executioner());
		EnchantManager.registerEnchant(new ComboPerun());
		EnchantManager.registerEnchant(new ComboDamage());
		EnchantManager.registerEnchant(new ComboHeal());
		EnchantManager.registerEnchant(new Punisher());
		EnchantManager.registerEnchant(new KingBuster());
		EnchantManager.registerEnchant(new Bruiser());
		EnchantManager.registerEnchant(new BeatTheSpammers());
		EnchantManager.registerEnchant(new Sharp());
		EnchantManager.registerEnchant(new Crush());
		EnchantManager.registerEnchant(new SpeedyHit());
		EnchantManager.registerEnchant(new ComboSwift());
		EnchantManager.registerEnchant(new DiamondStomp());
		EnchantManager.registerEnchant(new BulletTime());
		EnchantManager.registerEnchant(new Healer());
		EnchantManager.registerEnchant(new Duelist());
		EnchantManager.registerEnchant(new ComboStun());
		EnchantManager.registerEnchant(new GoldAndBoosted());
		EnchantManager.registerEnchant(new PainFocus());
		EnchantManager.registerEnchant(new Shark());

		EnchantManager.registerEnchant(new MegaLongBow());
		EnchantManager.registerEnchant(new Volley());
		EnchantManager.registerEnchant(new Chipping());
		EnchantManager.registerEnchant(new Telebow());
		EnchantManager.registerEnchant(new Robinhood());
		EnchantManager.registerEnchant(new Fletching());
		EnchantManager.registerEnchant(new PushComesToShove());
		EnchantManager.registerEnchant(new Wasp());
		EnchantManager.registerEnchant(new SprintDrain());
		EnchantManager.registerEnchant(new BottomlessQuiver());
		EnchantManager.registerEnchant(new Parasite());
		EnchantManager.registerEnchant(new LuckyShot());
		EnchantManager.registerEnchant(new Pullbow());
		EnchantManager.registerEnchant(new Explosive());
		EnchantManager.registerEnchant(new FasterThanTheirShadows());
		EnchantManager.registerEnchant(new PinDown());

		EnchantManager.registerEnchant(new Solitude());
		EnchantManager.registerEnchant(new DiamondAllergy());
		EnchantManager.registerEnchant(new FractionalReserve());
		EnchantManager.registerEnchant(new Protection());
		EnchantManager.registerEnchant(new Prick());
		EnchantManager.registerEnchant(new RingArmor());
//		EnchantManager.registerEnchant(new PitBlob());
		EnchantManager.registerEnchant(new Peroxide());
		EnchantManager.registerEnchant(new NewDeal());
		EnchantManager.registerEnchant(new HeighHo());
		EnchantManager.registerEnchant(new GoldenHeart());
		EnchantManager.registerEnchant(new RetroGravityMicrocosm());
		EnchantManager.registerEnchant(new Mirror());
		EnchantManager.registerEnchant(new LastStand());
		EnchantManager.registerEnchant(new Booboo());
		EnchantManager.registerEnchant(new CriticallyFunky());
		EnchantManager.registerEnchant(new GottaGoFast());
		EnchantManager.registerEnchant(new Electrolytes());
		EnchantManager.registerEnchant(new CounterOffensive());

//		Resource Enchants
		EnchantManager.registerEnchant(new Moctezuma());
		EnchantManager.registerEnchant(new GoldBump());
		EnchantManager.registerEnchant(new GoldBoost());

//		After all
		EnchantManager.registerEnchant(new Regularity());
		EnchantManager.registerEnchant(new Lifesteal());
	}

	private void registerUpgrades() {

		PerkManager.registerUpgrade(new NoPerk());
		PerkManager.registerUpgrade(new Vampire());
		PerkManager.registerUpgrade(new Dirty());
		PerkManager.registerUpgrade(new StrengthChaining());
		PerkManager.registerUpgrade(new Gladiator());
		PerkManager.registerUpgrade(new Thick());
	}

	private void registerCommands() {

		ABaseCommand marketCommand = new MarketCommand("market");
		marketCommand.registerCommand(new ListCommand("list"));
		marketCommand.registerCommand(new AuctionCommand("ah"));

//		getCommand("atest").setExecutor(new ATestCommand());
		getCommand("perks").setExecutor(new PerkCommand());
		getCommand("non").setExecutor(new NonCommand());
		getCommand("enchant").setExecutor(new EnchantCommand());
		getCommand("fresh").setExecutor(new FreshCommand());
		getCommand("show").setExecutor(new ShowCommand());
		getCommand("jewel").setExecutor(new JewelCommand());
	}

	private void registerListeners() {

		KarhuAPI.getEventRegistry().addListener(new BypassManager());
		getServer().getPluginManager().registerEvents(new DamageManager(), this);
		getServer().getPluginManager().registerEvents(new NonManager(), this);
		getServer().getPluginManager().registerEvents(new PlayerManager(), this);
		getServer().getPluginManager().registerEvents(new ChatManager(), this);
		getServer().getPluginManager().registerEvents(new DamageIndicator(), this);
		getServer().getPluginManager().registerEvents(new MarketManager(), this);
		getServer().getPluginManager().registerEvents(new CombatManager(), this);
		getServer().getPluginManager().registerEvents(new SpawnManager(), this);
	}

	private void loadConfig() {

		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		VAULT = rsp.getProvider();
		return VAULT != null;
	}
}
