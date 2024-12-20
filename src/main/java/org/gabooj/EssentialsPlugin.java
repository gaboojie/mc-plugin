package org.gabooj;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.gabooj.chat.ChatManager;
import org.gabooj.difficulty.DifficultyManager;
import org.gabooj.landprotection.LandManager;
import org.gabooj.shop.ShopManager;

public class EssentialsPlugin extends JavaPlugin implements Listener {

    public ChatManager chatManager;
    public LandManager landManager;
    public ShopManager shopManager;
    public DifficultyManager difficultyManager;

    @Override
    public void onEnable() {
        chatManager = new ChatManager(this, getServer());
        chatManager.onEnable();
        landManager = new LandManager(this, getServer());
        landManager.onEnable();
        shopManager = new ShopManager(this, getServer());
        shopManager.onEnable();
        difficultyManager = new DifficultyManager(this, getServer());
        difficultyManager.onEnable();
    }

    @Override
    public void onDisable() {
        chatManager.onDisable();
        landManager.onDisable();
        shopManager.onDisable();
        difficultyManager.onDisable();
    }
}