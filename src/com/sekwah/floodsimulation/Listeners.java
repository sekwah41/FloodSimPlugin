package com.sekwah.floodsimulation;

import com.sekwah.floodsimulation.flooddata.FloodPos;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

/**
 * Created by on 30/03/2016.
 *
 * TODO listen to the place and destroy
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
        if(plugin.floodTracker.inRegion(block.getLocation()) || plugin.floodTracker.inRegion(blockTo.getLocation())){
            event.setCancelled(true);
        }

        // TODO Stop natural water flow in the area if currently flooding.
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        Material material = block.getType();
        //System.out.println(material);
        if (material == Material.STATIONARY_WATER) {
            if(plugin.floodTracker.inRegion(block.getLocation()) || plugin.floodTracker.inRegion(block.getLocation())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(PlayerBucketEmptyEvent event) {
        Block block = event.getBlockClicked().getRelative(event.getBlockFace());
        Material material = event.getBucket();
        //System.out.println(material);
        if (material.toString().contains("WATER")) {
            if(plugin.floodTracker.inRegion(block.getLocation()) || plugin.floodTracker.inRegion(block.getLocation())) {
                plugin.floodTracker.addWater(new FloodPos(block.getX(), block.getY(), block.getZ()), 99, block);
            }
        }

    }

    /*@EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Material material = block.getType();
        //System.out.println(material);
        if (material == Material.STATIONARY_WATER) {
            if(plugin.floodTracker.inRegion(block.getLocation()) || plugin.floodTracker.inRegion(block.getLocation())) {
                plugin.floodTracker.addWater(new FloodPos(block.getX(), block.getY(), block.getZ()), 100, block);
            }
        }

    }*/

    @EventHandler(priority = EventPriority.HIGH)
    public void onExplosion(EntityExplodeEvent event) {

    }

}
