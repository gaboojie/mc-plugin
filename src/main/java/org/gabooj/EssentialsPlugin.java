package org.gabooj;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.gabooj.chat.ChatManager;
import org.gabooj.customitem.CustomItemManager;
import org.gabooj.difficulty.DifficultyManager;
import org.gabooj.landprotection.LandManager;
import org.gabooj.misc.MiscManager;
import org.gabooj.shop.ShopManager;

public class EssentialsPlugin extends JavaPlugin implements Listener {

    // Update 1.21.4

    public ChatManager chatManager;
    public LandManager landManager;
    public ShopManager shopManager;
    public DifficultyManager difficultyManager;
    public MiscManager miscManager;
    public CustomItemManager customItemManager;

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
        miscManager = new MiscManager(this, getServer());
        miscManager.onEnable();
        customItemManager = new CustomItemManager(this, getServer());
        customItemManager.onEnable();
    }

    @Override
    public void onDisable() {
        chatManager.onDisable();
        landManager.onDisable();
        shopManager.onDisable();
        difficultyManager.onDisable();
        miscManager.onDisable();
        customItemManager.onDisable();
    }
}