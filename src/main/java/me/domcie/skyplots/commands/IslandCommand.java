package me.domcie.skyplots.commands;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import me.domcie.skyplots.SkyPlots;
import me.domcie.skyplots.data.DataStorage;
import me.domcie.skyplots.data.IslandData;
import me.domcie.skyplots.data.config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

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
            sender.sendMessage("§4This command can only be executed by a player.");
            return true;
        }

        if (args.length == 1) {
            createIslandForPlayer(((Player) sender).getPlayer());
            return true;
        }
        if (sender.hasPermission("skyplots.admin")) {
            //String usage = "§4Poprawne uzycie /schema [load|set] [spawn|island]";
        }
        return true;
    }

    public void createIslandForPlayer(Player player) {
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
                // Ulam Spirall calculating ring count based on database entrys.
                while(sum <= DataStorage.islands.size()-1) {
                    sum = sum*2 + 8;
                    ring = ring + 1;
                }
                if(!IslandData.isIslandLocationAvailable(islandLocation)) {
                    islandLocation = generateIslandLocation(world, ring);
                }

                if(IslandData.isIslandLocationAvailable(islandLocation)){
                    // Create the new island

                    //Island for Actual Use
                    //island = new IslandData(playerId.toString(), islandLocation, new ArrayList<>());

                    //Island for testing
                    island = new IslandData(UUID.randomUUID().toString(), islandLocation, new ArrayList<>());

                    // Paste Schematic

                    if(pasteSchematic(player, islandLocation)){
                        //If Pasting success add to database.
                        DataStorage.islands.add(island);
                        DataStorage.save();
                        player.sendMessage("§6Stworzono Wyspę");
                        //player.teleport(islandLocation);
                    }
                } else {
                    Bukkit.getLogger().log(Level.WARNING, "Could not find Avaiable Location");
                }

            } else {
                player.teleport(member.getLocation());
            }
        } else {
            player.teleport(owner.getLocation());
        }
    }

    public boolean pasteSchematic(Player p, Location location){
        Clipboard clipboard;
        // Load the schematic
        File file = new File(SkyPlots.getInst().getDataFolder() + "/schematic/island.schematic");
        if (!file.exists()) {
            p.sendMessage("Blad wczytywania schematu");
            Bukkit.getConsoleSender().sendMessage("Stworz schemat uzywajac komendy /schema");
            return false;
        }
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        assert format != null;
        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            clipboard = reader.read();
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.WARNING, e.getMessage());
            return false;
        }
        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(location.getWorld());
        // Paste the clipboard
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
            Operation operation = new ClipboardHolder(clipboard)
                    .createPaste(editSession)
                    .to(BlockVector3.at(location.getX(), location.getY(), location.getZ()))
                    .build();
            Operations.complete(operation);
        } catch (WorldEditException e) {
            Bukkit.getLogger().log(Level.WARNING, e.getMessage());
            return false;
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
}

