package org.gabooj.chat;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;

public class ChatManager {

    private final JavaPlugin plugin;
    private final Server server;
    private ChatListener listener;

    public static HashMap<String, PlayerChatSettings> chatSettings = new HashMap<>();

    public static HashMap<String, ChatColor> formatNameToChatColor = new HashMap<>();
    public static HashMap<String, ChatColor> colorNameToChatColor = new HashMap<>();

    public static HashMap<String, List<String>> playerMail = new HashMap<>();

    public ChatManager(JavaPlugin plugin, Server server) {
        initializeFormatAndColors();

        this.plugin = plugin;
        this.server = server;
        this.listener = new ChatListener(plugin, server);

        // Register commands
        ChatCommands commands = new ChatCommands(server, plugin);
        plugin.getCommand("chat").setExecutor(commands);
        plugin.getCommand("mail").setExecutor(commands);
    }

    public void onEnable() {
        chatSettings = ChatManagerIO.readData(plugin, server);
        playerMail = ChatManagerIO.readMailData(plugin, server);
    }

    public void onDisable() {
        ChatManagerIO.writeData(chatSettings, plugin, server);
        ChatManagerIO.writeMailData(playerMail, plugin, server);
    }

    public static PlayerChatSettings getChatSettingsForPlayer(Player player) {
        String name = player.getName();
        if (chatSettings.containsKey(name)) {
            return chatSettings.get(name);
        } else {
            return new PlayerChatSettings("", name, ChatColor.WHITE, ChatColor.WHITE, ChatColor.WHITE, ChatColor.RESET, ChatColor.RESET, ChatColor.RESET);
        }
    }

    public static ChatColor getChatColorByName(String name) {
        return colorNameToChatColor.getOrDefault(name.toUpperCase(), ChatColor.WHITE);
    }

    public static ChatColor getChatFormatByName(String name) {
        return formatNameToChatColor.getOrDefault(name.toUpperCase(), ChatColor.RESET);
    }

    public void initializeFormatAndColors() {
        formatNameToChatColor.put("RESET", ChatColor.RESET);
        formatNameToChatColor.put("BOLD", ChatColor.BOLD);
        formatNameToChatColor.put("ITALIC", ChatColor.ITALIC);
        formatNameToChatColor.put("UNDERLINE", ChatColor.UNDERLINE);
        formatNameToChatColor.put("STRIKETHROUGH", ChatColor.STRIKETHROUGH);
        formatNameToChatColor.put("OBFUSCATED", ChatColor.MAGIC);

        colorNameToChatColor.put("BLACK", ChatColor.BLACK);
        colorNameToChatColor.put("DARK BLUE", ChatColor.DARK_BLUE);
        colorNameToChatColor.put("DARK GREEN", ChatColor.DARK_GREEN);
        colorNameToChatColor.put("DARK AQUA", ChatColor.DARK_AQUA);
        colorNameToChatColor.put("DARK RED", ChatColor.DARK_RED);
        colorNameToChatColor.put("DARK PURPLE", ChatColor.DARK_PURPLE);
        colorNameToChatColor.put("GOLD", ChatColor.GOLD);
        colorNameToChatColor.put("GRAY", ChatColor.GRAY);

        colorNameToChatColor.put("DARK GRAY", ChatColor.DARK_GRAY);
        colorNameToChatColor.put("BLUE", ChatColor.BLUE);
        colorNameToChatColor.put("GREEN", ChatColor.GREEN);
        colorNameToChatColor.put("AQUA", ChatColor.AQUA);
        colorNameToChatColor.put("RED", ChatColor.RED);
        colorNameToChatColor.put("LIGHT PURPLE", ChatColor.LIGHT_PURPLE);
        colorNameToChatColor.put("YELLOW", ChatColor.YELLOW);
        colorNameToChatColor.put("WHITE", ChatColor.WHITE);
    }
}
