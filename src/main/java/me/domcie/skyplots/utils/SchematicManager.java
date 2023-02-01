package me.domcie.skyplots.utils;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.*;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.session.SessionManager;
import me.domcie.skyplots.SkyPlots;
import me.domcie.skyplots.data.config;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SchematicManager {
    static config cfg = config.getInst();

    public static boolean pasteSchematic(Player p, Location location){
        Clipboard clipboard;
        // Load the schematic
        File file = new File(SkyPlots.getInst().getDataFolder() + "/schematic/island.schematic");
        if (!file.exists()) {
            p.sendMessage(cfg.msg_no_schematic);
            return false;
        }
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        assert format != null;
        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            clipboard = reader.read();
        } catch (IOException e) {
            p.sendMessage(cfg.msg_error_pasting);
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
            p.sendMessage(cfg.msg_error_pasting);
            return false;
        }
        return true;
    }

    public static boolean saveSchematic(Player player) {
        com.sk89q.worldedit.entity.Player actor = BukkitAdapter.adapt(player);
        SessionManager manager = WorldEdit.getInstance().getSessionManager();
        LocalSession localSession = manager.get(actor);

        ClipboardHolder clipboard;
        try {
            clipboard = localSession.getClipboard();
        } catch (EmptyClipboardException e) {
            player.sendMessage(cfg.msg_empty_clipboard);
            return true;
        }

        File file = new File(SkyPlots.getInst().getDataFolder().getPath() + "/schematic/island.schematic");
        file.getParentFile().mkdirs();

        try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(file))) {
            writer.write(clipboard.getClipboard());
        } catch (IOException e) {
            player.sendMessage(cfg.msg_error_saving);
            return true;
        }

        player.sendMessage(cfg.msg_success);
        return true;
    }
}
