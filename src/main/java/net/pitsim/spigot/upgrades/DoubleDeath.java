package net.pitsim.spigot.upgrades;

import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.spigot.controllers.UpgradeManager;
import net.pitsim.spigot.controllers.objects.TieredRenownUpgrade;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class DoubleDeath extends TieredRenownUpgrade {
	public static DoubleDeath INSTANCE;

	public DoubleDeath() {
		super("Double-Death", "DOUBLE_DEATH", 9);
		INSTANCE = this;
	}

	public boolean isDoubleDeath(Player player) {
		if(!UpgradeManager.hasUpgrade(player, this)) return false;

		int tier = UpgradeManager.getTier(player, this);
		if(tier == 0) return false;

		double chance = 0.01 * (tier * 1);

		boolean isDouble = Math.random() < chance;

		if(isDouble) {
			AOutput.send(player, "&d&lDOUBLE DEATH!&7 Megastreak death rewards doubled!");

			File file = new File("plugins/NoteBlockAPI/Effects/DoubleDeath.nbs");
			Song song = NBSDecoder.parse(file);
			RadioSongPlayer rsp = new RadioSongPlayer(song);
			rsp.setRepeatMode(RepeatMode.NO);
			rsp.addPlayer(player);
			rsp.setAutoDestroy(true);
			rsp.setPlaying(true);
		}

		return isDouble;
	}

	@Override
	public ItemStack getBaseDisplayStack() {
		return new AItemStackBuilder(Material.SKULL_ITEM)
				.getItemStack();
	}

	@Override
	public String getCurrentEffect(int tier) {
		return "&d" + (tier * 1) + "% chance";
	}

	@Override
	public String getEffectPerTier() {
		return "&7Gain &d+5% chance &7to double megastreak death rewards";
	}

	@Override
	public String getSummary() {
		return "&dDouble-Death &7gives you a chance to gain double death rewards on a &cMegastreak";
	}

	@Override
	public List<Integer> getTierCosts() {
		return Arrays.asList(5, 10, 20, 40, 80);
	}
}
