package com.taahyt.amongus.command;

import com.taahyt.amongus.AmongUs;
import io.netty.util.internal.ThreadLocalRandom;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;

public class JoinCMD implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            return true;
        }

        Player player = (Player) sender;
        Location b = AmongUs.get().getGame().getScanner().getLobbySeats().get(ThreadLocalRandom.current().nextInt(AmongUs.get().getGame().getScanner().getLobbySeats().size()));
        Arrow arrow = player.getWorld().spawn(new Location(player.getWorld(), b.getX() + 0.5, b.getY() - 0.2, b.getZ() - 0.15), Arrow.class);
        arrow.addPassenger(player);

        return true;
    }
}
