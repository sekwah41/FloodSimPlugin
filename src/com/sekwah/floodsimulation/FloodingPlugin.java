package com.sekwah.floodsimulation;

import com.sekwah.floodsimulation.flooddata.FloodTracker;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by on 29/03/2016.
 *
 * TODO save the current area as a file or copy it somewhere else to revert it afterwards.
 *
 * @author sekwah41
 */
public class FloodingPlugin extends JavaPlugin {

    public ConfigAccessor config;

    public VisualDebug visualDebug;

    public FloodTracker floodTracker;

    public void onEnable(){

        visualDebug = new VisualDebug(this);

        saveDefaultConfig();

        config = new ConfigAccessor(this, "config.yml");

        FileConfiguration configAccess = config.getConfig();

        floodTracker = new FloodTracker(this);

        new FloodCommand(this);

        new Listeners(this);

        this.getServer().getConsoleSender().sendMessage("\u00A7aFlood simulation has been enabled.");
    }

    public void onDisable(){
        this.getServer().getConsoleSender().sendMessage("\u00A7cFlood simulation has been disabled.");
    }

}
