package org.gabooj.chat;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.gabooj.misc.MiscCommands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatListener implements Listener {

    private final JavaPlugin plugin;
    private final Server server;

    public ChatListener(JavaPlugin plugin, Server server) {
        this.plugin = plugin;
        this.server = server;

        server.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        PlayerChatSettings chatSettings = ChatManager.getChatSettingsForPlayer(event.getPlayer());

        String toAdd = chatSettings.prefix.isEmpty() ? ""  : " ";

        String message = combineColorWithFormat(chatSettings.prefix_color, chatSettings.prefix_format) + chatSettings.prefix + toAdd
                        + combineColorWithFormat(chatSettings.name_color, chatSettings.name_format) + chatSettings.nickname + ": "
                        + combineColorWithFormat(chatSettings.message_color, chatSettings.message_format) + event.getMessage();

        server.broadcastMessage(message);
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerLogIn(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        HashMap<String, List<String>> playerMail = ChatManager.playerMail;
        List<String> mail = playerMail.getOrDefault(player.getName(), new ArrayList<>());
        if (mail.isEmpty()) {
            player.sendMessage(ChatColor.GOLD + "You don't have any mail.");
        } else {
            player.sendMessage(ChatColor.GOLD + "You have unread mail! Use '/mail read' to read your mail!");
        }
    }

    public String combineColorWithFormat(ChatColor color, ChatColor format) {
        if (format == ChatColor.RESET) {
            return ChatColor.RESET + "" + color;
        } else {
            return color + "" + format;
        }
    }
}
