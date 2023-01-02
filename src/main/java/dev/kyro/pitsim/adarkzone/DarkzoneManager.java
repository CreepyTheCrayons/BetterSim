package dev.kyro.pitsim.adarkzone;

import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class DarkzoneManager implements Listener {
	public static List<SubLevel> subLevels = new ArrayList<>();

	static {
	}

	public static void registerSubLevel(SubLevel subLevel) {

	}

	public static SubLevel getSubLevel(Class<? extends SubLevel> clazz) {
		for(SubLevel subLevel : subLevels) if(subLevel.getClass() == clazz) return subLevel;
		return null;
	}
}
