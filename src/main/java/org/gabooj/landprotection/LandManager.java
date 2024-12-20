package org.gabooj.landprotection;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.gabooj.chat.ChatManagerCommands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LandManager {

    // IO
    // Listen to events (Entities breaking blocks, fire tick spread?, inventory openings, block place/break, interact)
    // Friends
    // /map to show the map to people
    // /claim and /unclaim
    // /setclaim 0 for wilderness
    // /friend add/remove <name>
    // Set first 3 chunks around origin to spawn chunks
    // Can only claim in the first 1000 blocks;

    public static final int CHUNK_RADIUS = 625;
    public final LandManagerListener listener;
    public static Server server = null;
    public static JavaPlugin plugin = null;

    public static HashMap<String, Integer> playerIDs = new HashMap<>();
    public static List<PlayerLandInfo> playerInfo = new ArrayList<>();
    public static int[][] claims = new int[CHUNK_RADIUS*2][CHUNK_RADIUS*2];

    public LandManager(JavaPlugin plugin_to_use, Server server_to_use) {
        // Register commands
        server = server_to_use;
        plugin = plugin_to_use;
        LandManagerCommands executor = new LandManagerCommands(server, plugin);

        plugin.getCommand("claim").setExecutor(executor);
        plugin.getCommand("unclaim").setExecutor(executor);
        plugin.getCommand("setclaim").setExecutor(executor);
        plugin.getCommand("map").setExecutor(executor);
        plugin.getCommand("friend").setExecutor(executor);

        this.listener = new LandManagerListener(plugin, server);
    }

    public static int getPlayerID(String name) {
        if (playerIDs.containsKey(name)) {
            return playerIDs.get(name);
        } else {
            return addPlayer(name);
        }
    }

    public static int addPlayer(String name) {
        int id = playerInfo.size();
        playerInfo.add(new PlayerLandInfo(name, new ArrayList<>()));
        playerIDs.put(name, id);
        return id;
    }

    public void onEnable() {
        LandManagerIO.readPlayerIDs();
        LandManagerIO.readClaims();
        LandManagerIO.readPlayerInfo();

        // Set Nature and Server values
        if (playerInfo.size() > 0) {
            playerInfo.set(0, new PlayerLandInfo("Nature", new ArrayList<>()));
        } else {
            playerInfo.add(new PlayerLandInfo("Nature", new ArrayList<>()));
        }
        if (playerInfo.size() > 1) {
            playerInfo.set(1, new PlayerLandInfo("Server", new ArrayList<>()));
        } else {
            playerInfo.add(new PlayerLandInfo("Server", new ArrayList<>()));
        }
        playerIDs.put("Nature", 0);
        playerIDs.put("Server", 1);
    }

    public void onDisable() {
        LandManagerIO.writePlayerIDs();
        LandManagerIO.writeClaims();
        LandManagerIO.writePlayerInfo();
    }

    public static boolean doesPlayerHaveAccessToChunkAt(Location loc, String name) {
        int chunkID = getOwnerOfChunkAt(loc);

        // Nature should have full access
        if (chunkID == 0) return true;
        int playerID = getPlayerID(name);

        // Player is the owner of the chunk
        return chunkID == playerID || playerInfo.get(chunkID).friends.contains(playerID);
    }

    public static int getOwnerOfChunkAt(Location loc) {
        if (isWithinClaimBorders(loc)) {
            return getClaimOwner(loc.getChunk().getX(), loc.getChunk().getZ());
        } else {
            return 0;
        }
    }

    public static boolean isWithinClaimBorders(Location loc) {
        return loc.getWorld().getEnvironment() == World.Environment.NORMAL &&
                -624 <= loc.getChunk().getX() && loc.getChunk().getX() <= 625 &&
                -624 <= loc.getChunk().getZ() && loc.getChunk().getZ() <= 625;
    }

    public static boolean isWithinBordersXZ(int chunkX, int chunkZ) {
        return -624 <= chunkX && chunkX <= 625 &&
                -624 <= chunkZ && chunkZ <= 625;
    }

    public static void setClaimOwner(int chunkX, int chunkZ, int owner_id) {
        claims[chunkX+624][chunkZ+624] = owner_id;
    }

    public static int getClaimOwner(int chunkX, int chunkZ) {
        return claims[chunkX+624][chunkZ+624];
    }

}
