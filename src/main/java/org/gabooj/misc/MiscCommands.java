package org.gabooj.misc;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MiscCommands implements CommandExecutor {

    public static final HashMap<Player, List<Player>> teleports = new HashMap<>();
    public static final HashMap<String, Location> warps = new HashMap<>();

    private final JavaPlugin plugin;
    private final Server server;

    public MiscCommands(JavaPlugin plugin, Server server) {
        this.plugin = plugin;
        this.server = server;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "You must be a player to issue this command.");
            return true;
        }
        Player player = (Player) commandSender;

        if (label.equalsIgnoreCase("info")) {
            infoCommand(player);
        } else if (label.equalsIgnoreCase("rules")) {
            rulesCommand(player);
        } else if (label.equalsIgnoreCase("commands")) {
            showCommandsCommand(player);
        } else if (label.equalsIgnoreCase("tpa")) {
            tpaCommand(player, args);
        } else if (label.equalsIgnoreCase("home")) {
            homeCommand(player);
        } else if (label.equalsIgnoreCase("showhand")) {
            showHandCommand(player);
        } else if (label.equalsIgnoreCase("warp")) {
            warpCommand(player, args);
        }
        return true;
    }

    public void warpCommand(Player player, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("list")) {
            if (!warps.isEmpty()) {
                String warpsStr = ChatColor.GOLD + "Warps: ";
                for (String str : warps.keySet()) {
                    warpsStr += str + ", ";
                }
                warpsStr = warpsStr.substring(0, warpsStr.length()-2);
                player.sendMessage(warpsStr);
                player.sendMessage(ChatColor.GOLD + "Use '/warp <name>' to teleport to that warp's location.");
            } else {
                player.sendMessage(ChatColor.RED + "Unfortunately, no warps exist yet.");
            }
        } else if (args[0].equalsIgnoreCase("add")) {
            if (!player.isOp()) {
                player.sendMessage(ChatColor.RED + "You must be an admin to use this command.");
                return;
            }

            if (args.length > 1) {
                String warpName = args[1];
                // Does warp exist
                for (String name : warps.keySet()) {
                    if (name.equalsIgnoreCase(warpName)) {
                        player.sendMessage(ChatColor.RED + "That warp name already exists!");
                        return;
                    }
                }

                // Add warp
                player.sendMessage(ChatColor.GOLD + "'" + warpName + "' was added as a warp location.");
                warps.put(warpName, player.getLocation());
            } else {
                player.sendMessage(ChatColor.RED + "To add a warp, use /warp add <warp name>.");
            }
        } else if (args[0].equalsIgnoreCase("remove")) {
            if (!player.isOp()) {
                player.sendMessage(ChatColor.RED + "You must be an admin to use this command.");
                return;
            }

            if (args.length > 1) {
                String warpName = args[1];

                if (warps.containsKey(warpName)) {
                    player.sendMessage(ChatColor.GOLD + "'" + warpName + "' was removed as a warp location.");
                    warps.remove(warpName);
                } else {
                    player.sendMessage(ChatColor.RED + "That warp name does not exist!");
                }
            } else {
                player.sendMessage(ChatColor.RED + "To add a warp, use /warp add <warp name>.");
            }
        } else {
            String warpName = args[0];
            if (warps.containsKey(warpName)) {
                Location loc = warps.get(warpName);
                player.teleport(loc);
                player.sendMessage(ChatColor.GOLD + "Teleported to " + warpName + ".");
            } else {
                player.sendMessage(ChatColor.RED + "That warp does not exist!");
            }
        }
    }


    public void showHandCommand(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "You must hold an item to show!");
            return;
        }
        String itemName = makeSingular(formatString(item.getType().toString()), (item.getAmount() == 1));
        String toDisplay = ChatColor.GOLD + player.getName() + " shows their " + item.getAmount() + " " + itemName;

        // Show name
        ItemMeta meta = item.getItemMeta();
        if (meta.hasDisplayName()) {
            toDisplay += ", " + ChatColor.ITALIC + "'" + meta.getDisplayName() + "'" + ChatColor.RESET + ChatColor.GOLD;
        }
        toDisplay += ".";

        // Show enchants
        if (meta.hasEnchants()) {
            toDisplay += ChatColor.LIGHT_PURPLE + "\nWith Enchant(s): ";
            for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
                NamespacedKey enchantKey = entry.getKey().getKey();
                String enchantName = enchantKey.toString();
                if (enchantName.startsWith("minecraft:")) {
                    enchantName = enchantName.substring(10);
                }
                toDisplay += formatString(enchantName) + " " + entry.getValue() + ", ";
            }
            toDisplay = toDisplay.substring(0, toDisplay.length()-2);
        }
        server.broadcastMessage(toDisplay);
    }

    public String makeSingular(String str, boolean makeSingular) {
        if (makeSingular && str.endsWith("s")) {
            return str.substring(0, str.length()-1);
        } else return str;
    }

    public String formatString(String str) {
        boolean capitalizeNext = true;
        StringBuilder toReturn = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (capitalizeNext) {
                toReturn.append((c + "").toUpperCase());
                capitalizeNext = false;
                continue;
            }

            if (c == '_') {
                capitalizeNext = true;
                toReturn.append(" ");
            } else {
                toReturn.append((c + "").toLowerCase());
            }
        }
        return toReturn.toString();
    }

    public void homeCommand(Player player) {
        Location bedLoc = player.getBedSpawnLocation();
        if (bedLoc == null) {
            player.sendMessage(ChatColor.RED + "You do not have a bed to teleport to! Claim a bed and use /home to teleport back to it!");
        } else {
            player.sendMessage(ChatColor.GOLD + "Teleported to your home.");
            player.teleport(bedLoc);
        }
    }

    public void tpaCommand(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(ChatColor.GOLD + "Use /tpa <name> to request to teleport to <name>. To accept a teleport request, use /tpa accept. To deny a teleport request, use /tpa deny.");
            return;
        }

        String arg = args[0];

        if (arg.equalsIgnoreCase("accept")) {
            List<Player> requests = teleports.getOrDefault(player, new ArrayList<>());
            if (requests.isEmpty()) {
                player.sendMessage(ChatColor.RED + "You do not have any active teleport requests.");
            } else {
                if (args.length > 1) {
                    // Handle /tpa accept <name>
                    String toPlayer = args[1];

                    // Determine if player with arg matches the requests list
                    Player playerThatRequested = null;
                    for (Player requestedPlayer : requests) {
                        if (requestedPlayer.getName().equalsIgnoreCase(toPlayer)) {
                            playerThatRequested = requestedPlayer;
                        }
                    }
                    if (playerThatRequested == null) {
                        player.sendMessage(ChatColor.RED + "Error: Couldn't identify an active TPA request from " + toPlayer + ".");
                        return;
                    }

                    // If player isn't online edge case
                    if (!playerThatRequested.isOnline()) {
                        player.sendMessage(ChatColor.RED + toPlayer + " is no longer online.");
                        requests.remove(playerThatRequested);
                        return;
                    }

                    playerThatRequested.sendMessage(ChatColor.GOLD + player.getName() + " has accepted your tpa request!");
                    player.sendMessage(ChatColor.GOLD + "Accepted " + playerThatRequested.getName() + "'s tpa request.");
                    playerThatRequested.teleport(player);
                    requests.remove(playerThatRequested);
                } else {
                    // Handle /tpa accept
                    Player playerThatRequested = requests.getFirst();
                    if (playerThatRequested.isOnline()) {
                        playerThatRequested.sendMessage(ChatColor.GOLD + player.getName() + " has accepted your tpa request!");
                        player.sendMessage(ChatColor.GOLD + "Accepted " + playerThatRequested.getName() + "'s tpa request.");
                        playerThatRequested.teleport(player);
                    } else {
                        player.sendMessage(ChatColor.RED + playerThatRequested.getName() + " is no longer online.");
                    }
                    requests.remove(playerThatRequested);
                }
            }
        } else if (arg.equalsIgnoreCase("deny")) {
            List<Player> requests = teleports.getOrDefault(player, new ArrayList<>());
            if (requests.isEmpty()) {
                player.sendMessage(ChatColor.RED + "You do not have any active teleport requests.");
            } else {
                if (args.length > 1) {
                    // Handle /tpa deny <name>
                    String toPlayer = args[1];

                    // If player is self, don't allow tpa request
                    if (toPlayer.equalsIgnoreCase(player.getName())) {
                        player.sendMessage(ChatColor.RED + "You cannot request to teleport to yourself!");
                        return;
                    }

                    // Determine if player with arg matches the requests list
                    Player playerThatRequested = null;
                    for (Player requestedPlayer : requests) {
                        if (requestedPlayer.getName().equalsIgnoreCase(toPlayer)) {
                            playerThatRequested = requestedPlayer;
                        }
                    }
                    if (playerThatRequested == null) {
                        player.sendMessage(ChatColor.RED + "Error: Couldn't identify an active TPA request from " + toPlayer + ".");
                        return;
                    }

                    // If player isn't online edge case
                    if (!playerThatRequested.isOnline()) {
                        player.sendMessage(ChatColor.RED + toPlayer + " is no longer online.");
                        requests.remove(playerThatRequested);
                        return;
                    }

                    playerThatRequested.sendMessage(ChatColor.RED + player.getName() + " has denied your tpa request!");
                    player.sendMessage(ChatColor.GOLD + "Denied " + playerThatRequested.getName() + "'s tpa request.");
                    requests.remove(playerThatRequested);
                } else {
                    // Handle /tpa deny
                    Player playerThatRequested = requests.getFirst();
                    if (playerThatRequested.isOnline()) {
                        playerThatRequested.sendMessage(ChatColor.RED + player.getName() + " has denied your tpa request!");
                        player.sendMessage(ChatColor.GOLD + "Denied " + playerThatRequested.getName() + "'s tpa request.");
                    } else {
                        player.sendMessage(ChatColor.RED + playerThatRequested.getName() + " is no longer online.");
                    }
                    requests.remove(playerThatRequested);
                }
            }
        } else {
            Player playerToRequest = getOnlinePlayerByName(arg);
            if (playerToRequest == null) {
                player.sendMessage(ChatColor.RED + "'" + arg + "' is not online right now, so you can't request to teleport to them.");
                return;
            }

            // Handle spam requests
            List<Player> requests = teleports.getOrDefault(playerToRequest, new ArrayList<>());
            if (requests.contains(player)) {
                player.sendMessage(ChatColor.RED + "You have already requested to teleport to " + playerToRequest.getName() + "!");
                return;
            }

            // Send request
            player.sendMessage(ChatColor.GOLD + "Sent a TPA request to " + playerToRequest.getName() + ".");
            playerToRequest.sendMessage(ChatColor.GOLD + player.getName() + " has requested to teleport to you. Use '/tpa accept' to accept or '/tpa deny' to deny. The request will expire in 60 seconds.");

            requests.add(player);
            teleports.put(playerToRequest, requests);

            // Remove TPA request
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (requests.contains(player)) {
                    if (player.isOnline())
                        player.sendMessage(ChatColor.RED + "Your TPA request has expired.");
                    if (playerToRequest.isOnline())
                        playerToRequest.sendMessage(ChatColor.GOLD + player.getName() + "'s request to teleport to you has expired.");
                    requests.remove(player);
                }
            }, 20 * 60L);
        }
    }

    public Player getOnlinePlayerByName(String name) {
        for (Player player : server.getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(name)) {
                return player;
            }
        }
        return null;
    }


    public void infoCommand(Player player) {
        for (int i = 0; i < 10; i++) player.sendMessage("");
        player.sendMessage(header("SERVER INFO"));

        // Sign Info
        player.sendMessage("\n");
        player.sendMessage(header("Sign Shop"));
        player.sendMessage(body("To place a sign shop, you must claim the chunk the chest is within using /claim."));
        player.sendMessage(body("You need to use both an oak sign and an oak chest to make the shop."));
        player.sendMessage(body("The sign in your shop should be formatted to look like:"));
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + " Selling 40");
        player.sendMessage(ChatColor.GOLD + " OAK_LOG");
        player.sendMessage(ChatColor.GOLD + " For 3");
        player.sendMessage(ChatColor.GOLD + " DIAMOND");
        player.sendMessage("");
        player.sendMessage(body("In this example, the shop would be selling 40 oak logs to buy 3 diamonds."));

        // Claim Info
        player.sendMessage("\n");
        player.sendMessage(header("Claims"));
        player.sendMessage(body("Use /claim to claim and /unclaim to unclaim the chunk you are within. This will prevent other players from griefing/stealing from your land. It won't protect any mobs in your land, though."));
        player.sendMessage(body("Use /map to get a map of claimed chunks near you."));
        player.sendMessage(body("Use /friend to manage who has access to your claimed chunks. By default, only you will have access."));
        player.sendMessage(body("You can only claim chunks within the first 10,000 blocks in the Overworld."));

        // Custom Item info
        player.sendMessage("\n");
        player.sendMessage(header("Custom Items"));
        player.sendMessage(body("To improve quality of life on the server, some custom items have been added and can be crafted:"));
        player.sendMessage(ChatColor.GOLD + " Builder's Wand" + ChatColor.GREEN + " - Extends all blocks of a given type to make building large projects easier.");
        player.sendMessage(ChatColor.GOLD + " Tree Feller" + ChatColor.GREEN + " - Shift and break a log to break all connected logs.");
        player.sendMessage(ChatColor.GOLD + " Night vision goggles" + ChatColor.GREEN + " - To make it fair to the players not using the Full-Bright mod, this item can be crafted to give you unlimited night vision.");
        player.sendMessage(ChatColor.GOLD + " Kinetic Elytra" + ChatColor.GREEN + " - An elytra that removes slamming into wall damage, so players don't randomly die 10,000 blocks in the Nether and quit (@Colin).");
        player.sendMessage(ChatColor.GOLD + " Tombstone" + ChatColor.GREEN + " - Craftable item that will place a chest with your loot where you die (@Alex).");
        player.sendMessage(ChatColor.GOLD + " Shulker Box" + ChatColor.GREEN + " - Right click any shulker box in the air to open it's inventory (@Alex).");

        // Hard++ Info
        player.sendMessage("\n");
        player.sendMessage(header(ChatColor.RED + "" + ChatColor.BOLD + "Hard++" + ChatColor.GREEN + "" + ChatColor.BOLD));
        player.sendMessage(body("In an effort to make MC more challenging, this world will increase in difficulty the farther you travel from the world origin."));
        player.sendMessage(body("The world is on regular HARD mode for the first 500 blocks from spawn, then gets harder for the next 500 blocks, and becomes even harder after 1000 blocks from spawn."));
        player.sendMessage(body("Bosses, particularly the ender dragon, are much harder."));
        player.sendMessage(body("If you want something more peaceful, live close to spawn."));
        player.sendMessage(body("Be careful. The world is on " + ChatColor.RED + "Hard++" + ChatColor.GREEN + " mode."));

        // General Info
        player.sendMessage("\n");
        player.sendMessage(header("General Info"));
        player.sendMessage(body("Seed: 307370674568038595"));
        player.sendMessage(body("Use /rules to get a list of server rules."));
        player.sendMessage(body("Use /commands to get a list of custom server commands."));
        player.sendMessage(body("Use /chat to change how your chat is displayed."));
        player.sendMessage(body("Use /home to teleport to your bed and /warp spawn to go to spawn."));
        player.sendMessage(body("If you have any questions/want something to change about the server, ask the owner (DaGabeyWabey)."));
    }

    public void showCommandsCommand(Player player) {
        player.sendMessage("\n");
        player.sendMessage(header("Custom Commands"));
        player.sendMessage("\n");

        player.sendMessage(ChatColor.GOLD + "[Land claiming]");
        player.sendMessage(commandLine("/claim", "To claim the chunk you are currently on"));
        player.sendMessage(commandLine("/unclaim", "To unclaim the chunk you are currently on"));
        player.sendMessage(commandLine("/map", "To display claims around you as a map"));
        player.sendMessage(commandLine("/friend", "To manage who has access to your claims\n"));

        player.sendMessage(ChatColor.GOLD + "[Misc]");
        player.sendMessage(commandLine("/chat", "To manage how your chat is displayed"));
        player.sendMessage(commandLine("/mail", "To send mail to players (even when they are offline)"));
        player.sendMessage(commandLine("/rules", "To list server rules"));
        player.sendMessage(commandLine("/commands", "To list server commands"));
        player.sendMessage(commandLine("/info", "To get server info"));
        player.sendMessage(commandLine("/warp", "To teleport to warp locations, like spawn"));
        player.sendMessage(commandLine("/tpa", "To request to teleport to a player"));
        player.sendMessage(commandLine("/home", "To teleport to your bed"));
        player.sendMessage(commandLine("/showhand", "To show the server the item that is held in your main hand."));

        player.sendMessage(ChatColor.GREEN + "\nIf you would like extra commands to be added, ask the owner (DaGabeyWabey).");
    }

    public void rulesCommand(Player player) {
        player.sendMessage("\n");
        player.sendMessage(header("Rules"));
        player.sendMessage(body("No griefing/stealing/killing without reason, but pranking/trolling is fine."));
        player.sendMessage(body("No lag machines/farms that create extensive amounts of lag."));
        player.sendMessage(body("No item/block/tnt dupers."));
        player.sendMessage(body("Client-side mods/textures are fine, but they shouldn't give you an unfair advantage, i.e. no x-ray."));
        player.sendMessage(body("Don't claim chunks that are not yours (like when someone forgot to claim their land)."));
        player.sendMessage(body("No slurs."));
        player.sendMessage(ChatColor.RED + "If you see anyone violating these rules, report them to DaGabeyWabey.");
    }

    public String body(String text) {
        return ChatColor.GREEN + " - " + text;
    }

    public String commandLine(String command, String label) {
        return ChatColor.GREEN + command + " - " + label;
    }

    public String header(String text) {
        return ChatColor.GREEN + "" + ChatColor.BOLD + "[" + text + "]";
    }





}
