package org.gabooj.difficulty;

import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

public class DifficultyManager {

    private final JavaPlugin plugin;
    private final Server server;

    private final DifficultyManagerListener listener;

    public DifficultyManager(JavaPlugin plugin, Server server) {
        this.plugin = plugin;
        this.server = server;
        this.listener = new DifficultyManagerListener(plugin, server);
    }

    public void onEnable() {

    }

    public void onDisable() {

    }

}
