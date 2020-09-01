package com.taahyt.amongus.utils;

import org.bukkit.Location;

public class LocationUtil
{

    public static boolean matches(Location loc1, Location loc2)
    {
        if (Math.round(loc1.getBlockX()) == Math.round(loc2.getBlockX()))
        {
            if (Math.round(loc1.getBlockY()) == Math.round(loc2.getBlockY()))
            {
                if (Math.round(loc1.getBlockZ()) == Math.round(loc2.getBlockZ()))
                {
                    return true;
                }
            }
        }
        return false;
    }

}
