package com.sekwah.floodsimulation;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by on 29/03/2016.
 *
 * @author sekwah41
 */
public class FloodingPlugin extends JavaPlugin {

    public boolean simulatingFlood = false;

    public ConfigAccessor config;

    public VisualDebug visualDebug;

    public void onEnable(){

        visualDebug = new VisualDebug(this);

        saveDefaultConfig();

        config = new ConfigAccessor(this, "config.yml");

        new FloodCommand(this);

        this.getServer().getConsoleSender().sendMessage("\u00A7aFlood simulation has been enabled.");
    }

    public void onDisable(){
        this.getServer().getConsoleSender().sendMessage("\u00A7cFlood simulation has been disabled.");
    }

}
