package com.taahyt.amongus.menus;

import com.google.common.collect.Maps;
import com.taahyt.amongus.AmongUs;
import com.taahyt.amongus.game.AUGame;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.Map;

public class AnimatedMenuTest implements Listener {


    private Map<ItemStack, Integer> animatedItem = Maps.newHashMap();
    private BukkitTask task;

    @Getter
    private Inventory inventory;

    private AUGame game;

    public AnimatedMenuTest() {
        this.inventory = Bukkit.createInventory(null, 27, ChatColor.RED + "Animated Test");
        this.game = AmongUs.get().getGame();
    }

    public void openInventory(Player player) {
        ItemStack confirm = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta confirmMeta = confirm.getItemMeta();
        assert confirmMeta != null;
        confirmMeta.setDisplayName(ChatColor.GREEN + "Confirm!");
        confirmMeta.setLore(Arrays.asList(ChatColor.DARK_GREEN + "Clicking this will initiate", ChatColor.DARK_GREEN + "an emergency meeting!"));
        confirm.setItemMeta(confirmMeta);
        animatedItem.put(confirm, 9);
        getInventory().setItem(animatedItem.get(confirm), confirm);

        player.openInventory(getInventory());

        task = new BukkitRunnable() {
            int i = 9;
            @Override
            public void run() {
                i++;
                getInventory().setItem(animatedItem.get(confirm), new ItemStack(Material.AIR));
                animatedItem.put(confirm, i);
                getInventory().setItem(animatedItem.get(confirm), confirm);
                if (i == 16)
                {
                    this.cancel();
                }
            }
        }.runTaskTimer(AmongUs.get(), 0, 1);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event)
    {
        if (event.getInventory().hashCode() == getInventory().hashCode())
        {
            for (Integer value : animatedItem.values()) {
                getInventory().setItem(value, new ItemStack(Material.AIR));
            }

            animatedItem.clear();

            getInventory().clear();
            task.cancel();
        }
    }
}
