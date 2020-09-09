package com.taahyt.amongus.map;

import com.google.common.collect.Lists;
import com.taahyt.amongus.AmongUs;
import com.taahyt.amongus.game.AUGame;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Attachable;
import org.bukkit.block.data.type.Switch;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;

@Getter
public class MapScanner
{

    private List<Location> lobbySeats = Lists.newArrayList();
    private Location emergencyMeeting;
    private Location adminCardSlider;


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

    public void parseMap(AUGame game)
    {
        for (int x = Math.min(game.getGameA().getBlockX(), game.getGameB().getBlockX()); x < Math.max(game.getGameA().getBlockX() , game.getGameB().getBlockX()); x++)
        {
            for (int y = Math.min(game.getGameA().getBlockY(), game.getGameB().getBlockY()); y < Math.max(game.getGameA().getBlockY() , game.getGameB().getBlockY()); y++)
            {
                for (int z = Math.min(game.getGameA().getBlockZ(), game.getGameB().getBlockZ()); z < Math.max(game.getGameA().getBlockZ() , game.getGameB().getBlockZ()); z++)
                {
                    Block block = game.getLobbyA().getWorld().getBlockAt(x, y, z);
                    if (block.getState() instanceof Sign)
                    {
                        Sign sign = (Sign) block.getState();
                        if (sign.getLine(0).equalsIgnoreCase("GAME_DATA"))
                        {
                            if (sign.getLine(1).equalsIgnoreCase("MEETING"))
                            {
                                for (BlockFace face : BlockFace.values())
                                {
                                    if (sign.getLocation().getBlock().getRelative(face).getLocation().add(0, 1, 0).getBlock().getType() != Material.STONE_BUTTON)
                                    {
                                        Block supposedbutton = sign.getLocation().getBlock().getRelative(face).getLocation().add(0, 1, 1).getBlock();
                                        supposedbutton.setMetadata("emergency_meeting", new FixedMetadataValue(AmongUs.get(), "emergency_meeting"));
                                        emergencyMeeting = supposedbutton.getLocation();
                                    }
                                }
                            }
                            else if (sign.getLine(1).equalsIgnoreCase("ADMIN_CARD"))
                            {
                                adminCardSlider = sign.getLocation();
                            }
                        }
                    }
                }
            }
        }
    }
}
