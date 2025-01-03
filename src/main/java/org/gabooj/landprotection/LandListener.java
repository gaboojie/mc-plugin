package org.gabooj.landprotection;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.java.JavaPlugin;

public class LandListener implements Listener {

    private final JavaPlugin plugin;
    private final Server server;

    public LandListener(JavaPlugin plugin, Server server) {
        this.plugin = plugin;
        this.server = server;

        server.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();
        if (!LandManager.doesPlayerHaveAccessToChunkAt(event.getBlock().getLocation(), player.getName())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You do not own and cannot change signs in this land.");
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (LandManager.getOwnerOfChunkAt(event.getLocation()) != 0) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!LandManager.doesPlayerHaveAccessToChunkAt(event.getBlock().getLocation(), player.getName())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You do not own and cannot place blocks in this land.");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!LandManager.doesPlayerHaveAccessToChunkAt(event.getBlock().getLocation(), player.getName())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You do not own and cannot break blocks in this land.");
        }
    }

    @EventHandler
    public void onBlockIgniteEvent(BlockIgniteEvent event) {
        Player player = event.getPlayer();
        if (player != null && !LandManager.doesPlayerHaveAccessToChunkAt(event.getBlock().getLocation(), player.getName())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You do not own and cannot ignite anything within this land.");
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        InventoryType type = event.getInventory().getType();
        if (!canInventoryBeUsedEverywhere(type)) {
            if (!LandManager.doesPlayerHaveAccessToChunkAt(event.getPlayer().getLocation(), event.getPlayer().getName())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "You do not own and cannot open that in this land.");
            }
        }
    }

    public boolean canInventoryBeUsedEverywhere(InventoryType type) {
        return switch (type) {
            case ENCHANTING, CRAFTING, GRINDSTONE, CREATIVE, WORKBENCH, PLAYER, ENDER_CHEST, ANVIL, CARTOGRAPHY, COMPOSTER, JUKEBOX, MERCHANT, LOOM, STONECUTTER, SMITHING, LECTERN -> true;
            default -> false;
        };
    }
}
