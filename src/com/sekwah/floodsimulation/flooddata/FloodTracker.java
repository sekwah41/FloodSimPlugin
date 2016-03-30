package com.sekwah.floodsimulation.flooddata;

import com.sekwah.floodsimulation.FloodingPlugin;
import net.minecraft.server.v1_8_R2.Block;

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

    private FloodingPlugin plugin;

    public ChunkPos[] regionPoints = {null, null};

    /**
     * Stores if the plugin is simulating but other than it tracking the code it is used as a trigger to block changes
     * e.g. natural water flow.
     *
     */
    public boolean simulating = false;

    public boolean lockChanges = false;

    public List<Block> waterBlocks = new ArrayList<Block>();

    public FloodTracker(FloodingPlugin plugin){
        this.plugin = plugin;
    }

    public boolean setPos(int posID, int posX, int poxZ){
        if(simulating){
            return false;
        }
        regionPoints[posID] = new ChunkPos(posX,poxZ);
        return true;
    }

    public void calculate(){
        lockChanges = true;

    }

}
