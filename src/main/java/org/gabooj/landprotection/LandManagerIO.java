package org.gabooj.landprotection;

import org.bukkit.ChatColor;
import org.gabooj.chat.PlayerChatSettings;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LandManagerIO {

    public static void writePlayerIDs() {
        // String, ID
        File folder = LandManager.plugin.getDataFolder();
        if (!folder.exists()) {
            folder.mkdirs(); // Creates the folder if it doesn't exist
        }

        File file = new File(folder, "playerClaimIDs.txt");
        HashMap<String, Integer> playerIDs = LandManager.playerIDs;
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {

            for (int id = 0; id < playerIDs.size(); id++) {
                for (String name : playerIDs.keySet()) {
                    if (playerIDs.get(name) == id) {
                        String str_to_save = name + ";" + playerIDs.get(name) + "\n";
                        writer.write(str_to_save);
                    }
                }
            }
        } catch (Exception e) {
            LandManager.server.broadcastMessage(ChatColor.RED + "Uh-oh! The server could not write to the file that saves the ID's of player's for land protection purposes!");
            e.printStackTrace();
        }
    }

    public static void writePlayerInfo() {
        // String, ID
        File folder = LandManager.plugin.getDataFolder();
        if (!folder.exists()) {
            folder.mkdirs(); // Creates the folder if it doesn't exist
        }

        File file = new File(folder, "playerClaimInfo.txt");
        List<PlayerLandInfo> playerInfos = LandManager.playerInfo;
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (int id = 0; id < playerInfos.size(); id++) {
                PlayerLandInfo info = playerInfos.get(id);
                StringBuilder str_to_save = new StringBuilder(id + ";" + info.name + ";");
                for (int friend : info.friends) {
                    str_to_save.append(friend).append(";");
                }
                str_to_save.append("\n");
                writer.write(str_to_save.toString());
            }
        } catch (Exception e) {
            LandManager.server.broadcastMessage(ChatColor.RED + "Uh-oh! The server could not write to the file that saves player info for land protection purposes!");
            e.printStackTrace();
        }
    }

    public static void writeClaims() {
        // String, ID
        File folder = LandManager.plugin.getDataFolder();
        if (!folder.exists()) {
            folder.mkdirs(); // Creates the folder if it doesn't exist
        }

        File file = new File(folder, "playerClaims.txt");
        int[][] claims = LandManager.claims;
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (int x = 0; x < LandManager.CHUNK_RADIUS * 2; x++) {
                for (int z = 0; z < LandManager.CHUNK_RADIUS * 2; z++) {
                    int claim_id = claims[x][z];
                    if (claim_id != 0) {
                        String str_to_save = x + ";" + z + ";" + claim_id + ";\n";
                        writer.write(str_to_save);
                    }
                }
            }
        } catch (Exception e) {
            LandManager.server.broadcastMessage(ChatColor.RED + "Uh-oh! The server could not write to the file that saves player claims!");
            e.printStackTrace();
        }
    }

    public static void readPlayerIDs() {
        File file = new File(LandManager.plugin.getDataFolder(), "playerClaimIDs.txt");

        if (!file.exists()) {
            LandManager.server.broadcastMessage(ChatColor.RED + "Uh-oh! The server could not find the file that saves everyone's player IDs for land protection purposes!");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");

                String name = parts[0];
                int id = Integer.parseInt(parts[1]);
                LandManager.playerIDs.put(name, id);
            }
        } catch (Exception e) {
            LandManager.server.broadcastMessage(ChatColor.RED + "Uh-oh! The server could not read the file that saves everyone's player IDs for land protection purposes!");
        }
    }

    public static void readPlayerInfo() {
        File file = new File(LandManager.plugin.getDataFolder(), "playerClaimInfo.txt");

        if (!file.exists()) {
            LandManager.server.broadcastMessage(ChatColor.RED + "Uh-oh! The server could not find the file that saves every player's claim information!");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");

                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                List<Integer> friends = new ArrayList<>();
                for (int i = 2; i < parts.length; i++) {
                    String playerID = parts[i];
                    if (playerID.isEmpty()) continue;
                    friends.add(Integer.parseInt(playerID));
                }

                PlayerLandInfo info = new PlayerLandInfo(name, friends);
                LandManager.playerInfo.add(info);
            }
        } catch (Exception e) {
            LandManager.server.broadcastMessage(ChatColor.RED + "Uh-oh! The server could not find the file that saves every player's claim information!");
        }
    }

    public static void readClaims() {
        File file = new File(LandManager.plugin.getDataFolder(), "playerClaims.txt");

        if (!file.exists()) {
            LandManager.server.broadcastMessage(ChatColor.RED + "Uh-oh! The server could not find the file that saves the world claim information!");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");


                int x = Integer.parseInt(parts[0]);
                int z = Integer.parseInt(parts[1]);
                int owner_id = Integer.parseInt(parts[2]);

                LandManager.claims[x][z] = owner_id;
            }
        } catch (Exception e) {
            LandManager.server.broadcastMessage(ChatColor.RED + "Uh-oh! The server could not find the file that saves the world claim information!");
        }
    }

}
