package dev.kyro.pitsim.commands;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.aitems.PitItem;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.ItemFactory;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enchants.overworld.SelfCheckout;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.exceptions.*;
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

		if(!PitSim.isDev()) {
			if(!player.hasPermission("group.nitro")) {
				AOutput.send(player, "&cYou must boost our discord server to gain access to this feature!&7 Join with: &f&ndiscord.pitsim.net");
				return false;
			}

			if(MysticType.getMysticType(player.getItemInHand()) == MysticType.TAINTED_CHESTPLATE || MysticType.getMysticType(player.getItemInHand()) == MysticType.TAINTED_SCYTHE) {
				if(!player.isOp()) {
					AOutput.error(player, "&cNice try.");
					return false;
				}
			}
		}

		PitItem pitItem = ItemFactory.getItem(player.getItemInHand());
		if(pitItem == null || !pitItem.isMystic) {
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
		if((pitEnchant.isTainted || pitEnchant == SelfCheckout.INSTANCE) && !player.isOp()) {
			AOutput.error(player, "&cNice try.");
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
			if(player.isOp() || PitSim.isDev()) {
				updatedItem = EnchantManager.addEnchant(player.getItemInHand(), pitEnchant, level, false);
			} else {
				updatedItem = EnchantManager.addEnchant(player.getItemInHand(), pitEnchant, level, true);
			}
		} catch(Exception exception) {
			if(exception instanceof MismatchedEnchantException) {
				AOutput.error(player, "That enchant can't go on that item");
			} else if(exception instanceof InvalidEnchantLevelException) {

				if(!((InvalidEnchantLevelException) exception).levelTooHigh) {
					AOutput.error(player, "Level too low");
				} else {
					AOutput.error(player, "Level too high");
				}
			} else if(exception instanceof MaxTokensExceededException) {

				if(((MaxTokensExceededException) exception).isRare) {
					AOutput.error(player, "You cannot have more than 4 rare tokens on an item");
				} else {
					AOutput.error(player, "You cannot have more than 8 tokens on an item");
				}
			} else if(exception instanceof MaxEnchantsExceededException) {

				AOutput.error(player, "You cannot have more than 3 enchants on an item");
			} else if(exception instanceof IsJewelException) {
				AOutput.error(player, "You cannot modify a jewel enchant");
			} else if(exception instanceof NoCommonEnchantException) {
				AOutput.error(player, "You must have at least one common enchant on an item");
			} else {
				exception.printStackTrace();
			}
			return false;
		}

		player.setItemInHand(updatedItem);
		AOutput.send(player, "Added the enchant");
		return false;
	}
}