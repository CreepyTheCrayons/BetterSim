package dev.kyro.pitsim.placeholders;

import dev.kyro.arcticapi.hooks.papi.APAPIPlaceholder;
import dev.kyro.arcticguilds.GuildLeaderboardData;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class GuildPlaceholder2 implements APAPIPlaceholder {

	@Override
	public String getIdentifier() {
		return "guild2";
	}

	@Override
	public String getValue(Player player) {
		GuildLeaderboardData guild = GuildLeaderboardData.getGuildData(1);
		if(guild == null) return "&cNone!";
		DecimalFormat formatter = new DecimalFormat("#,###.#");
		StringBuilder string = new StringBuilder("&f2. ");
		string.append(guild.getColor()).append(guild.name).append(" &7- &e").append(formatter.format(guild.reputation));
		return ChatColor.translateAlternateColorCodes('&', string.toString());
	}
}
