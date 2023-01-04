package dev.kyro.pitsim.battlepass.rewards;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.battlepass.PassReward;
import dev.kyro.pitsim.commands.FreshCommand;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.enums.PantColor;
import dev.kyro.pitsim.misc.Misc;
import org.bukkit.inventory.ItemStack;

public class PassPantsReward extends PassReward {
	public int count;

	public PassPantsReward(int count) {
		this.count = count;
	}

	@Override
	public boolean giveReward(PitPlayer pitPlayer) {
		if(Misc.getEmptyInventorySlots(pitPlayer.player) < count) {
			AOutput.error(pitPlayer.player, "&7Please make space in your inventory");
			return false;
		}

		for(int i = 0; i < count; i++) {
			ItemStack jewel = FreshCommand.getFreshItem(MysticType.PANTS, PantColor.JEWEL);
//			jewel = ItemManager.enableDropConfirm(jewel);
			NBTItem nbtItem = new NBTItem(jewel);
			nbtItem.setBoolean(NBTTag.IS_JEWEL.getRef(), true);
			EnchantManager.setItemLore(nbtItem.getItem(), pitPlayer.player);
			AUtil.giveItemSafely(pitPlayer.player, nbtItem.getItem());
		}
		return true;
	}

	@Override
	public ItemStack getDisplayItem(PitPlayer pitPlayer, boolean hasClaimed) {
		ItemStack itemStack = FreshCommand.getFreshItem(MysticType.PANTS, PantColor.JEWEL);
		new AItemStackBuilder(itemStack)
				.setName("&3&lPants Reward")
				.setLore(new ALoreBuilder(
						"&7Reward: &3" + count + "x Hidden Jewel Pants"
				));
		itemStack.setAmount(count);
		return itemStack;
	}
}
