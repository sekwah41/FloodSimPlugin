package com.sekwah.floodsimulation.flooddata;

import com.sekwah.floodsimulation.FloodingPlugin;

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

    public FloodTracker(FloodingPlugin plugin){
        this.plugin = plugin;
    }

}
