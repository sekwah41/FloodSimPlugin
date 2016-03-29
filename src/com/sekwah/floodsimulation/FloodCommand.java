package com.sekwah.floodsimulation;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

/**
 * Created by on 29/03/2016.
 *
 * @author sekwah41
 */
public class FloodCommand implements CommandExecutor, TabCompleter {

    private FloodingPlugin plugin;

    public FloodCommand(FloodingPlugin plugin) {
        this.plugin = plugin;

        plugin.getCommand("flood").setExecutor(this);


    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String command, String[] args) {
        return false;
    }

    // Not needed but makes it easier to use.
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String command, String[] args) {
        return null;
    }
}
