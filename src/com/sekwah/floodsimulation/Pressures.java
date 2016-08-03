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
 * Base it off the blocks size at the end of an update once it has tried pushing in other directions. If its still at 100
 * then stuff like glass will shatter as it obviously has nowhere else to go.
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
        pressureValues.put(Material.LONG_GRASS, 12);
        pressureValues.put(Material.DOUBLE_PLANT, 12);
        pressureValues.put(Material.RED_ROSE, 12);
        pressureValues.put(Material.YELLOW_FLOWER, 12);
        pressureValues.put(Material.DEAD_BUSH, 12);
        pressureValues.put(Material.LADDER, 50);
        pressureValues.put(Material.GLASS, 99);
        pressureValues.put(Material.THIN_GLASS, 80);
        pressureValues.put(Material.STAINED_GLASS, 99);
        pressureValues.put(Material.STAINED_GLASS_PANE, 80);

        //TODO either remove this or add the rest of the doors.
        pressureValues.put(Material.WOOD_DOOR, 99);
    }

}
