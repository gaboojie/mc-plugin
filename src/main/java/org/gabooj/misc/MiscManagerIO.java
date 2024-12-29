package org.gabooj.misc;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.gabooj.landprotection.LandManager;
import org.gabooj.landprotection.PlayerLandInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MiscManagerIO {

    public static void readWarpInfo(JavaPlugin plugin, Server server) {
        File file = new File(LandManager.plugin.getDataFolder(), "warps.txt");

        if (!file.exists()) {
            server.broadcastMessage(ChatColor.RED + "Uh-oh! The server could not find the file that saves the server's warp information!");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");

                String name = parts[0];
                String worldName = parts[1];
                double x = Double.parseDouble(parts[2]);
                double y = Double.parseDouble(parts[3]);
                double z = Double.parseDouble(parts[4]);
                float yaw = Float.parseFloat(parts[5]);
                float pitch = Float.parseFloat(parts[6]);
                World world = server.getWorld(worldName);
                Location loc = new Location(world, x, y, z, yaw, pitch);
                MiscCommands.warps.put(name, loc);
            }
        } catch (Exception e) {
            server.broadcastMessage(ChatColor.RED + "Uh-oh! The server could not find the file that saves the server's warp information!");
            e.printStackTrace();
        }
    }

    public static void writeWarpInfo(JavaPlugin plugin, Server server) {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        File file = new File(plugin.getDataFolder(), "warps.txt");
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (String name : MiscCommands.warps.keySet()) {
                String str_to_save = name;
                Location loc = MiscCommands.warps.get(name);

                str_to_save += ";" + loc.getWorld().getName();
                str_to_save += ";" + loc.getX();
                str_to_save += ";" + loc.getY();
                str_to_save += ";" + loc.getZ();
                str_to_save += ";" + loc.getYaw();
                str_to_save += ";" + loc.getPitch();
                writer.write(str_to_save + "\n");
            }
        } catch (Exception e) {
            server.broadcastMessage(ChatColor.RED + "Uh-oh! The server could not write to the file that saves the server's warp information!");
            e.printStackTrace();
        }
    }

}
