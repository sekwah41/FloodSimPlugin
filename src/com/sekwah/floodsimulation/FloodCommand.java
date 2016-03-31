package com.sekwah.floodsimulation;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
                Player player = (Player) sender;
                Location loc = player.getLocation();
                if(plugin.floodTracker.setPos(0, loc.getBlockX(), loc.getBlockZ())){
                    sender.sendMessage("\u00A79Flood>\u00A7f Pos 1 Set.");
                }
                else{
                    sender.sendMessage("\u00A79Flood>\u00A7f The region cannot be set right now.");
                }
            }
            else if (args[0].toLowerCase().equals("pos2")) {
                Player player = (Player) sender;
                Location loc = player.getLocation();
                if(plugin.floodTracker.setPos(1, loc.getBlockX(), loc.getBlockZ())){
                    sender.sendMessage("\u00A79Flood>\u00A7f Pos 2 Set.");
                }
                else{
                    sender.sendMessage("\u00A79Flood>\u00A7f The region cannot be set right now.");
                }
            }
            else if (args[0].toLowerCase().equals("calculate")) {
                sender.sendMessage("\u00A79Flood>\u00A7f Calculating flood data, please wait.");
                plugin.floodTracker.calculate();
            }
        }
        else{
            sender.sendMessage("\u00A79Flood>\u00A7f You need to type an argument.");
        }

        return true;
    }

    // Not needed but makes it easier to use.
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String command, String[] args) {
        List<String> autoComplete = new ArrayList<String>();
        if (args.length == 1) {
            autoComplete.addAll(Arrays.asList("pos1", "pos2", "calculate"));
        }
        Collections.sort(autoComplete);
        return autoComplete;
    }
}
