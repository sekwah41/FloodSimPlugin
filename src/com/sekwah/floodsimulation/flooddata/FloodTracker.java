package com.sekwah.floodsimulation.flooddata;

import com.sekwah.floodsimulation.FloodingPlugin;
import com.sekwah.floodsimulation.Pressures;
import com.sun.scenario.effect.Flood;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private float waterThreshold = 100f / 8f;

    private boolean debug = true;

    // Update frequency in ticks(Keep tracked to the world).
    private int updateFrequency = 5;

    /**
     * Stores if the plugin is simulating but other than it tracking the code it is used as a trigger to block changes
     * e.g. natural water flow.
     *
     */
    public boolean simulating = false;

    public boolean lockChanges = false;

    private long lastUpdateTick = 0;

    /**
     * Use metadata on blocks to store where in the list their active data is. That or use a hashmap.
     */

    private List<FloodSource> floodSources = new ArrayList<FloodSource>();

    private Map<Block, WaterData> floodData = new HashMap<Block, WaterData>();

    private List<WaterData> activeWaterBlocks = new ArrayList<WaterData>();

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
    public boolean addActiveBlock(WaterData waterBlock){
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

    /**
     * Shows how the plugin sees the region before starting.
     */
    public void visualise(){
        lockChanges = true;
        this.rearrangeRegion();
        this.plugin.visualDebug.showRegion();
        //this.analyzeWater();
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
            @Override
            public void run() {
                visualRegionData();
            }
        });
    }

    public boolean visualRegionData(){
        // Return false if no water flood sources are calculated.
        ArrayList<VisuBlock> blocks = new ArrayList<VisuBlock>();

        int worldHeight = this.currentWorld.getMaxHeight();
        for(int x = this.regionPoints[0].posX; x <= this.regionPoints[1].posX; x++){
            for(int z = this.regionPoints[0].posZ; z <= this.regionPoints[1].posZ; z++){
                for(int y = 0; y <= worldHeight; y++){
                    Block block = this.currentWorld.getBlockAt(x,y,z);
                    if(block.getType() == Material.STATIONARY_WATER){
                        //this.plugin.getLogger().info(String.valueOf(block.getData()));
                        if (block.getData() == (byte) 0) {
                            Block blockAbove = block.getRelative(BlockFace.UP);
                            if(blockAbove.getType() == Material.STATIONARY_WATER && blockAbove.getData() == (byte) 0){
                                blocks.add(new VisuBlock(new Location(plugin.floodTracker.currentWorld, x,y,z), Material.STAINED_GLASS, (byte) 5));
                            }
                            else{
                                blocks.add(new VisuBlock(new Location(plugin.floodTracker.currentWorld, x,y,z), Material.STAINED_GLASS, (byte) 4));
                            }
                        }
                        else{
                            blocks.add(new VisuBlock(new Location(plugin.floodTracker.currentWorld, x,y,z), Material.STAINED_GLASS, (byte) 7));
                        }
                    }
                    // Be treated like a full block, im yet to see this actually be found though.
                    else if(block.getType() == Material.WATER){
                        blocks.add(new VisuBlock(new Location(plugin.floodTracker.currentWorld, x,y,z), Material.STAINED_GLASS, (byte) 2));
                    }
                    else if(block.getType() == infWaterSource){
                        blocks.add(new VisuBlock(new Location(plugin.floodTracker.currentWorld, x,y,z), Material.STAINED_GLASS, (byte) 9));

                    }
                    //
                }
            }
        }
        this.plugin.visualDebug.showBlocks(blocks);
        return true;
    }

    public boolean analyzeWater(){
        // Return false if no water flood sources are calculated.

        int worldHeight = this.currentWorld.getMaxHeight();
        for(int x = this.regionPoints[0].posX; x <= this.regionPoints[1].posX; x++){
            for(int z = this.regionPoints[0].posZ; z <= this.regionPoints[1].posZ; z++){
                for(int y = 0; y <= worldHeight; y++){
                    Block block = this.currentWorld.getBlockAt(x,y,z);
                    if(block.getType() == Material.STATIONARY_WATER){
                        //this.plugin.getLogger().info(String.valueOf(block.getData()));
                        if (block.getData() == (byte) 0) {
                            Block blockAbove = block.getRelative(BlockFace.UP);
                            if(blockAbove.getType() == Material.STATIONARY_WATER && blockAbove.getData() == (byte) 0){
                                activeWaterBlocks.add(new WaterData(new FloodPos(x,y,z), 100));
                            }
                            else{
                                activeWaterBlocks.add(new WaterData(new FloodPos(x,y,z), 100));
                                floodSources.add(new FloodSource(new FloodPos(x,y,z), false));
                            }
                        }
                        else{
                            addWater(new FloodPos(x,y,z), ((8 - block.getData()) * waterThreshold), block);
                            //activeWaterBlocks.add(new WaterData(new FloodPos(x,y,z), ((8 - block.getData()) * waterThreshold)));
                        }
                    }
                    // Be treated like a full block, im yet to see this actually be found though.
                    else if(block.getType() == Material.WATER){
                        activeWaterBlocks.add(new WaterData(new FloodPos(x,y,z), 100));
                        floodSources.add(new FloodSource(new FloodPos(x,y,z), false));
                    }
                    else if(block.getType() == infWaterSource){
                        floodSources.add(new FloodSource(new FloodPos(x,y,z), true));
                    }
                    //
                }
            }
        }
        return true;
    }

    public void addWater(FloodPos pos, float level, Block block){
        WaterData waterData = new WaterData(pos, level);
        floodData.put(block, waterData);
    }

    public void removeWater(WaterData waterData, Block block){
        activeWaterBlocks.remove(waterData);
        floodData.remove(block);
    }

    /**
     * Start the flood
     */
    public boolean start() {
        if(!this.simulating){
            this.rearrangeRegion();
            plugin.getServer().broadcastMessage("\u00A79Flood>\u00A7f Flood simulation started.");
            this.simulating = true;
            this.analyzeWater();
            this.lastUpdateTick = currentWorld.getFullTime();

            update();

            return true;
        }
        else{
            return false;
        }

    }

    /**
     * Rise the flood levels and add more water(raise flood level if current flood block is full and can move up.
     *
     * @return if the flood could be risen.
     */
    public boolean raiseFlood(){
        return true;
    }


    /**
     * Updates all the water
     * @return if there are changes made.
     */
    public boolean update(){

        plugin.getLogger().info("Flood Update");
        updateBlocks();

        plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
            @Override
            public void run() {
                simulateWater();

                long tickDelay = updateFrequency - (lastUpdateTick - currentWorld.getFullTime());

                if(tickDelay > 0){
                    plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
                        @Override
                        public void run() {
                            update();
                        }
                    }, tickDelay);
                }
                else{
                    plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            update();
                        }
                    });
                }
            }
        });

        return true;
    }

    private void simulateWater() {
        Object[] activeArray = activeWaterBlocks.toArray();
        for(int i = activeArray.length - 1; i >= 0; i--){
            WaterData waterData = (WaterData) activeArray[i];
            if(waterData.level > 0){
                updateBlock(waterData);
            }
        }
    }

    /**
     * Block update code
     * @param waterData
     */
    private void updateBlock(WaterData waterData) {
        FloodPos pos = waterData.pos;
        Block block = this.currentWorld.getBlockAt(pos.posX, pos.posY, pos.posZ);
        //Block blockBelow =
    }

    public void updateBlocks() {
        Object[] activeWater = activeWaterBlocks.toArray();
        for(Object waterDataObj : activeWater){
            WaterData waterData = (WaterData) waterDataObj;
            FloodPos pos = waterData.pos;
            Block block = this.currentWorld.getBlockAt(pos.posX, pos.posY, pos.posZ);
            if(block.getType() == Material.STATIONARY_WATER){
                double waterLevel = 8 - Math.ceil(waterData.level / this.waterThreshold);
                if(waterLevel >= 8){
                    block.breakNaturally();
                    removeWater(waterData, block);
                }
                else {
                    if (waterLevel < 0) {
                        waterLevel = 0;
                    }

                    if (waterLevel != block.getData()) {
                        block.setData((byte) waterLevel);
                    }
                }
            }
            else{
                removeWater(waterData, block);
                plugin.getLogger().info("A non water block seems to be in the water data.");
            }
        }
    }
}
