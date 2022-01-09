package dev.kyro.pitsim.tutorial.objects;

import dev.kyro.pitsim.tutorial.MessageManager;
import dev.kyro.pitsim.tutorial.Task;
import dev.kyro.pitsim.tutorial.sequences.InitialSequence;
import dev.kyro.pitsim.tutorial.sequences.PerkSequence;
import dev.kyro.pitsim.tutorial.sequences.VampireSequence;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class Tutorial {
	public Player player;
	public TutorialSequence sequence;


	public Tutorial(Player player) {
		this.player = player;
		this.sequence = new InitialSequence(player, this);
		MessageManager.blockPlayer(player);
		sequence.play();
	}

	public void onTaskComplete(Task task) {
		for(BukkitTask runnable : sequence.getRunnables()) {
			runnable.cancel();
		}
		sequence = null;

		if(task == Task.VIEW_MAP) sequence = new VampireSequence(player, this);
		if(task == Task.EQUIP_VAMPIRE) sequence = new PerkSequence(player, this);

		if(sequence != null) sequence.play();
	}

}
