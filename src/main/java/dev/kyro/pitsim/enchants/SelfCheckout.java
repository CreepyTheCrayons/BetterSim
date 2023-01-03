package dev.kyro.pitsim.enchants;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.logging.LogManager;
import dev.kyro.pitsim.battlepass.quests.EarnRenownQuest;
import dev.kyro.pitsim.controllers.DamageManager;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.PlayerManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.ApplyType;
import dev.kyro.pitsim.enums.KillModifier;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.events.KillEvent;
import dev.kyro.pitsim.megastreaks.NoMegastreak;
import dev.kyro.pitsim.megastreaks.RNGesus;
import dev.kyro.pitsim.megastreaks.Uberstreak;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SelfCheckout extends PitEnchant {
	public static SelfCheckout INSTANCE;
	public static final int LIVES_ON_USE = 3;

	public SelfCheckout() {
		super("Self-Checkout", true, ApplyType.PANTS,
				"selfcheckout", "self-checkout", "sco", "selfcheck", "checkout", "soco");
		INSTANCE = this;
	}

	@EventHandler
	public void onKill(KillEvent killEvent) {
		if(!killEvent.isKillerPlayer() || killEvent.getKiller() == killEvent.getDead()) return;

		ItemStack leggings = killEvent.getKiller().getEquipment().getLeggings();
		int enchantLvl = EnchantManager.getEnchantLevel(leggings, this);
		if(enchantLvl == 0) return;

		PitPlayer pitKiller = PitPlayer.getPitPlayer(killEvent.getKillerPlayer());
		if(pitKiller.getKills() + 1 < 200 || pitKiller.megastreak.getClass() == Uberstreak.class ||
				pitKiller.megastreak.getClass() == NoMegastreak.class || pitKiller.megastreak.getClass() == RNGesus.class) return;

		NBTItem nbtItem = new NBTItem(leggings);
		if(!EnchantManager.isJewelComplete(leggings) || !nbtItem.getString(NBTTag.ITEM_JEWEL_ENCHANT.getRef()).equalsIgnoreCase(refNames.get(0))) {
			AOutput.error(killEvent.getKiller(), "Self-Checkout only works on jewel items");
			return;
		}

		String scoMessage = "&9&lSCO!&7 Self-Checkout pants activated";
		int renown = Math.min((pitKiller.getKills() + 1) / 300, 4);
		if(renown != 0) {
			pitKiller.renown += renown;
			EarnRenownQuest.INSTANCE.gainRenown(pitKiller, renown);
			scoMessage += " giving &e" + renown + " &7 renown";
		}

		AOutput.send(killEvent.getKillerPlayer(), scoMessage);
		DamageManager.death(killEvent.getKiller(), KillModifier.SELF_CHECKOUT);

		if(nbtItem.hasKey(NBTTag.CURRENT_LIVES.getRef())) {
			int lives = nbtItem.getInteger(NBTTag.CURRENT_LIVES.getRef());
			if(lives - LIVES_ON_USE <= 0) {
				killEvent.getKillerPlayer().getEquipment().setLeggings(new ItemStack(Material.AIR));
				killEvent.getKillerPlayer().updateInventory();
				PlayerManager.sendItemBreakMessage(killEvent.getKillerPlayer(), leggings);
				if(pitKiller.stats != null) {
					pitKiller.stats.itemsBroken++;
					LogManager.onItemBreak(killEvent.getKillerPlayer(), nbtItem.getItem());
				}
			} else {
				nbtItem.setInteger(NBTTag.CURRENT_LIVES.getRef(), nbtItem.getInteger(NBTTag.CURRENT_LIVES.getRef()) - LIVES_ON_USE);
				EnchantManager.setItemLore(nbtItem.getItem(), pitKiller.player);
				killEvent.getKillerPlayer().getEquipment().setLeggings(nbtItem.getItem());
				killEvent.getKillerPlayer().updateInventory();

				if(pitKiller.stats != null) pitKiller.stats.livesLost += LIVES_ON_USE;
			}
		}
	}

	@Override
	public List<String> getDescription(int enchantLvl) {

		return new ALoreBuilder("&7On kill, if you have a killstreak", "&7of at least 200, &eExplode:",
				"&e\u25a0 &7Die! Keep jewel lives on death",
				"&a\u25a0 &7Gain &e+1 renown &7for every 300 killstreak (max 4)",
				"&c\u25a0 &7Lose &c" + LIVES_ON_USE + " lives &7on this item").getLore();
	}
}
