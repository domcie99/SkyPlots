package me.domcie.skyplots.commands;

import me.domcie.skyplots.data.DataStorage;
import me.domcie.skyplots.data.IslandData;
import me.domcie.skyplots.data.config;
import me.domcie.skyplots.utils.ConfirmationManager;
import me.domcie.skyplots.utils.InvitationManager;
import me.domcie.skyplots.utils.SchematicManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.UUID;

public class IslandCommand implements CommandExecutor {
    private final Plugin plugin;
    public IslandCommand(Plugin plugin) {
        this.plugin = plugin;
    }
    config cfg = config.getInst();
    public final int ISLAND_SIZE = cfg.island_size;
    public final int GAP_SIZE = cfg.island_gap;
    public final int RADIUS = ISLAND_SIZE + GAP_SIZE;
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(cfg.msg_player_only);
            return true;
        }
        Player p = ((Player) sender).getPlayer();
        if (args.length < 1) {
            if(sender.hasPermission("skyplots.admin")) {
                for(String s : cfg.adminGui){
                    p.sendMessage(s);
                }
            } else {
                for(String s : cfg.playerGui){
                    p.sendMessage(s);
                }
            }
            return true;
        } else if (args.length <= 2) {
            if (args[0].equalsIgnoreCase("home")) {

                teleportToIsland(p);

            } else if (args[0].equalsIgnoreCase("create")) {

                createIslandForPlayer(p);

            } else if (args[0].equalsIgnoreCase("accept")) {

                InvitationManager.accept(p);

            } else if (args[0].equalsIgnoreCase("decline")) {

                InvitationManager.decline(p);

            } else if (args[0].equalsIgnoreCase("invite")) {

                invitePlayer(p, args);

            } else if (args[0].equalsIgnoreCase("delete")) {

                deleteIsland(p, args);

            } else if (args[0].equalsIgnoreCase("remove")) {

                removePlayer(p, args);

            } else if(sender.hasPermission("skyplots.admin") && (args[0].equalsIgnoreCase("tp"))) {

                p.teleport(new Location(Bukkit.getWorld(cfg.island_world), 0, 100, 0));

            } else {
                p.performCommand("is");
            }
        } else {
            p.performCommand("is");
        }

        return true;
    }
    public boolean teleportToIsland(Player p){
        IslandData is = IslandData.getIslandByUUID(p.getUniqueId().toString());
        if(is != null){
            p.teleport(is.getLocation());
        } else {
            p.sendMessage(cfg.msg_no_island);
        }
        return true;
    }
    public boolean createIslandForPlayer(Player player) {

        UUID playerId = player.getUniqueId();

        IslandData island;
        IslandData owner = IslandData.getIslandByOwnerId(playerId.toString());
        IslandData member = IslandData.getIslandByMemberId(playerId.toString());

        if (owner == null) {
            if(member == null) {
                // Calculate the location of the new island
                String worldName = cfg.island_world;
                World world = Bukkit.getWorld(worldName);
                Location islandLocation = new Location(world, 0.5, 64, 0.5);

                int size = cfg.island_size;
                int gap = cfg.island_gap;
                int rad = gap + size;

                int ring = 2;
                int sum = 8;
                // Ulam Spirall calculating ring count based on database entry's.
                while(sum <= DataStorage.islands.size()-1) {
                    sum = sum*2 + 8;
                    ring = ring + 1;
                }

                //Checking for available location if not found go to next ring.
                if(!IslandData.isIslandLocationAvailable(islandLocation)) {
                    while(true){
                        islandLocation = generateIslandLocation(world, ring);
                        if(islandLocation == null){
                            ring++;
                        } else {
                            break;
                        }
                    }
                }

                if(IslandData.isIslandLocationAvailable(islandLocation)){
                    //Using randomUUID For testing purpose
                    island = new IslandData(playerId.toString(), islandLocation, new ArrayList<>());
                   //island = new IslandData(UUID.randomUUID().toString(), islandLocation, new ArrayList<>());

                    // Paste Schematic - If Pasting success add to database.
                    if(SchematicManager.pasteSchematic(player, islandLocation)){
                        DataStorage.islands.add(island);
                        DataStorage.save();
                        player.sendMessage(cfg.msg_created);
                        player.teleport(islandLocation);
                    }
                } else {
                    player.sendMessage(cfg.msg_no_location);
                }
            } else {
                player.sendMessage(cfg.msg_has_island);
            }
        } else {
            player.sendMessage(cfg.msg_has_island);
        }
        return true;
    }
    public Location generateIslandLocation(World world, int ringCount) {
        int w = (ringCount * 2) - 1;
        int x = 0 - (w / 2);
        int y = 0 - (w / 2);

        int dx = 1;
        int dy = 0;
        //Calculating next island location in Ulam Spirall Patern.
        outer:
        for (int side = 0; side < 4; ++side) {
            for (int i = 1; i < w; ++i) {
                Location islandLocation = new Location(world, x * RADIUS + 0.5, 64, y * RADIUS + 0.5);

                // Check if island already exists at this location
                if (IslandData.isIslandLocationAvailable(islandLocation)) {
                    return islandLocation;
                }

                x += dx;
                y += dy;
            }

            int t = dx;
            dx = -dy;
            dy = t;
        }
        return null;
    }

    boolean invitePlayer(Player p, String[] args){
        if (args.length == 2 && !args[1].isEmpty()) {
            Player p2 = Bukkit.getPlayer(args[1]);
            IslandData is = IslandData.getIslandByOwnerId(p.getUniqueId().toString());
            if(is == null){
                p.sendMessage(cfg.msg_not_owner);
                return true;
            } else {
                if(p2 != null && p2.isOnline() && !p.equals(p2)){
                    if (InvitationManager.hasPendingInvitation(p2)) {
                        p.sendMessage(cfg.msg_has_pending_invitation);
                    } else {
                        InvitationManager.sendInvitation(p, p2);
                    }
                } else {
                    p.sendMessage(cfg.msg_player_offline);
                }
            }
        } else {
            p.sendMessage(cfg.msg_usage.replace("<usage>","/is invite <player>"));
        }
        return true;
    }

    boolean deleteIsland(Player p, String[] args){
        if(args.length == 2) {
            if (p.hasPermission("skyplots.admin")) {
                Player p2 = Bukkit.getPlayer(args[1]);
                IslandData is;

                if(p2 != null && p2.isOnline()){
                    is = IslandData.getIslandByOwnerId(p2.getUniqueId().toString());
                    if(is != null){
                        IslandData.deleteIsland(is);
                        p.sendMessage(cfg.msg_delete);
                        return true;
                    } else {
                        p.sendMessage(cfg.msg_no_island);
                        return false;
                    }
                } else {
                    p2 = Bukkit.getPlayer(UUID.fromString(args[1]));
                    if(p2 != null && p2.isOnline()) {
                        is = IslandData.getIslandByOwnerId(p2.getUniqueId().toString());
                        if (is != null) {
                            IslandData.deleteIsland(is);
                            p.sendMessage(cfg.msg_delete);
                            return true;
                        } else {
                            p.sendMessage(cfg.msg_no_island);
                            return false;
                        }
                    }
                }
                p.sendMessage(cfg.msg_player_null);
            } else {
                p.sendMessage(cfg.msg_no_permission);
            }
            return false;
        } else {
            IslandData is = IslandData.getIslandByOwnerId(p.getUniqueId().toString());
            if(is == null){
                p.sendMessage(cfg.msg_not_owner);
                return true;
            } else {
                if(ConfirmationManager.hasPendingConfirmation(p) != null) {
                    p.sendMessage(cfg.msg_delete);
                    IslandData.deleteIsland(is);
                    ConfirmationManager.confirmations.remove(p.getUniqueId());
                } else {
                    ConfirmationManager.SendConfirmation(p, args[0]);
                }
            }
            return false;
        }
    }

    boolean removePlayer(Player p, String[] args){
        if(args.length == 2) {
            OfflinePlayer p2 = Bukkit.getOfflinePlayer(args[1]);
            IslandData is = IslandData.getIslandByUUID(p2.getUniqueId().toString());

            if(is != null){
                if (is.getUserId().equals(p.getUniqueId().toString()) || p.hasPermission("skyplots.admin")) {
                    is.removeMember(p2.getUniqueId().toString());
                    p.sendMessage(cfg.msg_remove_sender.replace("<player>", p2.getName()));
                    if(p2.isOnline()) {
                        p2.getPlayer().sendMessage(cfg.msg_remove_receiver.replace("<player>", p.getName()));
                    }
                    return true;
                } else {
                    p.sendMessage(cfg.msg_not_owner);
                    return false;
                }
            } else {
                p.sendMessage(cfg.msg_player_not_member.replace("<player>", p2.getName()));
                return false;
            }
        }
        p.sendMessage(cfg.msg_usage.replace("<usage>", "/is remove <player>"));
        return false;
    }
}

