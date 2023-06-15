package net.pitsim.spigot.controllers;

import com.xxmicloxx.NoteBlockAPI.NoteBlockAPI;
import com.xxmicloxx.NoteBlockAPI.songplayer.EntitySongPlayer;
import dev.kyro.arcticapi.misc.AOutput;
import net.pitsim.spigot.PitSim;
import net.pitsim.spigot.events.PitQuitEvent;
import net.pitsim.spigot.misc.Misc;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class MusicManager implements Listener {

	public static List<EntitySongPlayer> songs = new ArrayList<>();

	static {
		new BukkitRunnable() {
			@Override
			public void run() {
				int songCount = NoteBlockAPI.getAPI().getPlayingSongCount();
				AOutput.log("NOTEBLOCK DEBUG: " + songCount + " playing song" + Misc.s(songCount));
			}
		}.runTaskTimer(PitSim.INSTANCE, 0, 20 * 60 * 5);
//		new BukkitRunnable() {
//			@Override
//			public void run() {
//				for(Player player : Bukkit.getOnlinePlayers()) {
//					if(!MapManager.inDarkzone(player) || CutsceneManager.cutscenePlayers.containsKey(player) ||
//							NoteBlockAPI.isReceivingSong(player) || PitPlayer.getPitPlayer(player).musicDisabled)
//						continue;
//					File file = new File("plugins/NoteBlockAPI/Effects/darkzone.nbs");
//					Song song = NBSDecoder.parse(file);
//					EntitySongPlayer esp = new EntitySongPlayer(song);
//					esp.setEntity(player);
//					esp.setDistance(16);
//					esp.setRepeatMode(RepeatMode.ONE);
//					esp.addPlayer(player);
//					esp.setAutoDestroy(true);
//					esp.setPlaying(true);
//					songs.add(esp);
//				}
//			}
//		}.runTaskTimer(PitSim.INSTANCE, 20, 20);
	}

	@EventHandler
	public static void onQuit(PitQuitEvent event) {
		Player player = event.getPlayer();
		stopPlaying(player);
	}

	public static void stopPlaying(Player player) {
		if(!PlayerManager.isRealPlayer(player)) return;
		NoteBlockAPI.stopPlaying(player);
		EntitySongPlayer toRemove = null;
		for(EntitySongPlayer song : songs) {
			if(song.getEntity() == player) {
				toRemove = song;
			}
		}
		if(toRemove != null) songs.remove(toRemove);
	}
}
