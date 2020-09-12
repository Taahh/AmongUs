package com.taahyt.amongus.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import com.taahyt.amongus.AmongUs;
import com.taahyt.amongus.customization.Kit;
import com.taahyt.amongus.game.player.AUPlayer;
import net.minecraft.server.v1_16_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R2.CraftServer;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_16_R2.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class NMSUtils
{

    public static void spawnCorpse(Player corpsePlayer, Location loc, Collection<Player> players)
    {
        MinecraftServer ms = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer ws = ((CraftWorld)corpsePlayer.getWorld()).getHandle();

        Kit kit = AmongUs.get().getGame().getPlayer(corpsePlayer.getUniqueId()).getKitColor();

        GameProfile profile = new GameProfile(corpsePlayer.getUniqueId(), "");

        EntityPlayer corpse = new EntityPlayer(ms, ws, profile, new PlayerInteractManager(ws));

        corpse.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());

        PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(corpse);

        PacketPlayOutPlayerInfo info = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, corpse);


        for (Player p : players)
        {
            sendPacket(p, info);
            sendPacket(p, spawn);


            corpse.setEquipment(EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(kit.getArmorContents()[0]));
            corpse.setEquipment(EnumItemSlot.LEGS, CraftItemStack.asNMSCopy(kit.getArmorContents()[1]));
            corpse.setEquipment(EnumItemSlot.FEET, CraftItemStack.asNMSCopy(kit.getArmorContents()[2]));

            Location bed = loc.add(1, 0, 0);

            new BukkitRunnable() {
                @Override
                public void run() {
                    DataWatcher watcher = corpse.getDataWatcher();
                    watcher.set(DataWatcherRegistry.a.a(18), (byte) 2);
                    sendPacket(p, new PacketPlayOutEntityMetadata(corpse.getId(), watcher, false));

                    //corpse.entitySleep(new BlockPosition(bed.getX(), bed.getY(), bed.getZ()));
                    //corpse.setPose(EntityPose.SLEEPING);
                    sendPacket(p, new PacketPlayOutEntityMetadata(corpse.getId(), corpse.getDataWatcher(), false));
                }
            }.runTaskTimer(AmongUs.get(), 0, 20);


            ScoreboardTeam team = new ScoreboardTeam(((CraftScoreboard) Bukkit.getScoreboardManager().getMainScoreboard()).getHandle(), p.getName());

            team.setNameTagVisibility(ScoreboardTeamBase.EnumNameTagVisibility.NEVER);

            sendPacket(p, new PacketPlayOutScoreboardTeam(team, 1));
            sendPacket(p, new PacketPlayOutScoreboardTeam(team, 0));
            sendPacket(p, new PacketPlayOutScoreboardTeam(team, Arrays.asList(corpse.getName()), 3));

            List<Pair<EnumItemSlot, ItemStack>> equipmentList = new ArrayList<>();
            equipmentList.add(new Pair<>(EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(kit.getArmorContents()[0])));
            equipmentList.add(new Pair<>(EnumItemSlot.LEGS, CraftItemStack.asNMSCopy(kit.getArmorContents()[1])));
            equipmentList.add(new Pair<>(EnumItemSlot.FEET, CraftItemStack.asNMSCopy(kit.getArmorContents()[2])));

            PacketPlayOutEntityEquipment equipment = new PacketPlayOutEntityEquipment(corpse.getId(), equipmentList);
            sendPacket(p, equipment);



            new BukkitRunnable() {
                @Override
                public void run() {
                    sendPacket(p, new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, corpse));
                }
            }.runTaskLater(AmongUs.get(), 40);
        }

    }


    private static void sendPacket(Player player, Packet<?> packet)
    {
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
    }

}
