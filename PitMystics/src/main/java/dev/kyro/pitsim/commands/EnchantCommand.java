package dev.kyro.pitsim.commands;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.PitEnchant;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.exceptions.InvalidEnchantLevelException;
import dev.kyro.pitsim.exceptions.MaxEnchantsExceededException;
import dev.kyro.pitsim.exceptions.MaxTokensExceededException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EnchantCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(!(sender instanceof Player)) return false;
		Player player = (Player) sender;

		if(player.getItemInHand() == null) {

			AOutput.error(player, "Not holding a mystic item");
			return false;
		}
		NBTItem nbtItem = new NBTItem(player.getItemInHand());
		if(!nbtItem.hasKey(NBTTag.ITEM_UUID.getRef())) {

			AOutput.error(player, "Not holding a mystic item");
			return false;
		}

		if(args.length < 2) {

			AOutput.error(player, "Usage: /enchant <name> <level>");
			return false;
		}

		String refName = args[0].toLowerCase();
		PitEnchant pitEnchant = EnchantManager.getEnchant(refName);
		if(pitEnchant == null) {

			AOutput.error(player, "That enchant does not exist");
			return false;
		}

		int level;
		try {
			level = Integer.parseInt(args[1]);
		} catch(Exception ignored) {
			AOutput.error(player, "Usage: /enchant <name> <level>");
			return false;
		}

		ItemStack updatedItem;
		try {
			if(player.isOp()) {
				updatedItem = EnchantManager.addEnchant(player.getItemInHand(), pitEnchant, level, false);
			} else {
				updatedItem = EnchantManager.addEnchant(player.getItemInHand(), pitEnchant, level, true);
			}
		} catch(Exception e) {
			if(e instanceof InvalidEnchantLevelException) {

				if(!((InvalidEnchantLevelException) e).levelTooHigh) {
					AOutput.error(player, "Invalid enchant level");
				} else {
					AOutput.error(player, "There is already a higher level enchant on this item");
				}
			} else if(e instanceof MaxTokensExceededException) {

				if(!((MaxTokensExceededException) e).isRare) {
					AOutput.error(player, "You cannot have more than 5 rare tokens on an item");
				} else {
					AOutput.error(player, "You cannot have more than 8 tokens on an item");
				}
			} else if(e instanceof MaxEnchantsExceededException) {

				AOutput.error(player, "You cannot have more than 3 enchants on an item");
			} else {
				e.printStackTrace();
			}
			return false;
		}

		player.setItemInHand(updatedItem);
		AOutput.send(player, "Added the enchant");
		return false;
	}
}