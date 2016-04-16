package com.sekwah.floodsimulation.flooddata;

import com.sekwah.floodsimulation.FloodingPlugin;
import com.sekwah.floodsimulation.Pressures;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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
 * Water .data 0 = source block and 7 = almost empty.
 * Use this to visualise if a block is very full or not.
 *
 * Has details on checking the server time.
 * https://bukkit.org/threads/solved-counting-ticks.44950/
 *
 * @author sekwah41
 */
public class FloodTracker {

    private final Pressures pressureVal;

    private FloodingPlugin plugin;

    public ChunkPos[] regionPoints = {null, null};

    public World currentWorld;

    public boolean infWaterSourceTest = true;

    public Material infWaterSource = Material.LAPIS_BLOCK;

    public boolean debug = true;

    /**
     * Stores if the plugin is simulating but other than it tracking the code it is used as a trigger to block changes
     * e.g. natural water flow.
     *
     */
    public boolean simulating = false;

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
        if(this.activeWaterBlocks.contains(waterBlock)){
            return false;
        }
        else{
            this.activeWaterBlocks.add(waterBlock);
            return true;
        }
    }

    /**
     * Checks if the location is in the region. Always false if not simulating.
     * @param loc
     * @return
     */
    public boolean inRegion(Location loc){
        if(this.simulating){
            ChunkPos minPos = this.regionPoints[0];
            ChunkPos maxPos = this.regionPoints[1];
            return loc.getX() >= minPos.posX && loc.getX() <= maxPos.posX &&
                    loc.getZ() >= minPos.posZ && loc.getZ() <= maxPos.posZ;
        }
        return false;
    }


    /*public boolean setPos(int posID, int posX, int poxZ){
        if(this.simulating){
            return false;
        }
        this.regionPoints[posID] = new ChunkPos(posX,poxZ);
        return true;
    }*/

    /**
     * Sets the locations for the regions
     * @param posID 0 is pos1 1 is pos2
     * @param loc
     * @return
     */
    public boolean setPos(int posID, Location loc){
        if(this.simulating){
            return false;
        }
        this.regionPoints[posID] = new ChunkPos(loc.getBlockX(),loc.getBlockZ());
        this.currentWorld = loc.getWorld();
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

            this.regionPoints[0] = new ChunkPos(minX, minZ);
            this.regionPoints[1] = new ChunkPos(maxX, maxZ);

            this.plugin.visualDebug.showRegion();

            return true;
        }
        return false;
    }

    public void calculate(){
        lockChanges = true;
        this.rearrangeRegion();
        //this.analyzeWater();
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
            @Override
            public void run() {
                analyzeWater();
            }
        });
    }

    public boolean analyzeWater(){
        // Return false if no water flood sources are calculated.
        ArrayList<VisuBlock> blocks = new ArrayList<VisuBlock>();

        int worldHeight = this.currentWorld.getMaxHeight();
        for(int x = this.regionPoints[0].posX; x <= this.regionPoints[1].posX; x++){
            for(int z = this.regionPoints[0].posZ; z <= this.regionPoints[1].posZ; z++){
                for(int y = 0; y <= worldHeight; y++){
                    Block block = this.currentWorld.getBlockAt(x,y,z);
                    if(block.getType() == Material.STATIONARY_WATER){
                        //this.plugin.getLogger().info(String.valueOf(block.getData()));
                        System.out.println(block.getData());
                        if (block.getData() == (byte) 0) {
                            blocks.add(new VisuBlock(new Location(plugin.floodTracker.currentWorld, x,y,z), Material.STAINED_GLASS, (byte) 4));
                        }
                        else{
                            blocks.add(new VisuBlock(new Location(plugin.floodTracker.currentWorld, x,y,z), Material.WATER, (byte) 7));
                        }
                    }
                    else if(block.getType() == Material.WATER){
                        blocks.add(new VisuBlock(new Location(plugin.floodTracker.currentWorld, x,y,z), Material.STAINED_GLASS, (byte) 2));
                    }
                    //
                }
            }
        }
        this.plugin.visualDebug.showBlocks(blocks);
        return true;
    }

    /**
     * Start the flood
     */
    public boolean start() {
        if(!this.simulating){
            this.simulating = true;

            return true;
        }
        else{
            return false;
        }

    }
}
