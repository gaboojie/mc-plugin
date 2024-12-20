package org.gabooj.landprotection;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LandManagerCommands implements CommandExecutor {

    private final Server server;
    private final JavaPlugin plugin;

    public LandManagerCommands(Server server, JavaPlugin plugin) {
        this.server = server;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to execute this command.");
            return true;
        }
        Player player = (Player) sender;

        if (label.equalsIgnoreCase("map")) {
            onMapCommand(player, label, args);
        } else if (label.equalsIgnoreCase("claim")) {
            onClaimCommand(player, label, args);
        } else if (label.equalsIgnoreCase("unclaim")) {
            onUnClaimCommand(player, label, args);
        } else if (label.equalsIgnoreCase("friend")) {
            onFriendCommand(player, label, args);
        } else if (label.equalsIgnoreCase("setclaim")) {
            onSetClaimCommand(player, label, args);
        } else {
            return false;
        }
        return true;
    }

    public void onFriendCommand(Player player, String label, String[] args) {
        if (args.length == 0) {
            player.sendMessage(ChatColor.BLUE + "The friend command is used to add/remove other players to your claimed lands." +
                    "Use /friend add <name> or /friend remove <name> to add/remove friends.");
            return;
        }
        int playerID = LandManager.getPlayerID(player.getName());
        PlayerLandInfo info = LandManager.playerInfo.get(playerID);
        if (args[0].equalsIgnoreCase("add")) {
            if (args.length == 1) {
                player.sendMessage(ChatColor.RED + "You must specify a player's name!");
            } else {
                String name = args[1];
                if (isPlayerOnline(name, player.getServer())) {
                    int friendID = LandManager.getPlayerID(name);
                    if (info.friends.contains(friendID)) {
                        player.sendMessage(ChatColor.RED + "You are already friends with that player!");
                    } else {
                        info.friends.add(friendID);
                        player.sendMessage(ChatColor.GREEN + name + " has been added as a friend. They can now access all of your claimed land.");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "The player must be online for you to add them as a friend!");
                }
            }
        } else if (args[0].equalsIgnoreCase("remove")) {
            if (args.length == 1) {
                player.sendMessage(ChatColor.RED + "You must specify a player's name!");
            } else {
                String exfriend = args[1];
                int exfriendID = LandManager.playerIDs.getOrDefault(exfriend, -1);
                if (exfriendID == -1) {
                    player.sendMessage(ChatColor.RED + exfriend + " is not your friend.");
                    return;
                }

                List<Integer> friends = info.friends;
                if (friends.contains(exfriendID)) {
                    player.sendMessage(ChatColor.RED + exfriend + " is no longer your friend. They cannot access your land anymore.");
                    for (int friend_index = 0; friend_index < friends.size(); friend_index++) {
                        if (friends.get(friend_index) == exfriendID) {
                            friends.remove(friend_index);
                            break;
                        }
                    }
                } else {
                    player.sendMessage(ChatColor.RED + exfriend + " is not your friend.");
                }
            }
        } else if (args[0].equalsIgnoreCase("list")) {
            String friendsStr = "";

            List<Integer> friends = info.friends;
            for (int friendID : friends) {
                PlayerLandInfo friendInfo = LandManager.playerInfo.get(friendID);
                friendsStr += friendInfo.name + ", ";
            }
            if (friendsStr.isEmpty()) {
                player.sendMessage(ChatColor.GREEN + "You don't have any friends! (LOOSER)");
            } else {
                friendsStr = friendsStr.substring(0, friendsStr.length()-2);
                player.sendMessage(ChatColor.GREEN + "Friends: " + friendsStr);
            }
        } else {
            player.sendMessage(ChatColor.RED + "Error: To add a friend, use '/friend add <name>'. To remove a friend, use '/friend remove <name>'. To list friends, use '/friend list'.");
        }
    }

    public void onClaimCommand(Player player, String label, String[] args) {
        Location loc = player.getLocation();
        if (LandManager.isWithinClaimBorders(loc)) {
            int id = LandManager.getClaimOwner(loc.getChunk().getX(), loc.getChunk().getZ());
            int playerID = LandManager.getPlayerID(player.getName());
            if (id == 0) {
                player.sendMessage(ChatColor.GREEN + "You have claimed this chunk.");
                LandManager.setClaimOwner(loc.getChunk().getX(), loc.getChunk().getZ(), playerID);
            } else {
                if (player.isOp()) {
                    player.sendMessage(ChatColor.GREEN + "Your OP status allowed you to claim a chunk that was already owned.");
                    LandManager.setClaimOwner(loc.getChunk().getX(), loc.getChunk().getZ(), playerID);
                } else {
                    player.sendMessage(ChatColor.RED + "Error: this chunk has already been claimed!");
                }
            }
        } else {
            player.sendMessage(ChatColor.RED + "You must be in the Overworld within the first 10,000 blocks of spawn to claim a chunk.");
        }
    }

    public void onUnClaimCommand(Player player, String label, String[] args) {
        Location loc = player.getLocation();
        if (LandManager.isWithinClaimBorders(loc)) {
            int id = LandManager.getClaimOwner(loc.getChunk().getX(), loc.getChunk().getZ());
            int playerID = LandManager.getPlayerID(player.getName());

            if (id == playerID) {
                player.sendMessage(ChatColor.GREEN + "You have unclaimed this chunk.");
                LandManager.setClaimOwner(loc.getChunk().getX(), loc.getChunk().getZ(), 0);
            } else {
                if (player.isOp()) {
                    player.sendMessage(ChatColor.GREEN + "Your OP status allowed you to unclaim this chunk.");
                    LandManager.setClaimOwner(loc.getChunk().getX(), loc.getChunk().getZ(), 0);
                } else {
                    player.sendMessage(ChatColor.RED + "Error: this chunk is not yours to unclaim!");
                }
            }
        } else {
            player.sendMessage(ChatColor.RED + "You must be in the Overworld within the first 10,000 blocks of spawn to unclaim a chunk.");
        }
    }

    public void onSetClaimCommand(Player player, String label, String[] args) {
        if (player.isOp()) {
            Location loc = player.getLocation();
           if (LandManager.isWithinClaimBorders(loc)) {
               if (args.length > 0) {
                   String argument = args[0];
                    if (argument.equalsIgnoreCase("nature")) {
                        LandManager.setClaimOwner(loc.getChunk().getX(), loc.getChunk().getZ(), 0);
                        player.sendMessage(ChatColor.GREEN + "Chunk's owner has been set to nature.");
                    } else if (argument.equalsIgnoreCase("server")) {
                        LandManager.setClaimOwner(loc.getChunk().getX(), loc.getChunk().getZ(), 1);
                        player.sendMessage(ChatColor.GREEN + "Chunk's owner has been set to server.");
                    } else {
                        int id = LandManager.playerIDs.getOrDefault(argument, -1);
                        if (id == -1) {
                            player.sendMessage(ChatColor.RED + "That player does not exist!");
                        } else {
                            LandManager.setClaimOwner(loc.getChunk().getX(), loc.getChunk().getZ(), id);
                            player.sendMessage(ChatColor.GREEN + "Chunk's owner has been set to '" + argument + "'.");
                        }
                    }
               } else {
                   player.sendMessage(ChatColor.RED + "Use /setclaim <name> to set the owner of this chunk. The name can be 'nature' to reset it and the name can be 'server' for admin-use only.");
               }
           } else {
               player.sendMessage(ChatColor.RED + "You must be in the Overworld within the first 10,000 blocks of spawn to set the owner of a chunk.");
           }
        } else {
            player.sendMessage(ChatColor.RED + "You do not have permission to run this command.");
        }
    }

    public void onMapCommand(Player player, String label, String[] args) {
        Location loc = player.getLocation();
        if (LandManager.isWithinClaimBorders(loc)) {
            int radius = 5;
            boolean wasRadiusCapped = false;
            if (args.length > 0) {
                try {
                    radius = Integer.parseInt(args[0]);
                    if (radius < 1) {
                        player.sendMessage(ChatColor.RED + "Error: the map radius must be a positive number.");
                        return;
                    }
                    if (radius > 10) {
                        wasRadiusCapped = true;
                        radius = 10;
                    }
                } catch (Exception e) {
                    player.sendMessage(ChatColor.RED + "Error: " + args[0] + " must be a positive number as it is used for the map radius.");
                    return;
                }
            }

            // Display map radius
            String str = ChatColor.YELLOW + "Claim Map:\n"
                    + ChatColor.GREEN + "[■ - Your Claim]"
                    + ChatColor.GRAY + "[■ - Nature]"
                    + ChatColor.RED + "[■ - Server]"
                    + ChatColor.BLUE + "[■ - Other]";
            player.sendMessage(str);

            int chunkX = loc.getChunk().getX();
            int chunkZ = loc.getChunk().getZ();
            int playerID = LandManager.getPlayerID(player.getName());
            for (int offsetX = -radius; offsetX <= radius; offsetX++) {
                String to_print = "";
                for (int offsetZ = -radius; offsetZ <= radius; offsetZ++) {
                    int newChunkX = chunkX + offsetX;
                    int newChunkZ = chunkZ + offsetZ;
                    if (LandManager.isWithinBordersXZ(newChunkX, newChunkZ)) {
                        int id = LandManager.getClaimOwner(newChunkX, newChunkZ);
                        if (id == playerID) {
                            to_print += ChatColor.GREEN + "■";
                        } else if (id == 0) {
                            to_print += ChatColor.GRAY + "■";
                        } else if (id == 1) {
                            to_print += ChatColor.RED + "■";
                        } else {
                            to_print += ChatColor.BLUE + "■";
                        }
                    } else {
                        to_print += ChatColor.GRAY + "■";
                    }
                }
                player.sendMessage(to_print);
            }

            if (wasRadiusCapped) {
                player.sendMessage(ChatColor.RED + "The map radius has been capped at 10.");
            }
        } else {
            player.sendMessage(ChatColor.RED + "You must be in the Overworld within the first 10,000 blocks of spawn to get a claim map at this location.");
        }
    }

    public static boolean isPlayerOnline(String playerName, Server server) {
        for (Player player : server.getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(playerName)) {
                return true; // Player is online
            }
        }
        return false; // Player is not online
    }

}

