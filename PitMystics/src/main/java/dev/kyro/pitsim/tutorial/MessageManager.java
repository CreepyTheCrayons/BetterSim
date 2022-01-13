package dev.kyro.pitsim.tutorial;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.PitSim;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.List;

public class MessageManager implements Listener {


	public static void sendTutorialMessage(Player player, TutorialMessage message) {

		AOutput.send(player, message.message);
	}



	static {
			ProtocolLibrary.getProtocolManager().addPacketListener(
					new PacketAdapter(PitSim.INSTANCE, PacketType.Play.Server.CHAT) {
						@Override
						public void onPacketSending(PacketEvent event) {

							PacketContainer packet = event.getPacket();
							List<WrappedChatComponent> components = packet.getChatComponents().getValues();

							boolean isBypassed = false;
							for (WrappedChatComponent component : components) {
								for(String identifier : TutorialMessage.getIdentifiers()) {
									if(component.getJson() == null) continue;
									if(component.getJson().contains(identifier)) isBypassed = true;
								}
							}

							if(isBypassed) return;
							if(TutorialManager.tutorials.containsKey(event.getPlayer())) event.setCancelled(true);
						}
					});
	}

}
