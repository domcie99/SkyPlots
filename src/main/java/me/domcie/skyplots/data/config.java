package me.domcie.skyplots.data;

import me.domcie.skyplots.SkyPlots;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class config {
    private static config inst;
    public FileConfiguration cfg = SkyPlots.getInst().getConfig();
    public String DataType;
    public String host;
    public int port;
    public String dbname;
    public String user;
    public String password;

    public String island_world;
    public int island_size;
    public int island_gap;

    public void reload(){
        //plugin.reloadConfig();
        load();
    }
    public void load(){
        this.DataType = cfg.getString("database.type");

        this.host = cfg.getString("database.host");
        this.port = cfg.getInt("database.port");
        this.dbname = cfg.getString("database.dbname");
        this.user = cfg.getString("database.user");
        this.password = cfg.getString("database.password");

        this.island_world = cfg.getString("island.world-name");
        this.island_size = cfg.getInt("island.size");
        this.island_gap = cfg.getInt("island.gap");
    }

    public static config getInst(){
        if(inst == null) return new config();
        return inst;
    }
    public config(){
        inst = this;
    }
}
