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

        if (args.length > 0) {
            if (args[0].toLowerCase().equals("pos1")) {

            }
        }
        else{
            sender.sendMessage("\u00A79Flood\u00A7f> You need to type an argument.");
        }

        return true;
    }

    // Not needed but makes it easier to use.
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String command, String[] args) {
        return null;
    }
}
