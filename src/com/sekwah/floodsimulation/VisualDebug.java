package com.sekwah.floodsimulation;

import com.sekwah.floodsimulation.flooddata.VisuBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by on 29/03/2016.
 *
 * @author sekwah41
 */
public class VisualDebug {

    private Material selectionBlock = Material.STAINED_GLASS;

    private byte selectionMeta = 14;

    public static int displayTimeout = 10;

    private FloodingPlugin plugin;

    public VisualDebug(FloodingPlugin plugin) {
        this.plugin = plugin;
    }

    public void showRegion(){
        ArrayList<VisuBlock> blocks = new ArrayList<VisuBlock>();

        int worldHeight = plugin.floodTracker.currentWorld.getMaxHeight();

        this.addColumn(blocks, plugin.floodTracker.regionPoints[0].posX,plugin.floodTracker.regionPoints[0].posZ, worldHeight,
                selectionBlock, selectionMeta);

        this.addColumn(blocks, plugin.floodTracker.regionPoints[0].posX,plugin.floodTracker.regionPoints[1].posZ, worldHeight,
                selectionBlock, selectionMeta);

        this.addColumn(blocks, plugin.floodTracker.regionPoints[1].posX,plugin.floodTracker.regionPoints[0].posZ, worldHeight,
                selectionBlock, selectionMeta);

        this.addColumn(blocks, plugin.floodTracker.regionPoints[1].posX,plugin.floodTracker.regionPoints[1].posZ, worldHeight,
                selectionBlock, selectionMeta);

        this.showBlocks(blocks);
    }

    private void addColumn(ArrayList<VisuBlock> blocks, int posX, int posZ, int height, Material mat, byte data) {
        for(int i = 0; i < height; i++){
            blocks.add(new VisuBlock(new Location(plugin.floodTracker.currentWorld, posX,i,posZ), mat, data));
        }
    }

    /**
     * Pointless decaprecation. Works at at least.
     *
     * @param blocks
     */
    public void showBlocks(final ArrayList<VisuBlock> blocks){

        for(Player player: plugin.getServer().getOnlinePlayers()){
            for(VisuBlock block : blocks){
                player.sendBlockChange(block.loc, block.mat, block.data);
            }
        }

        plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
            public void run() {
                hideBlocks(blocks);
            }
        }, displayTimeout * 20);
    }

    /**
     * Gets the true data for the specific block and replaces the normal block.
     * @param blocks
     */
    public void hideBlocks(ArrayList<VisuBlock> blocks){

        for(Player player: plugin.getServer().getOnlinePlayers()){
            for(VisuBlock block : blocks){
                Location loc = block.loc;
                player.sendBlockChange(loc, loc.getBlock().getType(), loc.getBlock().getData());
            }
        }
    }
}
