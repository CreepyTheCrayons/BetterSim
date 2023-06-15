package net.pitsim.pitsim.npcs;

import net.pitsim.pitsim.controllers.MapManager;
import net.pitsim.pitsim.controllers.objects.PitNPC;
import net.pitsim.pitsim.controllers.objects.PitPlayer;
import net.pitsim.pitsim.inventories.help.KitGUI;
import net.pitsim.pitsim.tutorial.TutorialObjective;
import net.pitsim.pitsim.tutorial.Tutorial;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;

import java.util.List;

public class KitNPC extends PitNPC {

	public KitNPC(List<World> worlds) {
		super(worlds);
	}

	@Override
	public Location getRawLocation() {
		return null;
	}

	@Override
	public Location getFinalLocation(World world) {
		return MapManager.currentMap.getKitsNPCSpawn();
	}

	@Override
	public void createNPC(Location location) {
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		NPC npc = registry.createNPC(EntityType.RABBIT, " ");
		npc.spawn(location);
		Rabbit rabbit = (Rabbit) npc.getEntity();
		rabbit.setRabbitType(Rabbit.Type.WHITE);
		npc.getEntity().setCustomNameVisible(false);
		npcs.add(npc);
	}

	@Override
	public void onClick(Player player) {

		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
		Tutorial tutorial = pitPlayer.overworldTutorial;

		if(tutorial.isInObjective) return;
		if(tutorial.isActive() && !tutorial.isCompleted(TutorialObjective.KITS)) {

			tutorial.sendMessage("&c&lKITS: &eIf you don't know what to use, you'll be nothing but a sitting duck...", 0);
			tutorial.sendMessage("&c&lKITS: &eLuckily, I have the essentials you'll need to play like a pro!", 20 * 4);
			tutorial.sendMessage("&c&lKITS: &eClick on me again to access the kits. Best to take 1 of each!", 20 * 8);
			tutorial.completeObjective(TutorialObjective.KITS, 20 * 12);

			return;
		}

		KitGUI kitGUI = new KitGUI(player);
		kitGUI.kitPanel.openPanel(kitGUI.kitPanel);
	}
}
