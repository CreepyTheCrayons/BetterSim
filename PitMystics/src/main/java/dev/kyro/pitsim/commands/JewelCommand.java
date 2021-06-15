package dev.kyro.pitsim.commands;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.ItemManager;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.enums.PantColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class JewelCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(!(sender instanceof Player)) return false;
//		if(!sender.isOp()) return false;

		double finalBalance = PitSim.VAULT.getBalance((Player) sender) - 200000;
		if(finalBalance < 0) return false;
		PitSim.VAULT.withdrawPlayer((Player) sender, 200000);
		AOutput.send(sender, "&aPurchased a jewel for &6200,000g");


		Player player = (Player) sender;

		if(!player.isOp()) {
			player.kickPlayer("gay");
			return false;
		}

		if(args.length < 1) {

			AOutput.error(player, "Usage: /fresh <sword|bow|pants>");
			return false;
		}

		MysticType mysticType = null;
		String type = args[0].toLowerCase();
		switch(type) {
			case "sword":
				mysticType = MysticType.SWORD;
				break;
			case "bow":
				mysticType = MysticType.BOW;
				break;
			case "pants":
			case "pant":
			case "fresh":
				mysticType = MysticType.PANTS;
				break;
		}
		if(mysticType == null) {

			AOutput.error(player, "Usage: /fresh <sword|bow|pants>");
			return false;
		}

		ItemStack jewel = FreshCommand.getFreshItem(mysticType, PantColor.JEWEL);
		jewel = ItemManager.enableDropConfirm(jewel);
		assert jewel != null;
		NBTItem nbtItem = new NBTItem(jewel);
		nbtItem.setBoolean(NBTTag.IS_JEWEL.getRef(), true);
		EnchantManager.setItemLore(nbtItem.getItem());

		AUtil.giveItemSafely(player, nbtItem.getItem());
		return false;
	}
}
