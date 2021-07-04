package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.misc.ASound;
import dev.kyro.pitsim.PitSim;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class EnderChestManager implements Listener {


    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Block block = event.getPlayer().getTargetBlock((HashSet<Byte>) null, 5);
        if(block.getType().equals(Material.ENDER_CHEST)) {

            new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.dispatchCommand(event.getPlayer(), "pv 1");
                    ASound.play(event.getPlayer(), Sound.CHEST_OPEN);
                }
            }.runTaskLater(PitSim.INSTANCE, 1L);

        }
    }


    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if(event.getMessage().contains("pv") || event.getMessage().contains("playervault")) {
            Block block = event.getPlayer().getTargetBlock((HashSet<Byte>) null, 5);
            if(!block.getType().equals(Material.ENDER_CHEST)) {
                event.getPlayer().sendMessage("Unknown command. Type \"/help\" for help.");
                event.setCancelled(true);
            }
        }
    }
}
