package me.domcie.skyplots.commands;

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

import javax.xml.crypto.Data;

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


            String worldName = cfg.island_world;
            World world = Bukkit.getWorld(worldName);
            Location islandLocation = new Location(world, 0, 64, 0);
            int ring = 2;
            int sum = 8;

            while(sum <= DataStorage.islands.size()-1) {
                sum = sum*2 + 8;
                ring = ring + 1;
            }

            int w = (ring * 2) - 1;
            int x = 0 - (w / 2);
            int y = 0 - (w / 2);

            int dx = 1;
            int dy = 0;
            outer:
            for (int side = 0; side < 4; ++side) {
                for (int i = 1; i < w; ++i) {
                    islandLocation.setX(x*55);
                    islandLocation.setZ(y*55);
                    if(IslandData.isIslandLocationAvailable(islandLocation)){
                        p.sendMessage("Ring: "+ring);
                        p.sendMessage("Sum: "+sum);
                        p.sendMessage("Next at x: "+ x*55 + "z: "+ y*55);
                        break outer;
                    }
                    x += dx;
                    y += dy;
                }

                int t = dx;
                dx = -dy;
                dy = t;
            }

            //DataStorage.save();
            return false;
        }
    }
}
