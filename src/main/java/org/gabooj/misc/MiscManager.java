package org.gabooj.misc;

import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

public class MiscManager {

    private final Server server;
    private final JavaPlugin plugin;
    private final MiscListener listener;

    public MiscManager(JavaPlugin plugin, Server server) {
        this.server = server;
        this.plugin = plugin;
        this.listener = new MiscListener(server, plugin);

        MiscCommands executor = new MiscCommands(plugin, server);

        plugin.getCommand("info").setExecutor(executor);
        plugin.getCommand("rules").setExecutor(executor);
        plugin.getCommand("commands").setExecutor(executor);
    }

    public void onEnable() {

    }

    public void onDisable() {
        listener.onDisable();
    }

}
