package com.sekwah.floodsimulation;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

/**
 * Created by on 30/03/2016.
 *
 * @author sekwah41
 */
public class Listeners implements Listener {

    private FloodingPlugin plugin;

    public Listeners(FloodingPlugin plugin){

        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);

    }

    /**
     * Make sure that the plugin changes do not trigger this or are not blocked by this event :)
     * (Hopefully this is just nartural flowing)
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockFromTo(BlockFromToEvent event) {
        // when checking positions check the block and the to block
        Block blockTo = event.getToBlock();
        Block block = event.getBlock();

        // Limit it to the specific region possibly.
        if(plugin.floodTracker.simulating){
            event.setCancelled(true);
        }

        // TODO Stop natural water flow in the area if currently flooding.
    }

}
