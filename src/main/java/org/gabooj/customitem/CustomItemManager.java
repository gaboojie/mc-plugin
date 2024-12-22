package org.gabooj.customitem;

import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomItemManager {

    private final JavaPlugin plugin;
    private final Server server;
    private final CustomItemCrafting crafting;
    private final CustomItemListener listener;

    public CustomItemManager(JavaPlugin plugin, Server server) {
        this.plugin = plugin;
        this.server = server;
        this.crafting = new CustomItemCrafting(plugin, server);

        // Do this after custom item crafting has been created
        this.listener = new CustomItemListener(plugin, server);
    }

    public void onEnable() {

    }

    public void onDisable() {

    }


}
