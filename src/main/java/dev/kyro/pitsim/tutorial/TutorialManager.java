package dev.kyro.pitsim.tutorial;

import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.SpawnManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TutorialManager implements Listener {
	public static List<NPCCheckpoint> checkpoints = new ArrayList<>();
	public static Map<Player, Location> lastLocationMap = new HashMap<>();

	public static final int DARKZONE_OBJECTIVE_DISTANCE = 8;

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		DarkzoneTutorial darkzoneTutorial = pitPlayer.darkzoneTutorial;
		if(!darkzoneTutorial.isActive()) return;

		if(!darkzoneTutorial.isInObjective) {
			for(NPCCheckpoint checkpoint : checkpoints) {
				if(checkpoint.location.distance(player.getLocation()) > DARKZONE_OBJECTIVE_DISTANCE) continue;
				if(darkzoneTutorial.data.completedObjectives.contains(checkpoint.objective)) continue;
				if(darkzoneTutorial.tutorialNPC.getCheckpoint() == checkpoint) continue;

				darkzoneTutorial.tutorialNPC.setCheckpoint(checkpoint);
			}
		}

		if(SpawnManager.isInSpawn(player.getLocation())) {
			lastLocationMap.put(player, player.getLocation());
		} else {
			player.teleport(lastLocationMap.get(player));
			player.setVelocity(new Vector());
			AOutput.error(event.getPlayer(), "&c&c&lERROR!&7 You must complete the tutorial before leaving spawn!");
		}
	}

	public static NPCCheckpoint getCheckpoint(TutorialObjective objective) {
		for(NPCCheckpoint checkpoint : checkpoints) {
			if(checkpoint.objective == objective) return checkpoint;
		}
		return null;
	}

	public static NPCCheckpoint getCheckpoint(int index) {
		return checkpoints.get(index);
	}
}