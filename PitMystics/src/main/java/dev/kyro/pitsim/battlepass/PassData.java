package dev.kyro.pitsim.battlepass;

import java.util.Date;
import java.util.Map;

public class PassData {
	public int passLevel;
	public boolean hasPremium;

	public Map<Integer, Boolean> freePassClaims;
	public Map<Integer, Boolean> premiumPassClaims;

	public Map<String, Date> dailyQuestCompletions;
}