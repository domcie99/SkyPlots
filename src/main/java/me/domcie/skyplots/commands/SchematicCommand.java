package me.domcie.skyplots.commands;

import me.domcie.skyplots.data.config;
import me.domcie.skyplots.utils.SchematicManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class SchematicCommand implements CommandExecutor {
    private final Plugin plugin;
    public SchematicCommand(Plugin plugin) {
        this.plugin = plugin;
    }
    config cfg = config.getInst();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(cfg.msg_player_only);
            return true;
        }
        if(sender.hasPermission("skyplots.admin")) {
            String usage = cfg.msg_usage.replace("<usage>", "/schm [save|paste]");
            Player p = ((Player) sender).getPlayer();
            if (args.length < 1) {
                sender.sendMessage(usage);
                return true;
            } else {
                if (args[0].equalsIgnoreCase("save")) {
                    SchematicManager.saveSchematic(p);
                    return true;
                } if (args[0].equalsIgnoreCase("paste")) {
                    SchematicManager.pasteSchematic(p, p.getLocation());
                    return true;
                } else {
                    sender.sendMessage(usage);
                    return true;
                }
            }
        } else {
            sender.sendMessage(cfg.msg_no_permission);
        }
        return true;
    }
}
