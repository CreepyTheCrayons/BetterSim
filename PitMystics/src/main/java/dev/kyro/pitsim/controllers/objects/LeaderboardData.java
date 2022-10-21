package dev.kyro.pitsim.controllers.objects;

import javafx.util.Pair;

import java.util.*;

public class LeaderboardData {

	public static Map<Leaderboard, LeaderboardData> leaderboards = new HashMap<>();

	private final Leaderboard leaderboard;
	private final String leaderboardData;
	private final Map<UUID, PlayerData> leaderboardDataMap;

	public LeaderboardData(Leaderboard leaderboard, String leaderboardData) {
		this.leaderboard = leaderboard;
		this.leaderboardData = leaderboardData;

		this.leaderboardDataMap = new LinkedHashMap<>();
		String[] playerSplit = leaderboardData.split("\\|");
		for(String data : playerSplit) {
			String[] playerData = data.split(",");
			if(playerData.length == 3) {
				UUID uuid = UUID.fromString(playerData[0]);
				double value = Double.parseDouble(playerData[2]);
				leaderboardDataMap.put(uuid, new PlayerData(value, playerData[1]));
			} else {
				System.out.println("--------------");
				System.out.println(playerData[2]);
				System.out.println(playerData[3]);
				System.out.println(playerData[4]);
				System.out.println(playerData[1]);
				System.out.println("--------------");

				leaderboardDataMap.put(UUID.fromString(playerData[0]), new PlayerData(Integer.parseInt(playerData[2]),
						Integer.parseInt(playerData[3]), Long.parseLong(playerData[4]), playerData[1]));
			}
		}

		leaderboards.put(leaderboard, this);
	}

	public Leaderboard getLeaderboard() {
		return leaderboard;
	}

	public String getLeaderboardData() {
		return leaderboardData;
	}

	public Map<UUID, PlayerData> getLeaderboardDataMap() {
		return leaderboardDataMap;
	}

	public String getPrefix(UUID uuid) {
		if(!leaderboardDataMap.containsKey(uuid)) return null;
		return leaderboardDataMap.get(uuid).prefix;
	}

	public PlayerData getValue(UUID uuid) {
		return leaderboardDataMap.get(uuid);
	}

	public static LeaderboardData getLeaderboardData(Leaderboard leaderboard) {
		return leaderboards.get(leaderboard);
	}

	public static class PlayerData {

		public double primaryValue;
		public int prestige;
		public int level;
		public long xp;
		public String prefix;

		public PlayerData(int prestige, int level, long xp, String prefix) {
			this.prestige = prestige;
			this.level = level;
			this.xp = xp;
			this.prefix = prefix;
		}

		public PlayerData(double primaryValue, String prefix) {
			this.primaryValue = primaryValue;
			this.prefix = prefix;
		}
	}

}
