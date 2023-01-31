package me.domcie.skyplots.commands;

import me.domcie.skyplots.data.config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class test implements CommandExecutor {
    private final Plugin plugin;
    public test(Plugin plugin) {
        this.plugin = plugin;
    }

    config cfg = config.getInst();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        cfg.load();
        if(!(sender instanceof Player)) {
            sender.sendMessage("§4Komenda jest dostepna tylko dla graczy!");
            sender.sendMessage("asd" + cfg.island_world);
            return false;
        } else {
            Player p = (Player) sender;
            p.teleport(new Location(Bukkit.getWorld(cfg.island_world),0,100,0));
            return false;
        }
    }
}
