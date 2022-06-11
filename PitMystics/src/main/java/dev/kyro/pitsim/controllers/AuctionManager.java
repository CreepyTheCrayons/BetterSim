package dev.kyro.pitsim.controllers;

import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.kyro.arcticapi.data.AConfig;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.AuctionItem;
import dev.kyro.pitsim.enums.ItemType;
import dev.kyro.pitsim.misc.Misc;
import dev.kyro.pitsim.misc.Sounds;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class AuctionManager implements Listener {

    public static AuctionItem[] auctionItems = new AuctionItem[3];

    public static Location spawnLoc = new Location(MapManager.getDarkzone(), 243.5, 82, -282.5, 180, 0);

    public static int minutes = 60 * 24;

    static {
        new BukkitRunnable() {
            @Override
            public void run() {

                boolean showItems = false;

                for (int i = 0; i < auctionItems.length; i++) {
                    AuctionItem item = auctionItems[i];

                    if(System.currentTimeMillis() - item.initTime > minutes * 60000L) {
                        item.endAuction();
                        System.out.println(i + " Auction ended");
                        auctionItems[i] = new AuctionItem(generateItem(), 0, i, 0, null);
                        showItems = true;
                    }
                }

                if(showItems) AuctionDisplays.showItems();

            }
        }.runTaskTimer(PitSim.INSTANCE, 20, 20 * 60);
    }

    public static void onStart() {

        for (int i = 0; i < 3; i++) {
            if(AConfig.getInt("auctions.auction" + i + ".item") == 0) {
                continue;
            }

            int item = AConfig.getInt("auctions.auction" + i + ".item");
            int itemData = AConfig.getInt("auctions.auction" + i + ".itemdata");
            long startTime = (long) AConfig.getDouble("auctions.auction" + i + ".start");

            List<String> bids = AConfig.getStringList("auctions.auction" + i + ".bids");
            Map<UUID, Integer> bidMap = new LinkedHashMap<>();
            for (String bid : bids) {
                String[] split = bid.split(":");
                bidMap.put(UUID.fromString(split[0]), Integer.parseInt(split[1]));
            }

            System.out.println("Loaded auction " + i);
            System.out.println(item);
            auctionItems[i] = new AuctionItem(ItemType.getItemType(item), itemData, i, startTime, bidMap);
        }

        for (int i = 0; i < auctionItems.length; i++) {
            if(auctionItems[i] != null) continue;

            auctionItems[i] = new AuctionItem(generateItem(), 0, i, 0, null);
        }

        AuctionDisplays.showItems();
    }

    public static ItemType generateItem() {
        double random = Math.random() * 100;

        List<ItemType> itemTypes = Arrays.asList(ItemType.values());
        Collections.shuffle(itemTypes);

        for (ItemType itemType : itemTypes) {
            if(itemType.chance > random) return itemType;
        }

        return itemTypes.get(0);
    }

    List<Player> anim = new ArrayList<>();

    @EventHandler
    public void onMove(PlayerMoveEvent event) {

        Player player = event.getPlayer();
        if(anim.contains(player)) return;
        RegionContainer container = WorldGuardPlugin.inst().getRegionContainer();
        RegionManager regions = container.get(event.getTo().getWorld());
        assert regions != null;
        ApplicableRegionSet set = regions.getApplicableRegions((BukkitUtil.toVector(event.getTo())));

        for(ProtectedRegion region : set) {
            if(region.getId().equals("darkauctionenterance")) {

                anim.add(player);
                Misc.applyPotionEffect(player, PotionEffectType.BLINDNESS, 60, 99, false, false);
                Misc.applyPotionEffect(player, PotionEffectType.CONFUSION, 60, 5, false, false);
                Sounds.MANA.play(player);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.teleport(spawnLoc);
                        anim.remove(player);
                    }
                }.runTaskLater(PitSim.INSTANCE, 20);
            }
        }
    }


}
