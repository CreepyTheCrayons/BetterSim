package dev.kyro.pitsim.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class ChangeMapCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

//        if(sender instanceof Player && !sender.hasPermission("pitsim.changemap")) return false;
//
//        if(MapManager.map == GameMap.DESERT) MapManager.map = GameMap.STARWARS;
//        else MapManager.map = GameMap.DESERT;
//        MapManager.onSwitch();

        return false;
    }


}
