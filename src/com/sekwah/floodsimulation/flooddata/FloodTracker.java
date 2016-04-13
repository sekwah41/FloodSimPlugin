package com.sekwah.floodsimulation.flooddata;

import com.sekwah.floodsimulation.FloodingPlugin;
import com.sekwah.floodsimulation.Pressures;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by on 29/03/2016.
 *
 * Try methods such as hashmaps and 3d arrays to track the water. Probably best to store metadata and use a list for
 * currently active blocks.
 *
 * (Can use block change packets to show where the updates are taking place if needed)
 *
 * @author sekwah41
 */
public class FloodTracker {

    private final Pressures pressureVal;

    private FloodingPlugin plugin;

    public ChunkPos[] regionPoints = {null, null};

    /**
     * Stores if the plugin is simulating but other than it tracking the code it is used as a trigger to block changes
     * e.g. natural water flow.
     *
     */
    public boolean simulating = true;

    public boolean lockChanges = false;

    public List<Block> activeWaterBlocks = new ArrayList<Block>();

    // Create a thread and try multithreading the block updates. Other than players placing and breaking blocks nothing
    // should really change.

    public FloodTracker(FloodingPlugin plugin){
        this.plugin = plugin;
        this.pressureVal = new Pressures();
    }

    /**
     * Typically because the block has been full for a few ticks so should be fine in most cases.
     *
     * @return if block was added successfully.
     */
    public boolean addActiveBlock(Block waterBlock){
        if(activeWaterBlocks.contains(waterBlock)){
            return false;
        }
        else{
            activeWaterBlocks.add(waterBlock);
            return true;
        }
    }

    /**
     * Sets the locations for the regions
     * @param posID 0 is pos1 1 is pos2
     * @param posX
     * @param poxZ
     * @return
     */
    public boolean setPos(int posID, int posX, int poxZ){
        if(simulating){
            return false;
        }
        regionPoints[posID] = new ChunkPos(posX,poxZ);
        return true;
    }


    public boolean isPosSet(int posID){
        return regionPoints[posID] != null;
    }

    /**
     * Sorts pos 1 and 2 to min and max values for easier handling.
     * @return
     */
    public boolean rearrangeRegion(){
        if(regionPoints[0] != null && regionPoints[1] != null){
            int maxX = Math.max(regionPoints[0].posX, regionPoints[1].posX);
            int minX = Math.min(regionPoints[0].posX, regionPoints[1].posX);

            int maxZ = Math.max(regionPoints[0].posZ, regionPoints[1].posZ);
            int minZ = Math.min(regionPoints[0].posZ, regionPoints[1].posZ);

            regionPoints[0] = new ChunkPos(minX, minZ);
            regionPoints[1] = new ChunkPos(maxX, maxZ);

            return true;
        }
        return false;
    }

    public void calculate(){
        lockChanges = true;
        rearrangeRegion();
        analyzeWater();
    }

    public boolean analyzeWater(){
        // Return false if no water flood sources are calculated.
        return true;
    }
}
