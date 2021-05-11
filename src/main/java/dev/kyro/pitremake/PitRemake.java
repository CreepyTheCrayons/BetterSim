package dev.kyro.pitremake;

import dev.kyro.arcticapi.ArcticAPI;
import dev.kyro.pitremake.commands.ATestCommand;
import dev.kyro.pitremake.commands.EnchantCommand;
import dev.kyro.pitremake.commands.FreshCommand;
import dev.kyro.pitremake.commands.NonCommand;
import dev.kyro.pitremake.controllers.CooldownManager;
import dev.kyro.pitremake.controllers.DamageManager;
import dev.kyro.pitremake.controllers.EnchantManager;
import dev.kyro.pitremake.controllers.PitEnchant;
import dev.kyro.pitremake.enchants.*;
import dev.kyro.pitremake.nons.Non;
import dev.kyro.pitremake.nons.NonManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class PitRemake extends JavaPlugin {

	public static PitRemake INSTANCE;

	@Override
	public void onEnable() {

		INSTANCE = this;

		loadConfig();

		ArcticAPI.configInit(this, "prefix", "error-prefix");

		CooldownManager.init();

		registerCommands();
		registerListeners();
		registerEnchants();
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
//		After ComboHeal
		EnchantManager.registerEnchant(new GoldAndBoosted());
		EnchantManager.registerEnchant(new PainFocus());

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

		EnchantManager.registerEnchant(new Solitude());
		EnchantManager.registerEnchant(new DiamondAllergy());
		EnchantManager.registerEnchant(new FractionalReserve());
		EnchantManager.registerEnchant(new Protection());
		EnchantManager.registerEnchant(new Prick());
		EnchantManager.registerEnchant(new RingArmor());
		EnchantManager.registerEnchant(new PitBlob());

//		After all
		EnchantManager.registerEnchant(new Regularity());
		EnchantManager.registerEnchant(new Lifesteal());
	}

	private void registerCommands() {

		getCommand("non").setExecutor(new NonCommand());
		getCommand("atest").setExecutor(new ATestCommand());
		getCommand("enchant").setExecutor(new EnchantCommand());
		getCommand("fresh").setExecutor(new FreshCommand());
	}

	private void registerListeners() {

		getServer().getPluginManager().registerEvents(new DamageManager(), this);
		getServer().getPluginManager().registerEvents(new NonManager(), this);
	}

	private void loadConfig() {

		getConfig().options().copyDefaults(true);
		saveConfig();
	}
}
