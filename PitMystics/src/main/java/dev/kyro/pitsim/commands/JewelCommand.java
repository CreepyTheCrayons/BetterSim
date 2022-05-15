package dev.kyro.pitsim.commands;

import de.tr7zw.nbtapi.NBTItem;
import dev.kyro.arcticapi.commands.ACommand;
import dev.kyro.arcticapi.commands.AMultiCommand;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.arcticapi.misc.AUtil;
import dev.kyro.pitsim.controllers.EnchantManager;
import dev.kyro.pitsim.controllers.ItemManager;
import dev.kyro.pitsim.controllers.objects.PitEnchant;
import dev.kyro.pitsim.enums.MysticType;
import dev.kyro.pitsim.enums.NBTTag;
import dev.kyro.pitsim.enums.PantColor;
import dev.kyro.pitsim.misc.Constant;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class JewelCommand extends ACommand {
	public JewelCommand(AMultiCommand base, String executor) {
		super(base, executor);
	}

	@Override
	public void execute(CommandSender sender, Command command, String alias, List<String> args) {
		if(!(sender instanceof Player)) return;
		if(args.size() < 1) {
			AOutput.error(sender, "Usage: /jewel <sword|bow|pants> [enchant] [max-lives]");
			return;
		}

		MysticType mysticType = null;
		String type = args.get(0).toLowerCase();
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

			AOutput.error(sender, "Usage: /fresh <sword|bow|pants>");
			return;
		}

		ItemStack jewel = FreshCommand.getFreshItem(mysticType, PantColor.JEWEL);
		jewel = ItemManager.enableDropConfirm(jewel);
		assert jewel != null;
		NBTItem nbtItem = new NBTItem(jewel);
		nbtItem.setBoolean(NBTTag.IS_JEWEL.getRef(), true);

		if(args.size() >= 2) {
			nbtItem.setInteger(NBTTag.JEWEL_KILLS.getRef(), Constant.JEWEL_KILLS);

			String enchantString = args.get(1);
			PitEnchant jewelEnchant = EnchantManager.getEnchant(enchantString);
			if(jewelEnchant == null) {
				AOutput.error(sender, "Invalid enchant");
				return;
			}

			int maxLives;
			if(args.size() >= 3) {
				try {
					maxLives = Integer.parseInt(args.get(2));
					if(maxLives <= 0) throw new Exception();
				} catch(Exception ignored) {
					AOutput.error(sender, "Invalid max lives");
					return;
				}
			} else {
				maxLives = EnchantManager.getRandomMaxLives();
			}

			PantColor.setPantColor(nbtItem.getItem(), PantColor.getNormalRandom());
			nbtItem.setInteger(NBTTag.MAX_LIVES.getRef(), maxLives);
			nbtItem.setInteger(NBTTag.CURRENT_LIVES.getRef(), maxLives);
			try {
				jewel = EnchantManager.addEnchant(nbtItem.getItem(), jewelEnchant, 3, false, true, -1);
			} catch(Exception ignored) {
			}
		}

		EnchantManager.setItemLore(jewel, null);
		AUtil.giveItemSafely((Player) sender, jewel);
	}

	@Override
	public List<String> getTabComplete(Player player, String current, List<String> args) {
		return null;
	}
}
