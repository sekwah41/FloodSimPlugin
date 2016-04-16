package com.sekwah.floodsimulation.flooddata;

import org.bukkit.Location;
import org.bukkit.Material;

/**
 * Created by on 16/04/2016.
 *
 * @author sekwah41
 */
public class VisuBlock {

    public final Location loc;
    public final Material mat;
    public final byte data;

    public VisuBlock(Location loc, Material mat, byte data){
        this.loc = loc;
        this.mat = mat;
        this.data = data;
    }
}
