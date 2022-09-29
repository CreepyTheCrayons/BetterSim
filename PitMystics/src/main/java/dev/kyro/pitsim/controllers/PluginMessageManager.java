package dev.kyro.pitsim.controllers;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.kyro.arcticapi.data.AConfig;
import dev.kyro.pitsim.PitSim;
import dev.kyro.pitsim.controllers.objects.PluginMessage;
import dev.kyro.pitsim.events.MessageEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.*;

public class PluginMessageManager implements PluginMessageListener {
    public static void sendMessage(PluginMessage message) {
//
//        String id = PitSim.INSTANCE.getConfig().getString("server-ID");
//        if(id == null) return;

        System.out.println(1);


        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward"); // So BungeeCord knows to forward it
        out.writeUTF("ALL");
        out.writeUTF("PitSim");

        ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
        DataOutputStream msgout = new DataOutputStream(msgbytes);
        try {
            msgout.writeUTF(message.messageID.toString());
            msgout.writeUTF(message.responseID.toString());
            msgout.writeUTF(AConfig.getString("server"));

            msgout.writeInt(message.getStrings().size());
            msgout.writeInt(message.getIntegers().size());
            msgout.writeInt(message.getBooleans().size());

            for(String string : message.getStrings()) {
                msgout.writeUTF(string);
            }

            for(int integer : message.getIntegers()) {
                msgout.writeInt(integer);
            }

            for(Boolean bool : message.getBooleans()) {
                msgout.writeBoolean(bool);
            }

        } catch(IOException exception) {
            exception.printStackTrace();
        }

        out.writeShort(msgbytes.toByteArray().length);
        out.write(msgbytes.toByteArray());

        Player p = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
        assert p != null;
        p.sendPluginMessage(PitSim.INSTANCE, "BungeeCord", out.toByteArray());
        System.out.println(2);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        try {
            if (!channel.equals("BungeeCord")) {
                return;
            }
            ByteArrayDataInput in = ByteStreams.newDataInput(message);
            String type = in.readUTF();
            String server = in.readUTF();
            String subChannel = in.readUTF();

            if(!subChannel.equals("PitSim")) return;

            short len = in.readShort();
            byte[] msgbytes = new byte[len];
            in.readFully(msgbytes);
            DataInputStream subDIS = new DataInputStream(new ByteArrayInputStream(msgbytes));

            PluginMessage pluginMessage = new PluginMessage(subDIS);
            PitSim.INSTANCE.getServer().getPluginManager().callEvent(new MessageEvent(pluginMessage, subChannel));

        } catch(Exception ignored) { }
    }
}
