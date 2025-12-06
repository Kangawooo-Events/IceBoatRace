package org.limelight.iceBoatRace.general;

import com.destroystokyo.paper.Title;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class MessageHandler {
    public static void sendActionbar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }

    public static void sendTitlebar(Player player, String message, String message2) {
        player.sendTitle(message,message2,20,40,20);
    }
}
