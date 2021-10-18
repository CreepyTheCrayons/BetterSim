package dev.kyro.pitsim.controllers.objects;

import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.Cooldown;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class HelmetAbility implements Listener {

    public static List<HelmetAbility> helmetAbilities = new ArrayList<>();
    public static List<UUID> toggledHelmets = new ArrayList<>();

    public Player player;
    public String name;
    public String refName;
    public boolean isTogglable;
    public int slot;

    public HelmetAbility(Player player, String name, String refName, boolean isTogglable, int slot) {
        this.player = player;
        this.name = name;
        this.refName = refName;
        this.isTogglable = isTogglable;
        this.slot = slot;
    }

    public void onActivate() { }
    public void onDeactivate() { }
    public void onProc() { }
    public abstract boolean isActive();

    public abstract List<String> getDescription();
    public abstract ItemStack getDisplayItem();

    public static HelmetAbility getAbility(String refName) {
        for(HelmetAbility helmetAbility : helmetAbilities) {
            if(helmetAbility.refName.equals(refName)) return helmetAbility;
        }
        return null;
    }


    public static void registerHelmetAbility(HelmetAbility helmetAbility) {

        helmetAbilities.add(helmetAbility);
        PitSim.INSTANCE.getServer().getPluginManager().registerEvents(helmetAbility, PitSim.INSTANCE);
    }

    public Map<UUID, Cooldown> cooldowns = new HashMap<>();
    public Cooldown getCooldown(Player player, int time) {

        if(cooldowns.containsKey(player.getUniqueId())) {
            Cooldown cooldown = cooldowns.get(player.getUniqueId());
            cooldown.initialTime = time;
            return cooldown;
        }

        Cooldown cooldown = new Cooldown(time);
        cooldown.ticksLeft = 0;
        cooldowns.put(player.getUniqueId(), cooldown);
        return cooldown;
    }

}
