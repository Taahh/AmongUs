package com.taahyt.amongus.map;

import com.google.common.collect.Lists;
import com.taahyt.amongus.game.AUGame;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.util.List;

public class MapScanner
{

    private List<Location> lobbySeats = Lists.newArrayList();


    public void parseLobby(AUGame game)
    {
        for (int x = Math.min(game.getLobbyA().getBlockX(), game.getLobbyB().getBlockX()); x < Math.max(game.getLobbyA().getBlockX() , game.getLobbyB().getBlockX()); x++)
        {
            for (int y = Math.min(game.getLobbyA().getBlockY(), game.getLobbyB().getBlockY()); y < Math.max(game.getLobbyA().getBlockY() , game.getLobbyB().getBlockY()); y++)
            {
                for (int z = Math.min(game.getLobbyA().getBlockZ(), game.getLobbyB().getBlockZ()); z < Math.max(game.getLobbyA().getBlockZ() , game.getLobbyB().getBlockZ()); z++)
                {
                    Block block = game.getLobbyA().getWorld().getBlockAt(x, y, z);
                    if (block.getState() instanceof Sign)
                    {
                        Sign sign = (Sign) block.getState();
                        if (sign.getLine(0).equalsIgnoreCase("LOBBY_DATA"))
                        {
                            if (sign.getLine(1).equalsIgnoreCase("SEAT"))
                            {
                                lobbySeats.add(sign.getLocation());
                            }
                        }
                    }
                }
            }
        }
    }

    public List<Location> getLobbySeats() {
        return lobbySeats;
    }
}
