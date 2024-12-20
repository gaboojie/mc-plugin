package org.gabooj.shop;

import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

public class ShopManager {

    private final JavaPlugin plugin;
    private final Server server;
    private final ShopManagerListener listener;

    public ShopManager(JavaPlugin plugin_to_use, Server server_to_use) {
        plugin = plugin_to_use;
        server = server_to_use;
        listener = new ShopManagerListener(plugin_to_use, server_to_use);
    }

    public void onEnable() {

    }

    public void onDisable() {

    }
}
