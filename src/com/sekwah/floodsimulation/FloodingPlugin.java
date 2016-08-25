package com.sekwah.floodsimulation;

import com.sekwah.floodsimulation.compat.CraftBukkit;
import com.sekwah.floodsimulation.flooddata.FloodTracker;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by on 29/03/2016.
 *
 * TODO save the current area as a file or copy it somewhere else to revert it afterwards.
 *
 * TODO detect source blocks and make their levels rise too.
 *
 * TODO convert the hashmap of blocks into a 3d array which is linear but using % and / to get the correct data directly.
 * Would also make it so you dont have it flowing out of the area.
 *
 * @author sekwah41
 */
public class FloodingPlugin extends JavaPlugin {

    public ConfigAccessor config;

    public VisualDebug visualDebug;

    public boolean debug = false;

    public FloodTracker floodTracker;

    public CraftBukkit compat;

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
            this.compat = new CraftBukkit(this, version);

            this.getServer().getConsoleSender().sendMessage("\u00A7aFlood simulation has been enabled.");
        } catch (IllegalArgumentException |
                NoSuchFieldException | SecurityException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
            this.getLogger().warning("Something went wrong, please notify sekwah and tell him about this version v:" + version);
            this.getLogger().warning("Along with the above stacktrace");
            this.setEnabled(false);
        }
    }

    public void onDisable(){
        this.getServer().getConsoleSender().sendMessage("\u00A7cFlood simulation has been disabled.");
    }

}
