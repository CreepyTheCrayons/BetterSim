package dev.kyro.pitsim.tutorial.sequences;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.LevelManager;
import dev.kyro.pitsim.tutorial.MessageManager;
import dev.kyro.pitsim.tutorial.Task;
import dev.kyro.pitsim.tutorial.TutorialMessage;
import dev.kyro.pitsim.tutorial.objects.Tutorial;
import dev.kyro.pitsim.tutorial.objects.TutorialSequence;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class PrestigeSequence extends TutorialSequence {
	public Player player;
	public Tutorial tutorial;
	public int waitTime = 0;
	public List<BukkitTask> runnableList = new ArrayList<>();

	public PrestigeSequence(Player player, Tutorial tutorial) {
		super(player, tutorial, Task.PRESTIGE);
		this.player = player;
		this.tutorial = tutorial;
		//test
	}

	@Override
	public List<BukkitTask> getRunnables() {
		return runnableList;
	}

	@Override
	public void play() {
		disableNon();
		sendMessage(TutorialMessage.PRESTIGE1);
		wait(5);
		sendMessage(TutorialMessage.PRESTIGE2);
		wait(5);
		sendMessage(TutorialMessage.PRESTIGE3);
		wait(5);
		sendMessage(TutorialMessage.PRESTIGE4);
		wait(5);
		sendMessage(TutorialMessage.PRESTIGE5);
		wait(5);
		sendMessage(TutorialMessage.PRESTIGE6);
		wait(5);
		sendMessage(TutorialMessage.PRESTIGE7);
		wait(2);
		setLevel();
		wait(3);
		spawnPrestigeVillager();
		sendMessage(TutorialMessage.PRESTIGE8);

	}

	public void wait(int seconds) {
		waitTime = waitTime + seconds;
	}

	public void sendMessage(TutorialMessage message) {
		BukkitTask runnable = new BukkitRunnable() {
			@Override
			public void run() {
				MessageManager.sendTutorialMessage(player, message);
			}
		}.runTaskLater(PitSim.INSTANCE, 20L * waitTime);
		runnableList.add(runnable);
	}

	public void completeTask(Task task) {
		BukkitTask runnable = new BukkitRunnable() {
			@Override
			public void run() {
				tutorial.onTaskComplete(task);
			}
		}.runTaskLater(PitSim.INSTANCE, 20L * waitTime);
		runnableList.add(runnable);
	}

	public void disableNon() {
		BukkitTask runnable = new BukkitRunnable() {
			@Override
			public void run() {
				for(NPC non : tutorial.nons) {
					non.setProtected(true);
				}
			}
		}.runTaskLater(PitSim.INSTANCE, 20L * waitTime);
		runnableList.add(runnable);
	}

	public void spawnPrestigeVillager() {
		BukkitTask runnable = new BukkitRunnable() {
			@Override
			public void run() {
				for(NPC non : tutorial.nons) {
					non.destroy();
				}
				tutorial.spawnPrestigeNPC();
			}
		}.runTaskLater(PitSim.INSTANCE, 20L * waitTime);
		runnableList.add(runnable);
	}

	public void setLevel() {
		BukkitTask runnable = new BukkitRunnable() {
			@Override
			public void run() {
				LevelManager.addXP(player, 1000000);
				LevelManager.addGoldReq(player, 20000);
			}
		}.runTaskLater(PitSim.INSTANCE, 20L * waitTime);
		runnableList.add(runnable);
	}

}
