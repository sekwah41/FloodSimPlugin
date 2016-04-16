package com.sekwah.floodsimulation;

import com.sekwah.floodsimulation.flooddata.VisuBlock;
import net.minecraft.server.v1_8_R2.Blocks;
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

    /*@SuppressWarnings("deprecation")
    public static void Show(final Player player, final AdvancedPortalsPlugin plugin) {

        int LowX = 0;
        int LowY = 0;
        int LowZ = 0;

        int HighX = 0;
        int HighY = 0;
        int HighZ = 0;

        if (player.getMetadata("Pos1X").get(0).asInt() > player.getMetadata("Pos2X").get(0).asInt()) {
            LowX = player.getMetadata("Pos2X").get(0).asInt();
            HighX = player.getMetadata("Pos1X").get(0).asInt();
        } else {
            LowX = player.getMetadata("Pos1X").get(0).asInt();
            HighX = player.getMetadata("Pos2X").get(0).asInt();
        }
        if (player.getMetadata("Pos1Y").get(0).asInt() > player.getMetadata("Pos2Y").get(0).asInt()) {
            LowY = player.getMetadata("Pos2Y").get(0).asInt();
            HighY = player.getMetadata("Pos1Y").get(0).asInt();
        } else {
            LowY = player.getMetadata("Pos1Y").get(0).asInt();
            HighY = player.getMetadata("Pos2Y").get(0).asInt();
        }
        if (player.getMetadata("Pos1Z").get(0).asInt() > player.getMetadata("Pos2Z").get(0).asInt()) {
            LowZ = player.getMetadata("Pos2Z").get(0).asInt();
            HighZ = player.getMetadata("Pos1Z").get(0).asInt();
        } else {
            LowZ = player.getMetadata("Pos1Z").get(0).asInt();
            HighZ = player.getMetadata("Pos2Z").get(0).asInt();
        }

        final Location pos1 = new Location(player.getWorld(), LowX, LowY, LowZ);
        final Location pos2 = new Location(player.getWorld(), HighX, HighY, HighZ);

		*//*
		 * There are alot of for loops at the moment, when i find an easier way to do these other that a load of if statements
		 * then i will change it, but for now its the best way i can think of for doing this.
		 *//*

        for (int x = LowX; x <= HighX; x++) {
            Location loc = new Location(player.getWorld(), x, LowY, LowZ);
            player.sendBlockChange(loc, blockType, metadata);
        }
        for (int x = LowX; x <= HighX; x++) {
            Location loc = new Location(player.getWorld(), x, LowY, HighZ);
            player.sendBlockChange(loc, blockType, metadata);
        }
        for (int z = LowZ; z <= HighZ; z++) {
            Location loc = new Location(player.getWorld(), LowX, LowY, z);
            player.sendBlockChange(loc, blockType, metadata);
        }
        for (int z = LowZ; z <= HighZ; z++) {
            Location loc = new Location(player.getWorld(), HighX, LowY, z);
            player.sendBlockChange(loc, blockType, metadata);
        }
        for (int y = LowY; y <= HighY; y++) {
            Location loc = new Location(player.getWorld(), LowX, y, LowZ);
            player.sendBlockChange(loc, blockType, metadata);
        }
        for (int y = LowY; y <= HighY; y++) {
            Location loc = new Location(player.getWorld(), LowX, y, HighZ);
            player.sendBlockChange(loc, blockType, metadata);
        }
        for (int y = LowY; y <= HighY; y++) {
            Location loc = new Location(player.getWorld(), HighX, y, LowZ);
            player.sendBlockChange(loc, blockType, metadata);
        }
        for (int y = LowY; y <= HighY; y++) {
            Location loc = new Location(player.getWorld(), HighX, y, HighZ);
            player.sendBlockChange(loc, blockType, metadata);
        }
        for (int x = LowX; x <= HighX; x++) {
            Location loc = new Location(player.getWorld(), x, HighY, HighZ);
            player.sendBlockChange(loc, blockType, metadata);
        }
        for (int x = LowX; x <= HighX; x++) {
            Location loc = new Location(player.getWorld(), x, HighY, LowZ);
            player.sendBlockChange(loc, blockType, metadata);
        }
        for (int z = LowZ; z <= HighZ; z++) {
            Location loc = new Location(player.getWorld(), LowX, HighY, z);
            player.sendBlockChange(loc, blockType, metadata);
        }
        for (int z = LowZ; z <= HighZ; z++) {
            Location loc = new Location(player.getWorld(), HighX, HighY, z);
            player.sendBlockChange(loc, blockType, metadata);
        }


        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                Selection.Hide(player, plugin, pos1, pos2);
            }
        }, timeout * 20);

    }

    @SuppressWarnings("deprecation")
    protected static void Hide(Player player, AdvancedPortalsPlugin plugin, Location pos1, Location pos2) {

        int LowX = pos1.getBlockX();
        int LowY = pos1.getBlockY();
        int LowZ = pos1.getBlockZ();

        int HighX = pos2.getBlockX();
        int HighY = pos2.getBlockY();
        int HighZ = pos2.getBlockZ();

        for (int x = LowX; x <= HighX; x++) {
            Location loc = new Location(player.getWorld(), x, LowY, LowZ);
            player.sendBlockChange(loc, loc.getBlock().getType(), loc.getBlock().getData());
        }
        for (int x = LowX; x <= HighX; x++) {
            Location loc = new Location(player.getWorld(), x, LowY, HighZ);
            player.sendBlockChange(loc, loc.getBlock().getType(), loc.getBlock().getData());
        }
        for (int z = LowZ; z <= HighZ; z++) {
            Location loc = new Location(player.getWorld(), LowX, LowY, z);
            player.sendBlockChange(loc, loc.getBlock().getType(), loc.getBlock().getData());
        }
        for (int z = LowZ; z <= HighZ; z++) {
            Location loc = new Location(player.getWorld(), HighX, LowY, z);
            player.sendBlockChange(loc, loc.getBlock().getType(), loc.getBlock().getData());
        }
        for (int y = LowY; y <= HighY; y++) {
            Location loc = new Location(player.getWorld(), LowX, y, LowZ);
            player.sendBlockChange(loc, loc.getBlock().getType(), loc.getBlock().getData());
        }
        for (int y = LowY; y <= HighY; y++) {
            Location loc = new Location(player.getWorld(), LowX, y, HighZ);
            player.sendBlockChange(loc, loc.getBlock().getType(), loc.getBlock().getData());
        }
        for (int y = LowY; y <= HighY; y++) {
            Location loc = new Location(player.getWorld(), HighX, y, LowZ);
            player.sendBlockChange(loc, loc.getBlock().getType(), loc.getBlock().getData());
        }
        for (int y = LowY; y <= HighY; y++) {
            Location loc = new Location(player.getWorld(), HighX, y, HighZ);
            player.sendBlockChange(loc, loc.getBlock().getType(), loc.getBlock().getData());
        }
        for (int x = LowX; x <= HighX; x++) {
            Location loc = new Location(player.getWorld(), x, HighY, HighZ);
            player.sendBlockChange(loc, loc.getBlock().getType(), loc.getBlock().getData());
        }
        for (int x = LowX; x <= HighX; x++) {
            Location loc = new Location(player.getWorld(), x, HighY, LowZ);
            player.sendBlockChange(loc, loc.getBlock().getType(), loc.getBlock().getData());
        }
        for (int z = LowZ; z <= HighZ; z++) {
            Location loc = new Location(player.getWorld(), LowX, HighY, z);
            player.sendBlockChange(loc, loc.getBlock().getType(), loc.getBlock().getData());
        }
        for (int z = LowZ; z <= HighZ; z++) {
            Location loc = new Location(player.getWorld(), HighX, HighY, z);
            player.sendBlockChange(loc, loc.getBlock().getType(), loc.getBlock().getData());
        }

    }*/
}
