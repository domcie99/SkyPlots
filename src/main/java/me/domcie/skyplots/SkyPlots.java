package me.domcie.skyplots;

import me.domcie.skyplots.commands.IslandCommand;
import me.domcie.skyplots.commands.schema;
import me.domcie.skyplots.commands.test;
import me.domcie.skyplots.data.DataStorage;
import me.domcie.skyplots.data.config;
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
        saveDefaultConfig();
        config.getInst().load();

        dataStorage = new DataStorage();
        dataStorage.initialize();

        generateWorld();

        getCommand("test").setExecutor(new test(this));
        getCommand("schema").setExecutor(new schema(this));

        getCommand("island").setExecutor(new IslandCommand(this));
        Bukkit.getConsoleSender().sendMessage("Test: "+config.getInst().island_world);
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        //dataStorage.save();
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
