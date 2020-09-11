package com.taahyt.amongus.utils;

import com.google.common.collect.Maps;
import com.taahyt.amongus.AmongUs;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;

public class Animations
{

    public static void animate(Map<ItemStack, Integer> items, ItemStack item, int endingSlot, Inventory inventory, BukkitTask task, boolean increasing)
    {
        BukkitTask finalTask = task;
        task = new BukkitRunnable() {
            int i =  items.get(item);
            BukkitTask temporaryTask = finalTask;
            @Override
            public void run()
            {
                if (items.isEmpty())
                {
                    this.cancel();
                    return;
                }

                if (increasing)
                {
                    i++;
                } else {
                    i--;
                }

                inventory.setItem(items.get(item), new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
                items.put(item, i);
                inventory.setItem(items.get(item), item);
                if (i == endingSlot)
                {
                    temporaryTask = null;
                    this.cancel();
                }

            }
        }.runTaskTimer(AmongUs.get(), 0, 10);
    }

}
