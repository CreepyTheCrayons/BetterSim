package dev.kyro.pitsim.controllers;

import dev.kyro.pitsim.controllers.objects.Non;
import dev.kyro.pitsim.enums.GameMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class MapManager {

    public static GameMap map = null;

    public static void onStart() {
//        double d = Math.random();
//        if(d < 0.5) map = GameMap.DESERT;
//        else map = GameMap.STARWARS;

        for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.teleport(getPlayerSpawn());
        }
    }

    public static Location playerDesert = new org.bukkit.Location(Bukkit.getWorld("pit"), -108, 86, 194, 48, 3);
    public static Location desertNonSpawn = new Location(Bukkit.getWorld("pit"), -119, 85, 205);
    public static Location desertMid = new Location(Bukkit.getWorld("pit"), -118, 43, 204);
    public static int desertY = 42;

    public static Location playerSnow = new org.bukkit.Location(Bukkit.getWorld("pit"), -99, 46, 707, 0, 0);
    public static Location snowNonSpawn = new Location(Bukkit.getWorld("pit"), -99, 46, 716, -90, 0);
    public static Location snowMid = new Location(Bukkit.getWorld("pit"), -98, 6, 716);
    public static int snowY = 4;

    public static Location spawn = new Location(Bukkit.getWorld("pitsim"), 0.5, 88, 8.5, -180, 0);
    public static Location nonSpawn = new Location(Bukkit.getWorld("pitsim"), 0, 87, 0);
    public static Location mid = new Location(Bukkit.getWorld("pitsim"), 0, 70, 0);
    public static int y = 70;

    public static Location getNonSpawn() {

        Location spawn = nonSpawn;
        spawn = spawn.clone();
        spawn.setX(spawn.getX() + (Math.random() * 8 - 4));
        spawn.setZ(spawn.getZ() + (Math.random() * 8 - 4));
        return spawn;
    }

    public static Location getMid() {

        return mid;
    }

    public static int getY() {
        return y;
    }

    public static Location getPlayerSpawn() {
        return spawn;
    }

    public static void onSwitch() {
        for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.teleport(getPlayerSpawn());
        }

        for(Non non : NonManager.nons) {
            non.respawn();
        }
    }
}
