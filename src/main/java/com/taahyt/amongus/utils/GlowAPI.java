package com.taahyt.amongus.utils;

import net.minecraft.server.v1_16_R2.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class GlowAPI
{

    // phoenix's idea but made for among us

    public static void addGlowToBlock(Player player, Location location)
    {

        EntityShulker shulker = new EntityShulker(EntityTypes.SHULKER, ((CraftWorld)location.getWorld()).getHandle());
        shulker.setInvisible(true);
        shulker.setNoGravity(true);
        shulker.setCustomNameVisible(false);
        shulker.ai = false;
        shulker.getBukkitEntity().setGlowing(true);
        shulker.setFlag(5, true);
        shulker.setFlag(6, true);

        PacketPlayOutSpawnEntityLiving entity = new PacketPlayOutSpawnEntityLiving(shulker);

        DataWatcher watcher = shulker.getDataWatcher();
        watcher.set(DataWatcherRegistry.a.a(0), (byte) 64);

        PacketPlayOutEntityMetadata data = new PacketPlayOutEntityMetadata(64, shulker.getDataWatcher(), true);


        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(entity);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(data);
    }

}
