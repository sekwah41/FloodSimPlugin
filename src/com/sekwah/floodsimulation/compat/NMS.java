package com.sekwah.floodsimulation.compat;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public interface NMS {

    void sendBlockBreakParticles(Player p, Material mat, Location pos);
}
