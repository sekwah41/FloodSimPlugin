package com.sekwah.floodsimulation;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by on 13/04/2016.
 *
 * Allows some blocks such as grass and ladders to block water a little before it breaks it and flows.
 *
 * Need to find a way to naturally break the block as if a player has damaged it. Either study the code, trigger it
 * and drop the normal items if its not possible to call a method.
 *
 * Or activate the setblock command with the destroy tag.
 * Actually use common sense and see how the destroy tag works...
 *
 *
 * Extracted from CommandSetBlock
 * if (args[5].equals("destroy"))
                    {
                        world.destroyBlock(blockpos, true);

                        if (block == Blocks.air)
                        {
                            notifyOperators(sender, this, "commands.setblock.success", new Object[0]);
                            return;
                        }
                    }
                    else if (args[5].equals("keep") && !world.isAirBlock(blockpos))
                    {
                        throw new CommandException("commands.setblock.noChange", new Object[0]);
                    }
 *
 * @author sekwah41
 */
public class Pressures {

    public Map<Material,Integer> pressureValues = new HashMap<Material,Integer>();

    public Pressures(){
        pressureValues.put(Material.GRASS, 20);
        pressureValues.put(Material.RED_ROSE, 20);
        pressureValues.put(Material.YELLOW_FLOWER, 20);
        pressureValues.put(Material.YELLOW_FLOWER, 20);
    }

}
