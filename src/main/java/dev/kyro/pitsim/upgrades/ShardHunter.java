package dev.kyro.pitsim.upgrades;

import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.aitems.misc.AncientGemShard;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.controllers.UpgradeManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.controllers.objects.RenownUpgrade;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.megastreaks.Uberstreak;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShardHunter extends RenownUpgrade {
	public ShardHunter() {
		super("Shardhunter", "SHARDHUNTER", 40, 32, 28, true, 10);
	}

	@Override
	public List<Integer> getTierCosts() {
//		return Arrays.asList(40, 45, 50, 55, 60, 70, 80, 90, 100, 120);
//		return Arrays.asList(10, 13, 16, 19, 22, 25, 30, 35, 40, 50,    75, 100, 125, 150, 200    250, 300, 350, 400, 500);
		return Arrays.asList(10, 13, 16, 19, 22, 25, 30, 35, 40, 50);
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!killEvent.isKillerPlayer() || !killEvent.isDeadPlayer()) return;

		if(!UpgradeManager.hasUpgrade(killEvent.getKillerPlayer(), this)) return;

		int tier = UpgradeManager.getTier(killEvent.getKillerPlayer(), this);
		if(tier == 0) return;

		double chance = 0.00005 * tier;

		PitPlayer pitKiller = killEvent.getKillerPitPlayer();
		if(pitKiller.megastreak.isOnMega() && pitKiller.megastreak instanceof Uberstreak)
			chance *= Uberstreak.SHARD_MULTIPLIER;

		boolean givesShard = Math.random() < chance;

		if(!givesShard) return;
		AUtil.giveItemSafely(killEvent.getKillerPlayer(), ItemFactory.getItem(AncientGemShard.class).getItem(1), true);
		AOutput.send(killEvent.getKiller(), "&d&lGEM SHARD!&7 obtained from killing " + killEvent.getDeadPlayer().getDisplayName() + "!");

		File file = new File("plugins/NoteBlockAPI/Effects/ShardHunter.nbs");
		Song song = NBSDecoder.parse(file);
		RadioSongPlayer rsp = new RadioSongPlayer(song);
		rsp.setRepeatMode(RepeatMode.NO);
		rsp.addPlayer(killEvent.getKillerPlayer());
		rsp.setPlaying(true);
	}

	@Override
	public ItemStack getDisplayItem(Player player) {
		ItemStack item = new ItemStack(Material.EMERALD);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(UpgradeManager.itemNameString(this, player));
		meta.addEnchant(Enchantment.ARROW_FIRE, 1, false);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		List<String> lore = new ArrayList<>();
		if(UpgradeManager.hasUpgrade(player, this)) lore.add(ChatColor.translateAlternateColorCodes('&',
				"&7Current: &f" + 0.005 * UpgradeManager.getTier(player, this) + "&f% &7drop chance"));
		if(UpgradeManager.hasUpgrade(player, this))
			lore.add(ChatColor.GRAY + "Tier: " + ChatColor.GREEN + AUtil.toRoman(UpgradeManager.getTier(player, this)));
		if(UpgradeManager.hasUpgrade(player, this)) lore.add("");
		lore.add(ChatColor.GRAY + "Each Tier:");
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7Gain &f+0.005% &7chance to obtain a &aGem"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&aShard &7on kill. &7Use &aGem Shards"));
		lore.add(ChatColor.translateAlternateColorCodes('&', "&7to create &aTotally Legit Gems&7."));
		meta.setLore(UpgradeManager.loreBuilder(this, player, lore, true));
		item.setItemMeta(meta);
		return item;
	}

	@Override
	public String getSummary() {
		return "&aShardhunter&7 is an &erenown&7 upgrade that gives you the small chance to gain &aAncient Gem " +
				"Shards&7 on bot/player kills, &aShards&7 can be used to craft a &aGem&7 that allows you to get nine " +
				"token &3Jewel&7 Items";
	}
}
