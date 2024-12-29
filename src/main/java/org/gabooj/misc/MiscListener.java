package org.gabooj.misc;

import org.bukkit.*;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class MiscListener implements Listener {

    private final Server server;
    private final JavaPlugin plugin;
    public static HashMap<Integer, ItemStack> idToShulkerBox = new HashMap<>();
    private int idCounter = 0;

    public MiscListener(Server server, JavaPlugin plugin) {
        this.server = server;
        this.plugin = plugin;

        server.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPlayedBefore()) {
            // Logged in before
            player.sendMessage(ChatColor.GREEN + "Welcome back to the server!");

            for (Player otherPlayer : server.getOnlinePlayers()) {
                if (!otherPlayer.getName().equals(player.getName())) {
                    otherPlayer.sendMessage(ChatColor.GREEN + event.getPlayer().getName() + " has joined the server!");
                }
            }
            event.setJoinMessage("");
        } else {
            // First time logging in
            server.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "A new player has joined!\nWelcome " + player.getName() + " to the server!");
            player.sendMessage(ChatColor.GREEN + "Use /info to get started!");
            if (MiscCommands.warps.containsKey("spawn")) {
                player.teleport(MiscCommands.warps.get("spawn"));
            }
            event.setJoinMessage("");
        }
    }

    @EventHandler
    public void onPlayerFallIntoVoid(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.VOID && event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (player.getInventory().contains(Material.TOTEM_OF_UNDYING, 1)) {
                Location bedLocation = player.getBedSpawnLocation();
                player.setNoDamageTicks(20);
                if (bedLocation != null) {
                    // Teleport the player to their bed location after they die
                    player.teleport(bedLocation);
                    player.sendMessage(ChatColor.GOLD + "Your totem has saved you from the void!");
                } else {
                    // If no bed spawn location, teleport to world spawn
                    World overworld = Bukkit.getServer().getWorld("world");
                    player.teleport(overworld.getSpawnLocation());
                    player.sendMessage(ChatColor.GOLD + "Your totem has saved you from the void!");
                }
                removeFromInventory(player.getInventory(), Material.TOTEM_OF_UNDYING, 1);
                event.setCancelled(true);
            }
        }
    }

    public void removeFromInventory(Inventory inventory, Material material, int amount) {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null && item.getType() == material) {
                // Update quantity of item
                int itemAmount = item.getAmount();
                int amountToRemove = Math.min(amount, itemAmount);
                item.setAmount(itemAmount - amountToRemove);
                amount -= amountToRemove;

                // If item is now empty, remove item
                if (itemAmount <= 0) {
                    inventory.setItem(i, null);
                }

                // If all items removed, stop removing
                if (amount <= 0) {
                    break;
                }
            }
        }
    }

    public void onDisable() {
        for (Player player : server.getOnlinePlayers()) {
            InventoryView view = player.getOpenInventory();
            if (view != null) {
                String title = view.getTitle();
                if (title.startsWith(ChatColor.DARK_PURPLE + "")) {
                    // Get index
                    int beginID = title.indexOf("(");
                    int endID = title.indexOf(")");
                    int id = -1;
                    try {
                        id =  Integer.parseInt(title.substring(beginID+1, endID));
                    } catch (NumberFormatException _) { }

                    // Get item
                    ItemStack item = idToShulkerBox.getOrDefault(id, null);
                    if (item == null) {
                        player.sendMessage(ChatColor.RED + "An error occurred with the Shulker Box opener!");
                        return;
                    }

                    // Update shulker box contents
                    ItemMeta meta = item.getItemMeta();
                    if (meta instanceof BlockStateMeta blockStateMeta) {
                        ShulkerBox shulkerBox = (ShulkerBox) blockStateMeta.getBlockState();
                        Inventory shulkerBoxInventory = shulkerBox.getInventory();

                        shulkerBoxInventory.setContents(view.getTopInventory().getContents());

                        blockStateMeta.setBlockState(shulkerBox);
                        item.setItemMeta(blockStateMeta);
                        player.getInventory().setItemInMainHand(item);
                        idToShulkerBox.remove(id);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Ensure the event is from a player
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();

            // Get the item being moved
            ItemStack clickedItem = event.getCurrentItem();

            // Check if the clicked item is not null and is a Shulker Box
            if (clickedItem != null && clickedItem.getType() == Material.SHULKER_BOX) {
                InventoryView view = player.getOpenInventory();
                String title = view.getTitle();
                if (title.startsWith(ChatColor.DARK_PURPLE + "")) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onShulkerBoxWindowClosed(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inv = event.getInventory();
        InventoryView view = player.getOpenInventory();
        String title = view.getTitle();
        if (title.startsWith(ChatColor.DARK_PURPLE + "")) {
            // Get index
            int beginID = title.indexOf("(");
            int endID = title.indexOf(")");
            int id = -1;
            try {
                id =  Integer.parseInt(title.substring(beginID+1, endID));
            } catch (NumberFormatException _) { }

            // Get item
            ItemStack item = idToShulkerBox.getOrDefault(id, null);
            if (item == null) {
                player.sendMessage(ChatColor.RED + "An error occurred with the Shulker Box opener!");
                return;
            }

            // Update shulker box contents
            ItemMeta meta = item.getItemMeta();
            if (meta instanceof BlockStateMeta blockStateMeta) {
                ShulkerBox shulkerBox = (ShulkerBox) blockStateMeta.getBlockState();
                Inventory shulkerBoxInventory = shulkerBox.getInventory();

                shulkerBoxInventory.setContents(view.getTopInventory().getContents());

                blockStateMeta.setBlockState(shulkerBox);
                item.setItemMeta(blockStateMeta);
                player.getInventory().setItemInMainHand(item);
                idToShulkerBox.remove(id);
            }
        }
    }

    @EventHandler
    public void onRightClickShulkerBox(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (event.getAction() == Action.RIGHT_CLICK_AIR && item != null && item.getType() == Material.SHULKER_BOX) {
            // Handle right click shulker box
            Player player = event.getPlayer();
            Inventory inv = Bukkit.createInventory(null, 27, ChatColor.DARK_PURPLE + "Shulker Box (" + idCounter + ")");

            ItemStack itemInHand = event.getPlayer().getInventory().getItemInMainHand();
            ItemMeta meta = item.getItemMeta();
            if (meta instanceof BlockStateMeta) {
                BlockStateMeta blockStateMeta = (BlockStateMeta) meta;
                ShulkerBox shulkerBox = (ShulkerBox) blockStateMeta.getBlockState();
                Inventory boxInventory = shulkerBox.getInventory();
                inv.setContents(boxInventory.getContents());
                player.openInventory(inv);
                idToShulkerBox.put(idCounter, itemInHand);
            }
            idCounter += 1;
        }
    }

}
