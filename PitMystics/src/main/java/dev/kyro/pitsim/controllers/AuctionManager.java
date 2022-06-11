package dev.kyro.pitsim.controllers;

import dev.kyro.arcticapi.data.AConfig;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.AuctionItem;
import dev.kyro.pitsim.enums.ItemType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class AuctionManager {

    public static AuctionItem[] auctionItems = new AuctionItem[3];

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


}
