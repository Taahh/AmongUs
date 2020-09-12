package com.taahyt.amongus.utils;

import com.taahyt.amongus.AmongUs;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class Animations
{

    public static CompletableFuture<String> animate(Map<ItemStack, Integer> items, ItemStack item, int endingSlot, Inventory inventory, BukkitTask task, boolean increasing)
    {
        CompletableFuture<String> future = new CompletableFuture<>();

        AtomicReference<BukkitTask> atomicTask = new AtomicReference<>();
        atomicTask.set(task);
        atomicTask.set(new BukkitRunnable() {
            int i =  items.get(item);
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
                    atomicTask.set(null);
                    future.complete("Completed " + this.getTaskId());
                    this.cancel();
                }

            }
        }.runTaskTimer(AmongUs.get(), 0, 10));
        return future;
    }

    public static ItemStack getFromHashcode(Map<ItemStack, Integer> map, int hash)
    {
        for (ItemStack item : map.keySet())
        {
            if (item.hashCode() == hash)
            {
                return item;
            }
        }
        return null;
    }

    public static ItemStack getFromDisplayName(Map<ItemStack, Integer> map, String name)
    {
        for (ItemStack item : map.keySet())
        {
            if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase(name))
            {
                return item;
            }
        }
        return null;
    }
}
