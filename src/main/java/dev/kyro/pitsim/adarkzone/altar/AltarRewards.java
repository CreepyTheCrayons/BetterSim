package dev.kyro.pitsim.adarkzone.altar;

import dev.kyro.pitsim.adarkzone.altar.pedestals.TurmoilPedestal;
import dev.kyro.pitsim.adarkzone.altar.pedestals.WealthPedestal;
import org.bukkit.entity.Player;

import java.util.Random;

public class AltarRewards {

	//Primary 3 pedestals only determine chance.
	//Base amount is based on LOW/MEDIUM/HIGH system (All base numbers should be doubles)
	//Multipliers are Wealth, Turmoil, and Souls/Base roll cost

	public static void rewardPlayer(Player player) {
		boolean hasTurmoil = AltarPedestal.getPedestal(TurmoilPedestal.class).isActivated(player);
		boolean positiveTurmoil = new Random().nextBoolean();

		for(AltarPedestal.ALTAR_REWARD reward : AltarPedestal.ALTAR_REWARD.values()) {
			Random random = new Random();

//			int chance = AltarPedestal.getRewardChance(player, reward);
//			if(random.nextInt(100) > chance) continue;

			AltarPedestal.REWARD_SIZE size = AltarPedestal.REWARD_SIZE.SMALL;

			for(AltarPedestal.REWARD_SIZE value : AltarPedestal.REWARD_SIZE.values()) {
				if(random.nextInt(100) <= value.base + AltarPedestal.getRewardChance(player, reward)) {
					size = value;
					break;
				}
			}

			double rewardCount = reward.getRewardCount(size);

			rewardCount *= getSoulMultiplier(player);

			if(AltarPedestal.getPedestal(WealthPedestal.class).isActivated(player)) rewardCount *= AltarPedestal.WEALTH_MULTIPLIER;

			if(hasTurmoil) {
				int breakChance = positiveTurmoil ? 3 : 15;

				double multiplier = 1;
				while(new Random().nextInt(100) > breakChance) multiplier += 0.1;
				rewardCount *= multiplier;
			}

			reward.rewardPlayer(player, (int) Math.floor(rewardCount));
		}

		//Weighted map of LOW/MEDIUM/HIGH
		//Chance multiplier changes weight of certain catag

		//XP:
		//Calculate chance for LOW/MED/HIGH using base chance and XP chance increase
		//if != LOW, add small randomization to amount
		//Add static XP multiplier for XP pedestal being on
		//Add static wealth multiplier if wealth is on
		//Apply Turmoil

		//Renown and Vouchers: same as XP

		//Turmoil:
		//good/bad calc 50/50 (determines break chance of while loop)
		//starting multiplier < 1, increases by 0.1 each loop
		//when loop breaks, multiply total items by multiplier

		//Remove itemstack chance indicators
		//Copy soul explosion code for xp orb count


	}

	public static double getSoulMultiplier(Player player) {
		return AltarPedestal.getTotalCost(player) / (double) AltarPedestal.BASE_COST;
	}
}
