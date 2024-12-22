package org.gabooj.shop;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

public class ShopListener implements Listener {

    private final JavaPlugin plugin;
    private final Server server;

    public ShopListener(JavaPlugin plugin_to_use, Server server_to_use) {
        plugin = plugin_to_use;
        server = server_to_use;

        server.getPluginManager().registerEvents(this, plugin);
    }

    // Place Sign Event

    // Interact With Sign Event

    // [ Name ]

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock != null && event.getAction() == Action.RIGHT_CLICK_BLOCK && clickedBlock.getType() == Material.OAK_WALL_SIGN) {
            BlockData data = clickedBlock.getBlockData();
            if (data instanceof WallSign) {
                WallSign wallSign = (WallSign) data;
                Sign sign = (Sign) clickedBlock.getState();
                Block attachedTo = clickedBlock.getRelative(wallSign.getFacing().getOppositeFace());
                if (attachedTo.getType() == Material.CHEST) {
                    Chest chest = (Chest) attachedTo.getState();
                    if (ChatColor.stripColor(sign.getLine(0)).startsWith("Selling")) {
                        handleSignInteract(event, sign.getLines(), chest);
                    }
                }
            }
        }
    }

    public void handleSignInteract(PlayerInteractEvent event, String[] lines, Chest chest) {
        event.setCancelled(true);
        Player player = event.getPlayer();


        // Get contents of sign shop
        Material materialSelling = null;
        int quantitySelling = -1;
        Material materialBuying = null;
        int quantityBuying = -1;

        try {
            quantitySelling = Integer.parseInt(ChatColor.stripColor(lines[0]).split(" ")[1]);
            materialSelling = Material.valueOf(ChatColor.stripColor(lines[1]));
            quantityBuying = Integer.parseInt(ChatColor.stripColor(lines[2]).split(" ")[1]);
            materialBuying = Material.valueOf(ChatColor.stripColor(lines[3]));

            if (quantitySelling <= 0 || quantityBuying <= 0 ||
                    quantitySelling > 64 || quantityBuying > 64 ||
                    materialSelling == null || materialBuying == null) {
                throw new Exception();
            }
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Uh-oh! This sign shop was not created properly and cannot sell anything at this moment.");
            return;
        }

        // Ensure player has the contents of the sign shop
        PlayerInventory playerInventory = player.getInventory();
        ItemStack mainhand = playerInventory.getItemInMainHand();
        if (mainhand.getType() != materialBuying || mainhand.getAmount() < quantityBuying) {
            player.sendMessage(ChatColor.RED + "You don't have enough items to trade! In order to trade an item, you must hold the item(s) you wish to trade in your main hand.");
            return;
        }


        // Ensure chest has the contents of the sign shop
        Inventory chestInventory = chest.getInventory();
        if (!chestInventory.contains(materialSelling, quantitySelling)) {
            player.sendMessage(ChatColor.RED + "The shop does not have enough items to sell.");
            return;
        }

        // Ensure chest has enough contents to add
        if (chestInventory.firstEmpty() == -1) {
            player.sendMessage(ChatColor.RED + "The shop is full and cannot sell anymore!");
            return;
        }

        // Ensure player has enough contents to add
        if (playerInventory.firstEmpty() == -1) {
            player.sendMessage(ChatColor.RED + "Your inventory is too full! You must have an open slot in your inventory to trade!");
            return;
        }



        // Make trade by:
        // - Player's Inventory: Remove buying, add selling
        removeFromInventory(playerInventory, materialBuying, quantityBuying);
        addToInventory(playerInventory, materialSelling, quantitySelling);
        // - Chest's Inventory: Add buying, remove selling
        removeFromInventory(chestInventory, materialSelling, quantitySelling);
        addToInventory(chestInventory, materialBuying, quantityBuying);

        player.sendMessage(ChatColor.GREEN + "Successfully traded " + quantityBuying + " " + materialBuying.toString().toLowerCase() + " for " + quantitySelling + " " + materialSelling.toString().toLowerCase() + ".");
    }


    public void addToInventory(Inventory inventory, Material material, int amount) {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null) {
                inventory.setItem(i, new ItemStack(material, amount));
                break;
            } else {
                if (item.getType() == material && item.getAmount() < 64) {
                    int itemAmount = item.getAmount();
                    int amount_to_add = Math.min(64 - itemAmount, amount);
                    amount -= amount_to_add;
                    item.setAmount(itemAmount + amount_to_add);
                }

                // If all items added, stop adding
                if (amount <= 0) {
                    break;
                }
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


    public void handleSignPlace(SignChangeEvent event) {
        Player player = event.getPlayer();

        // Handle first line
        try {
            String[] tokens = ChatColor.stripColor(event.getLine(0)).split(" ");
            if (!tokens[0].equalsIgnoreCase("Selling")) {
                throw new Exception();
            }
            // Ensure that error will appear if second argument is not a number
            Integer.parseInt(tokens[1]);
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Error: the first line in your chest shop is not valid. It must be of the form 'Selling <amount>' like 'Selling 30'.");
            return;
        }

        // Handle second line
        try {
            String line = ChatColor.stripColor(event.getLine(1));
            Material material = Material.matchMaterial(line.toUpperCase());
            if (material == null) {
                player.sendMessage(ChatColor.RED + "Error: the second line in your chest shop is not valid. It must be of the form '<item>' like 'OAK_LOG'.");
                return;
            }
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Error: the second line in your chest shop is not valid. It must be of the form '<item>' like 'OAK_LOG'.");
            return;
        }

        // Handle third line
        try {
            String[] tokens = ChatColor.stripColor(event.getLine(2)).split(" ");
            if (!tokens[0].equalsIgnoreCase("For")) {
                throw new Exception();
            }
            // Ensure that error will appear if second argument is not a number
            Integer.parseInt(tokens[1]);
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Error: the third line in your chest shop is not valid. It must be of the form 'For <amount>' like 'For 30'.");
            return;
        }

        // Handle fourth line
        try {
            String line = ChatColor.stripColor(event.getLine(3));
            Material material = Material.matchMaterial(line.toUpperCase());
            if (material == null) {
                player.sendMessage(ChatColor.RED + "Error: the fourth line in your chest shop is not valid. It must be of the form '<item>' like 'OAK_LOG'.");
                return;
            }
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Error: the fourth line in your chest shop is not valid. It must be of the form '<item>' like 'OAK_LOG'.");
            return;
        }

        player.sendMessage(ChatColor.GREEN + "Chest shop has been created. Make sure to /claim the chunk the chest is in to protect against other players opening the chest!");
        // Reformat and color text on sign
        event.setLine(0, ChatColor.GOLD + "" + ChatColor.BOLD + "Selling " + event.getLine(0).split(" ")[1]);
        event.setLine(1, ChatColor.DARK_BLUE + "" + ChatColor.BOLD + event.getLine(1).toUpperCase());
        event.setLine(2, ChatColor.GOLD + "" + ChatColor.BOLD + "For " + event.getLine(2).split(" ")[1]);
        event.setLine(3, ChatColor.DARK_BLUE + "" + ChatColor.BOLD + event.getLine(3).toUpperCase());
    }

    @EventHandler
    public void onSignPlace(SignChangeEvent event) {
        Block block = event.getBlock();

        if (block.getType() == Material.OAK_WALL_SIGN) {
            if (block.getState() instanceof Sign) {
                BlockData data = block.getBlockData();
                if (data instanceof WallSign) {
                    WallSign wallSign = (WallSign) data;
                    Block adjacentBlock = block.getRelative(wallSign.getFacing().getOppositeFace());
                    if (adjacentBlock.getType() == Material.CHEST && ChatColor.stripColor(event.getLine(0)).startsWith("Selling")) {
                        handleSignPlace(event);
                    }
                }
            }
        }
    }

}
