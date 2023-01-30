package me.domcie.skyplots.commands;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.*;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.world.World;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;

public class schema implements CommandExecutor {
    private final Plugin plugin;
    public schema(Plugin plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage("§4Komenda jest dostepna tylko dla graczy!");
            return true;
        }
        if(sender.hasPermission("skyplots.admin")) {
            String usage = "§4Poprawne uzycie /schema [load|set] [spawn|island]";
            if (args.length <= 1) {
                sender.sendMessage(usage);
                return true;
            } else {
                if (args[0].equalsIgnoreCase("set")) {
                    if (args[1].equalsIgnoreCase("island")) {
                        saveSchema(player, "island");
                        return true;
                    } if (args[1].equalsIgnoreCase("spawn")) {
                        saveSchema(player, "spawn");
                        return true;
                    } else {
                        sender.sendMessage(usage);
                        return true;
                    }
                } if (args[0].equalsIgnoreCase("load")) {
                    if (args[1].equalsIgnoreCase("island")) {
                        loadSchema(player, "island");
                        return true;
                    } if (args[1].equalsIgnoreCase("spawn")) {
                        loadSchema(player, "spawn");
                        return true;
                    } else {
                        sender.sendMessage(usage);
                        return true;
                    }
                } else {
                    sender.sendMessage(usage);
                    return true;
                }
            }
        }
        return true;
    }

    public boolean saveSchema(Player player, String type) {
        com.sk89q.worldedit.entity.Player actor = BukkitAdapter.adapt(player);
        SessionManager manager = WorldEdit.getInstance().getSessionManager();
        LocalSession localSession = manager.get(actor);

        ClipboardHolder clipboard;
        try {
            clipboard = localSession.getClipboard();
        } catch (EmptyClipboardException e) {
            actor.printError(TextComponent.of("Schowek jest pusty."));
            return true;
        }

        File file = new File(plugin.getDataFolder().getPath() + "/schematic/" + type + ".schematic");
        file.getParentFile().mkdirs();

        try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(file))) {
            writer.write(clipboard.getClipboard());
        } catch (IOException e) {
            actor.printError(TextComponent.of("Wystpil blad podczas zapisywania."));
            Bukkit.getLogger().log(Level.WARNING, e.getMessage());
            return true;
        }

        player.sendMessage(ChatColor.GREEN + "Zapisano schemat w:" + file.getPath());
        return true;
    }

    public boolean loadSchema(Player player, String type){

        Clipboard clipboard;

        // Load the schematic
        File file = new File(plugin.getDataFolder().getPath() + "/schematic/" + type + ".schematic");
        if (file.exists()) {
            // Load the clipboard
            ClipboardFormat format = ClipboardFormats.findByFile(file);
            assert format != null;
            try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
                clipboard = reader.read();
            } catch (IOException e) {
                player.sendMessage("Wystpil blad podczas wczytywania.");
                Bukkit.getLogger().log(Level.WARNING, e.getMessage());
                return true;
            }


            World world = BukkitAdapter.adapt(player.getWorld());
            // Paste the clipboard
            try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(BlockVector3.at(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()))
                        .build();
                Operations.complete(operation);
            } catch (WorldEditException e) {
                player.sendMessage("Wystpil blad podczas wklejania.");
                Bukkit.getLogger().log(Level.WARNING, e.getMessage());
                return true;
            }

            player.sendMessage("Wklejono schemat.");
        } else {
            player.sendMessage(ChatColor.RED + "Schematy nie zostały jeszcze zapisane");
        }
        return true;
    }
}
