package com.sekwah.floodsimulation.flooddata;

import com.sekwah.floodsimulation.FloodingPlugin;
import com.sekwah.floodsimulation.Pressures;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.*;

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
 *
 * Has details on checking the server time.
 * https://bukkit.org/threads/solved-counting-ticks.44950/
 *
 * Need to be more careful about how the variables reference back to the origional.
 *
 * @author sekwah41
 */
public class FloodTracker {

    private final Pressures pressures;

    private FloodingPlugin plugin;

    public ChunkPos[] regionPoints = {null, null};

    public World currentWorld;

    public boolean infWaterSourceTest = true;

    public Material infWaterSource = Material.LAPIS_BLOCK;

    private float waterThreshold = 100f / 8f;

    public boolean debug = false;

    // Update frequency in ticks(Keep tracked to the world).
    private int updateFrequency = 4;

    /**
     * Stores if the plugin is simulating but other than it tracking the code it is used as a trigger to block changes
     * e.g. natural water flow.
     *
     */
    public boolean simulating = false;

    public boolean lockChanges = false;

    public long lastUpdateTick = 0;

    // Stops some blocks like glass breaking too easily.
    public float waterStartLevel = 100f;

    /**
     * Percentage that flows into the next blow, e.g. 100 and 50 will have 12.5 flow into the next if it was 0.25f
     *
     * 1 would mean it all goes and that would give some funny results.
     */
    private float flowRatio = 0.5f;

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
        this.pressures = new Pressures();
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
                                //activeWaterBlocks.add(new WaterData(new FloodPos(x,y,z), 100));
                                addWater(new FloodPos(x,y,z), this.waterStartLevel, block);
                            }
                            else{
                                addWaterSource(new FloodPos(x,y,z), this.waterStartLevel, block, false);
                            }
                        }
                        else{
                            addWater(new FloodPos(x,y,z), ((8 - block.getData()) * waterThreshold), block);
                            //activeWaterBlocks.add(new WaterData(new FloodPos(x,y,z), ((8 - block.getData()) * waterThreshold)));
                        }
                    }
                    // Be treated like a full block, im yet to see this actually be found though.
                    else if(block.getType() == Material.WATER){
                        addWaterSource(new FloodPos(x,y,z), this.waterStartLevel, block, false);
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

    public WaterData addWater(FloodPos pos, float level, Block block){
        WaterData waterData = floodData.get(block);
        if(waterData == null){
            waterData = new WaterData(pos, level);
            floodData.put(block, waterData);
            activeWaterBlocks.add(waterData);
        }
        else{
            waterData.level = level;
        }
        return waterData;
    }

    public WaterData addWaterSource(FloodPos pos, float level, Block block, boolean permaSource){
        floodSources.add(new FloodSource(pos, permaSource));
        return addWater(pos,level,block);
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
            this.lastUpdateTick = getTimeTicks();

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
     * Maybe recode to an async while loop or find an event that updates every game tick to see if it should update again.
     * It would remove the possibility of some sort of recursive call issue.
     *
     * Updates all the water
     * @return if there are changes made.
     */
    public boolean update(){

        if(debug) plugin.getLogger().info("Flood Update Call");

        if(debug) plugin.getLogger().info("------  Updating blocks  ------");

        updateFloodSource();

        updateBlocks();

        if(debug) plugin.getLogger().info("------  Done updating blocks  ------");

        plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
            @Override
            public void run() {
                simulateWater();
                if(debug) plugin.getLogger().info("lastUpdateTick: " + plugin.floodTracker.lastUpdateTick);
                if(debug) plugin.getLogger().info("Time: " + getTimeTicks());

                // Timing function, finds how long the last update took, then calculates the time offset to the next

                long tickOffset = getTimeTicks() - (plugin.floodTracker.lastUpdateTick + plugin.floodTracker.updateFrequency);

                long tickDelay = updateFrequency - tickOffset;

                if(tickDelay > 10){
                    tickDelay = 4;
                    tickOffset = 0;
                }

                plugin.floodTracker.lastUpdateTick = getTimeTicks() - tickOffset;

                if(debug) plugin.getLogger().info("Delay: " + tickDelay);

                if(tickDelay > 0){
                    if(debug) plugin.getLogger().warning("Server could not simulate within the update time.");
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

    private void updateFloodSource() {
        if(infWaterSourceTest){
            for(FloodSource floodSource : floodSources){
                if(floodSource.permaSource){
                    FloodPos pos = floodSource.pos;
                    Block block = currentWorld.getBlockAt(pos.posX, pos.posY, pos.posZ);

                    Block[] sideBlocks = {block.getRelative(BlockFace.NORTH),block.getRelative(BlockFace.SOUTH),block.getRelative(BlockFace.EAST),
                            block.getRelative(BlockFace.WEST)};

                    for(int i = 0; i < 4; i++){
                        flowToBlock(sideBlocks[i], 100, true, 0);
                        // System.out.println(i);
                        //System.out.println(flow);
                    }
                }
            }
        }
    }

    private void simulateWater() {
        Object[] activeArray = floodData.keySet().toArray();
        for(int i = activeArray.length - 1; i >= 0; i--){
            WaterData waterData = floodData.get(activeArray[i]);
            if(waterData.level > 0){
                while(true){
                    try{
                        updateBlock(waterData);
                        break;
                    }
                    catch(IllegalStateException e){
                        System.out.println("State Exeption");
                    }
                }
            }
        }
    }

    /**
     * Time since epoch in ticks :P
     * @return
     */
    public long getTimeTicks(){
        return System.currentTimeMillis() / 50l;
    }

    public void updateBlocks() {
        Object[] activeWater = activeWaterBlocks.toArray();
        for(Object waterDataObj : activeWater){
            WaterData waterData = (WaterData) waterDataObj;
            FloodPos pos = waterData.pos;
            Block block = this.currentWorld.getBlockAt(pos.posX, pos.posY, pos.posZ);
            boolean isWater = block.getType() == Material.STATIONARY_WATER || block.getType() == Material.WATER;
            if(isWater || waterData.newBlock){
                if(waterData.newBlock){
                    if(!isWater){
                        for(Player player: plugin.getServer().getOnlinePlayers()){
                            plugin.compat.sendBlockBreakParticles(player, block.getType(), block.getLocation());
                        }
                    }
                    block.setType(Material.STATIONARY_WATER, false);
                    waterData.newBlock = false;
                }

                double waterLevel = 8 - Math.ceil(waterData.level / this.waterThreshold);
                if(waterLevel >= 8){
                    //block.breakNaturally();
                    block.setType(Material.AIR, false);
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
                waterData.hasChanged = false;
                waterData.inactiveTicks++;
            }
            else{
                removeWater(waterData, block);
                if(debug) plugin.getLogger().info("A non water block seems to be in the water data.");
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
        Block below = block.getRelative(BlockFace.DOWN);

        // TODO replace this cheaty method.
        float inflateAmount = 1f;

        float flow = flowToBlock(below, waterData.level, true, waterData.inactiveTicks);
        if(flow > 0){
            flow *= inflateAmount;
        }
        waterData.change(-flow);

        if(debug) plugin.getLogger().info("Updating block");

        Block[] sideBlocks = {block.getRelative(BlockFace.NORTH),block.getRelative(BlockFace.SOUTH),block.getRelative(BlockFace.EAST),
                block.getRelative(BlockFace.WEST)};

        /*WaterData[] waterDataSides = new WaterData[4];

        for(int i = 0; i < 4; i++){
            waterDataSides[i] = this.floodData.get(sideBlocks[i]);
        }*/

        for(int i = 0; i < 4; i++){
            if(waterData.level > 5){
                flow = flowToBlock(sideBlocks[i], waterData.level, false, waterData.inactiveTicks);
                // TODO make more efficient and maybe remove this cheaty flood code to be more realistic
                if(flow > 0){
                    flow *= inflateAmount;
                }
                waterData.change(-flow);
                //waterData.level -= flow;
            }
            else if(waterData.inactiveTicks > 50   /* && below.getType() != Material.STATIONARY_WATER*/){
                waterData.level = 0.0f;
            }
            // System.out.println(i);
            //System.out.println(flow);
        }




        // TODO Code to average them all and some other stuff to make it more realistic



    }



    /**
     * Tries to flow an amount into a block.
     *
     * TODO to combat some of the linear flowing try also pulling from blocks with a higher fill value. this would speed
     * up linear flowing between active blocks but not to new air blocks(would possiby create better vertical flow).
     *
     * Also possible check nearby for blocks to the side flowing down and/or drain if below a certain amount for too long.
     *
     * @return the amout that has flowed into the block.
     */
    public float flowToBlock(Block block, float amount, boolean down, int inactiveTicks){
        WaterData waterData = floodData.get(block);
        if(waterData == null){
            // TODO add the pressure code here.
            Integer pressureVal = pressures.pressureValues.get(block.getType());
            if(block.getType() == Material.AIR){
                waterData = addWater(new FloodPos(block.getX(), block.getY(), block.getZ()), 0, block);
            }
            else if(pressureVal != null && pressureVal <= amount && ((pressureVal > 75 && inactiveTicks > 2) || pressureVal <= 75)){
                waterData = addWater(new FloodPos(block.getX(), block.getY(), block.getZ()), 0, block);
            }
            else{
                return 0;
            }
        }
        float amountRemaining = 100 - waterData.level;
        if(down){
            // Max flow down amount(stops instant 1 block flowing down)
            if(amount >= 20){
                amount = 80;
            }
            if(amount >= 80){
                amount = 80;
            }
            if(amountRemaining >= amount){
                waterData.level += amount;
                return amount;
            }
            else{
                waterData.level = 100;
                return amountRemaining;
            }
        }
        else{
            float flowAmount = (amount - waterData.level) * flowRatio;
            //if(flowAmount > 5){
            waterData.change(flowAmount);
            return flowAmount;
            //}
        }
        //return 0;
    }
}
