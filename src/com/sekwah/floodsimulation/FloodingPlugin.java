package com.sekwah.floodsimulation;

import com.sekwah.floodsimulation.flooddata.FloodTracker;
import com.sekwah.floodsimulation.compat.NMS;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;

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

    public NMS nmsAccess;

    public FloodTracker floodTracker;

    public void onEnable(){

        visualDebug = new VisualDebug(this);

        saveDefaultConfig();

        config = new ConfigAccessor(this, "config.yml");

        FileConfiguration configAccess = config.getConfig();

        floodTracker = new FloodTracker(this);

        new FloodCommand(this);

        new Listeners(this);

        String packageName = getServer().getClass().getPackage().getName();
        String[] packageSplit = packageName.split("\\.");
        String version = packageSplit[packageSplit.length - 1];

        try {
            Class<?> nmsClass = Class.forName("com.sekwah.floodsimulation.compat." + version);
            if (NMS.class.isAssignableFrom(nmsClass)) {
                this.nmsAccess = (NMS) nmsClass.getConstructor().newInstance();
            } else {
                this.getLogger().severe("Something went wrong, the version of bukkit seems to have an error with its compat file v:" + version);
                this.setEnabled(false);
            }
            this.getLogger().info("Using compat file v:" + version);
        } catch (ClassNotFoundException e) {
            this.getLogger().severe("Something went wrong, the version of bukkit you are using does not seem to have a compat file v:" + version);
            this.setEnabled(false);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException |
                NoSuchMethodException | SecurityException e) {
            this.getLogger().severe("Something went wrong, the version of bukkit seems to have an error with its compat file v:" + version);
             e.printStackTrace();
            this.setEnabled(false);
        }

        this.getServer().getConsoleSender().sendMessage("\u00A7aFlood simulation has been enabled.");
    }

    public void onDisable(){
        this.getServer().getConsoleSender().sendMessage("\u00A7cFlood simulation has been disabled.");
    }

}
