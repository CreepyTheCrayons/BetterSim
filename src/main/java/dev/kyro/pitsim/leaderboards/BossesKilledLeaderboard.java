package dev.kyro.pitsim.leaderboards;

import dev.kyro.arcticapi.builders.AItemStackBuilder;
import dev.kyro.arcticapi.builders.ALoreBuilder;
import dev.kyro.pitsim.controllers.objects.Leaderboard;
import dev.kyro.pitsim.controllers.objects.LeaderboardData;
import dev.kyro.pitsim.controllers.objects.LeaderboardPosition;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.misc.Formatter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class BossesKilledLeaderboard extends Leaderboard {
	public BossesKilledLeaderboard() {
		super("bosses-killed", "&5Bosses Killed");
	}

	@Override
	public ItemStack getDisplayStack(UUID uuid) {
		ItemStack itemStack = new AItemStackBuilder(Material.MOB_SPAWNER)
				.setName("&5Bosses Killed")
				.setLore(new ALoreBuilder(
						"&7Players who have &ckilled &7the most", "&5Bosses &7in the &5Darkzone", ""
				).addLore(getTopPlayers(uuid)).addLore(
						"", "&eClick to pick!"
				))
				.getItemStack();
		return itemStack;
	}

	@Override
	public String getDisplayValue(LeaderboardPosition position) {
		return "&5" + Formatter.formatLarge(position.intValue) + " boss" + (position.intValue == 1 ? "" : "es");
	}

	@Override
	public String getDisplayValue(PitPlayer pitPlayer) {
		return "&5" + Formatter.formatLarge(pitPlayer.stats.bossesKilled) + " boss" + (pitPlayer.stats.bossesKilled == 1 ? "" : "es");
	}

	@Override
	public void setPosition(LeaderboardPosition position) {
		LeaderboardData data = LeaderboardData.getLeaderboardData(this);

		position.intValue = (int) data.getValue(position.uuid).primaryValue;
	}

	@Override
	public boolean isMoreThanOrEqual(LeaderboardPosition position, LeaderboardPosition otherPosition) {
		return position.intValue >= otherPosition.intValue;
	}
}
