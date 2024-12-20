package org.gabooj.chat;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.HashMap;

public class ChatManagerIO {

    public static HashMap<String, PlayerChatSettings> readData(JavaPlugin plugin, Server server) {
        File file = new File(plugin.getDataFolder(), "chatSettings.txt");

        HashMap<String, PlayerChatSettings> allSettings = new HashMap<>();

        if (!file.exists()) {
            server.broadcastMessage(ChatColor.RED + "Uh-oh! The server could not find the file for saving everyone's chat settings!");
            return allSettings;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");

                String name = parts[0];
                String prefix = parts[1];
                ChatColor prefix_color = extractChatColor(parts[2]);
                ChatColor prefix_format = extractChatColor(parts[3]);
                String nickname = parts[4];
                ChatColor name_color = extractChatColor(parts[5]);
                ChatColor name_format = extractChatColor(parts[6]);
                ChatColor message_color = extractChatColor(parts[7]);
                ChatColor message_format = extractChatColor(parts[8]);

                PlayerChatSettings setting = new PlayerChatSettings(prefix, nickname, prefix_color, name_color, message_color, prefix_format, name_format, message_format);
                allSettings.put(name, setting);
            }
        } catch (Exception e) {
            server.broadcastMessage(ChatColor.RED + "Uh-oh! The server could not read the file for saving everyone's chat settings!");
        }

        return allSettings;
    }

    public static ChatColor extractChatColor(String input) {
        // Ensure the input is not null or too short
        if (input == null || input.length() < 2) {
            return null;  // Return null if the string is too short or null
        }

        // Check if the string contains the § symbol and has a character following it
        if (input.charAt(0) == '§' && input.length() > 1) {
            char colorCode = input.charAt(1);  // Get the character after the § symbol

            // Get the ChatColor corresponding to the color code
            return ChatColor.getByChar(colorCode);
        }

        // Return null if no valid color code is found
        return null;
    }

    public static void writeData(HashMap<String, PlayerChatSettings> allChatSettings, JavaPlugin plugin, Server server) {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs(); // Creates the folder if it doesn't exist
        }

        File file = new File(plugin.getDataFolder(), "chatSettings.txt");
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (String name : allChatSettings.keySet()) {
                PlayerChatSettings settings = allChatSettings.get(name);

                String str_to_save = name + ";" + settings.prefix + ";" + settings.prefix_color + ";" + settings.prefix_format +
                                            ";" + settings.nickname + ";" + settings.name_color + ";" + settings.prefix_format +
                                            ";" + settings.message_color + ";" + settings.message_format + ";\n";
                writer.write(str_to_save);
            }
        } catch (Exception e) {
            server.broadcastMessage(ChatColor.RED + "Uh-oh! The server could not write to the file that saves everyone's chat settings!");
            e.printStackTrace();
        }
    }


}
