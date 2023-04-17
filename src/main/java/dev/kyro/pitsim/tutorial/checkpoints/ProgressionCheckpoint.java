package dev.kyro.pitsim.tutorial.checkpoints;

import dev.kyro.pitsim.adarkzone.progression.MainProgressionPanel;
import dev.kyro.pitsim.adarkzone.progression.MainProgressionUnlock;
import dev.kyro.pitsim.adarkzone.progression.ProgressionManager;
import dev.kyro.pitsim.controllers.MapManager;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import dev.kyro.pitsim.tutorial.NPCCheckpoint;
import dev.kyro.pitsim.tutorial.Tutorial;
import dev.kyro.pitsim.tutorial.TutorialObjective;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ProgressionCheckpoint extends NPCCheckpoint {
	public ProgressionCheckpoint() {
		super(TutorialObjective.PROGRESSION, new Location(MapManager.getDarkzone(),
				190.5, 91, -86.5, 13, 0));
	}

	@Override
	public void onCheckpointEngage(Tutorial tutorial) {
		tutorial.sendMessage("&eHere is the &5&lDarkzone Progression&e Shop!", 0);
		tutorial.sendMessage("&eThis will be your main way of unlocking content here in the &5Darkzone&e.", 60);
		tutorial.sendMessage("&eYou can purchase &6Upgrades &efor &fTainted Souls&e, which are gained from killing &cMobs &eand &4Bosses&e.", 120);
		tutorial.sendMessage("&eGo ahead and &6unlock &ethe first two &6upgrades &elabeled &a&lFREE!&e.", 180);
		tutorial.sendMessage("&eTalk to me again once you've done so.", 240);
		tutorial.sendMessage("&6&nIf you already have these unlocked, talk to me again.", 300);
	}

	@Override
	public void onCheckpointSatisfy(Tutorial tutorial) {
		tutorial.sendMessage("&eNice job!", 0);
		tutorial.sendMessage("&eThis is the main place where you'll be spending your &fSouls&e, so you'll be back here soon!", 60);
	}

	@Override
	public int getEngageDelay() {
		return 300;
	}

	@Override
	public int getSatisfyDelay() {
		return 60;
	}

	@Override
	public boolean canEngage(Tutorial tutorial) {
		return true;
	}

	@Override
	public boolean canSatisfy(Tutorial tutorial) {
		Player player = tutorial.getPlayer();
		PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);

		for(MainProgressionUnlock tutorialUnlock : MainProgressionPanel.tutorialUnlocks) {
			if(!ProgressionManager.isUnlocked(pitPlayer, tutorialUnlock)) return false;
		}

		return true;
	}

	@Override
	public void onCheckPointDisengage(Tutorial tutorial) {

	}
}
