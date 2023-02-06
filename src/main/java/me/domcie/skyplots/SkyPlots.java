package me.domcie.skyplots;

import me.domcie.skyplots.commands.IslandCommand;
import me.domcie.skyplots.commands.SchematicCommand;
import me.domcie.skyplots.commands.test;
import me.domcie.skyplots.data.DataStorage;
import me.domcie.skyplots.data.config;
import me.domcie.skyplots.listeners.PlayerListener;
import me.domcie.skyplots.utils.EmptyChunkGenerator;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

public final class SkyPlots extends JavaPlugin {
    private World world;

    private DataStorage dataStorage;

    public static SkyPlots inst;
    public static SkyPlots getInst(){
        return inst;
    }
    @Override
    public void onEnable() {
        inst = this;
        //Config Initialize
        saveDefaultConfig();
        config.getInst().load();

        //Data Initialize
        dataStorage = new DataStorage();
        dataStorage.initialize();

        //Generate World specified in config.
        generateWorld();

        //Initialize commands
        getCommand("test").setExecutor(new test(this));
        getCommand("schm").setExecutor(new SchematicCommand(this));
        getCommand("island").setExecutor(new IslandCommand(this));

        //Initialize Listeners
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);

    }

    @Override
    public void onDisable() {
        dataStorage.save();
    }

    public void generateWorld(){
        config cfg = config.getInst();
        if (Bukkit.getWorld(cfg.island_world) == null) {
            WorldCreator worldCreator = new WorldCreator(cfg.island_world);
            worldCreator.generator(new EmptyChunkGenerator());
            world = worldCreator.createWorld();
        } else {
            world = Bukkit.getWorld(cfg.island_world);
        }
    }
}
