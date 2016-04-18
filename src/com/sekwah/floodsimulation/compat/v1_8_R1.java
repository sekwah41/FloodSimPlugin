package com.sekwah.floodsimulation.compat;

import net.minecraft.server.v1_8_R1.BlockPosition;
import net.minecraft.server.v1_8_R1.PacketPlayOutWorldEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Created by on 18/04/2016.
 *
 * @author sekwah41
 */
public class v1_8_R1 implements NMS {

    @Override
    public void sendBlockBreakParticles(Player p, Material mat, Location pos) {
        PacketPlayOutWorldEvent packet = new PacketPlayOutWorldEvent(2001, new BlockPosition(pos.getX(), pos.getY(), pos.getZ()), mat.getId(), false);
        ((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
    }
}
