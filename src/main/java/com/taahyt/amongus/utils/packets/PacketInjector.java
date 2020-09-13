package com.taahyt.amongus.utils.packets;

import com.taahyt.amongus.AmongUs;
import com.taahyt.amongus.utils.NMSUtils;
import io.netty.channel.*;
import net.minecraft.server.v1_16_R2.*;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class PacketInjector extends ChannelDuplexHandler
{

    private List<Packet> cooldownPackets = new ArrayList<>();

    @Override
    public void write(ChannelHandlerContext ctx, Object packet, ChannelPromise promise) throws Exception {
        if (packet instanceof PacketPlayInArmAnimation)
        {
            PacketPlayInArmAnimation animPacket = (PacketPlayInArmAnimation) packet;
            if (animPacket.b() == EnumHand.MAIN_HAND)
            {
                if (cooldownPackets.contains(animPacket))
                {
                    return;
                }

            }
        }
        super.write(ctx, packet, promise);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception
    {
        if (packet instanceof PacketPlayInArmAnimation)
        {
            PacketPlayInArmAnimation animPacket = (PacketPlayInArmAnimation) packet;
            if (animPacket.b() == EnumHand.MAIN_HAND)
            {
                if (cooldownPackets.contains(animPacket))
                {
                    return;
                }
                cooldownPackets.add(animPacket);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        cooldownPackets.remove(animPacket);
                    }
                }.runTaskLater(AmongUs.get(), 20);
            }
        }

        super.channelRead(ctx, packet);
    }

    public void inject(Player player)
    {
        try {
            ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.pipeline();
            pipeline.addBefore("packet_handler", player.getName(), this);
        } catch (Exception e)
        {

        }
    }

    public void eject(Player player) {
        Channel channel = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;

        channel.eventLoop().submit(() -> {
            channel.pipeline().remove(player.getName());
            return null;
        });
    }
}
