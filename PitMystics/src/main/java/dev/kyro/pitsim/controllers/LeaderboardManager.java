package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PitPlayer;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

public class LeaderboardManager {
    public static Map<String, Integer> finalLevels = new HashMap<>();
    public static TreeMap<String, Integer> finalSorted = new TreeMap<>();

    public static void calculate() {
        Map<String, Integer> levels = new HashMap<>();
        File directory = new File("plugins/PitRemake/playerdata");
        File[] files = directory.listFiles();
        for(File file : files) {
            FileConfiguration data = YamlConfiguration.loadConfiguration(file);
            levels.put(data.getString("name"), data.getInt("level"));
        }

        ValueComparator bvc =  new ValueComparator(levels);
        TreeMap<String,Integer> sorted_map = new TreeMap<String,Integer>(bvc);
        sorted_map.putAll(levels);
        finalSorted.clear();
        finalSorted = sorted_map;
        finalLevels.putAll(sorted_map);

    }

static {
    new BukkitRunnable() {

        @Override
        public void run() {

            for(Player player : Bukkit.getOnlinePlayers()) {
                PitPlayer pitPlayer = PitPlayer.getPitPlayer(player);
                String message = "%luckperms_prefix%";
                if(pitPlayer.megastreak.isOnMega()) {
                    pitPlayer.prefix = pitPlayer.megastreak.getName() + " " + PlaceholderAPI.setPlaceholders(player, message);
                } else {
                    pitPlayer.prefix = "&7[&e" + pitPlayer.playerLevel + "&7] &7" + PlaceholderAPI.setPlaceholders(player, message);
                }
            }

            calculate();
        }
    }.runTaskTimer(PitSim.INSTANCE, 0L, 4000L);
}

}




class ValueComparator implements Comparator<String> {

    Map<String, Integer> base;
    public ValueComparator(Map<String, Integer> base) {
        this.base = base;
    }

    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        }
    }
}