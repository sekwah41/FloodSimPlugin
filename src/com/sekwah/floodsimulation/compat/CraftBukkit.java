package com.sekwah.floodsimulation.compat;

import com.sekwah.floodsimulation.FloodingPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by on 03/08/2016.
 *
 * @author sekwah41
 */
public class CraftBukkit {

    private final String craftBukkitPackage;
    private final String minecraftPackage;

    private final FloodingPlugin plugin;

    private Class<?> packet;
    private Class<?> craftPlayer;
    private Class<?> blockPosition;
    private Class<?> playOutWorldPacket;

    public CraftBukkit(FloodingPlugin plugin, String craftBukkitVer) throws ClassNotFoundException, NoSuchFieldException, NoSuchMethodException {

        this.craftBukkitPackage = "org.bukkit.craftbukkit." + craftBukkitVer + ".";

        this.minecraftPackage = "net.minecraft.server." + craftBukkitVer + ".";

        this.plugin = plugin;

        this.setupCompat();
    }

    private void setupCompat() throws ClassNotFoundException, NoSuchFieldException, NoSuchMethodException {

        this.craftPlayer = Class.forName(craftBukkitPackage + "entity.CraftPlayer");

        this.blockPosition = Class.forName(minecraftPackage + "BlockPosition");
        this.playOutWorldPacket = Class.forName(minecraftPackage + "PacketPlayOutWorldEvent");

        this.packet = Class.forName(minecraftPackage + "Packet");

    }

    public void sendBlockBreakParticles(Player player, Material mat, Location pos) {

        try{

            Object handle = this.craftPlayer.getMethod("getHandle").invoke(player);

            Field playerConnectionObj = handle.getClass().getDeclaredField("playerConnection");

            Constructor<?> packetConstructor = this.playOutWorldPacket.getConstructor(int.class, this.blockPosition, int.class, boolean.class);

            Constructor<?> blockConstructor = this.blockPosition.getConstructor(double.class, double.class, double.class);

            Object packet = packetConstructor.newInstance(2001, blockConstructor.newInstance(pos.getX(), pos.getY(), pos.getZ()), mat.getId(), false);

            Object playerConnection = playerConnectionObj.get(handle);

            playerConnection.getClass().getMethod("sendPacket", this.packet).invoke(playerConnection, packet);


        } catch (IllegalAccessException |InvocationTargetException | NoSuchMethodException | NoSuchFieldException
                | InstantiationException e) {
            this.plugin.getLogger().warning("Error creating raw message, something must be wrong with reflection");
            e.printStackTrace();
        }

        /*PacketPlayOutWorldEvent packet = new PacketPlayOutWorldEvent(2001, new BlockPosition(pos.getX(), pos.getY(), pos.getZ()), mat.getId(), false);
        ((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);*/
    }

}
