package com.taahyt.amongus.menus.fixing;

import com.taahyt.amongus.AmongUs;
import com.taahyt.amongus.game.AUGame;
import com.taahyt.amongus.game.player.AUPlayer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class AdminCardSliderMenu implements Listener
{
    @Getter
    private Inventory inventory;

    private AUGame game;

    public AdminCardSliderMenu()
    {
        this.inventory = Bukkit.createInventory(null, 54, ChatColor.RED + "Admin Card");
        this.game = AmongUs.get().getGame();
    }

    public void openInventory(Player player)
    {

        getInventory().setItem(22, new ItemStack(Material.DISPENSER));
        getInventory().setItem(10, new ItemStack(Material.DIAMOND));

        for (int i = 0; i<= getInventory().getSize() - 1; i++)
        {
            if (getInventory().getItem(i) == null || getInventory().getItem(i).getType() == Material.AIR)
            {
                getInventory().setItem(i, new ItemStack(Material.YELLOW_STAINED_GLASS_PANE));
            }
        }

        player.openInventory(getInventory());
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (event.getWhoClicked().getOpenInventory().getTopInventory().hashCode() != this.inventory.hashCode()) return;
        if (event.getClickedInventory().hashCode() != this.inventory.hashCode())
        {
            event.setCancelled(true);
            return;
        }
        if (event.getCurrentItem() == null) return;

        if (event.getCurrentItem().getType() == Material.YELLOW_STAINED_GLASS_PANE)
        {
            event.setCancelled(true);
            return;
        }

        if (event.getCurrentItem().getType() != Material.DISPENSER) return;

        if (event.getCursor().getType() != Material.DIAMOND) return;

        if (event.getCurrentItem().getType() == Material.DISPENSER)
        {
            event.setCancelled(true);
        }

        AUPlayer player = game.getPlayer(event.getWhoClicked().getUniqueId());
        player.getTasksCompleted().add("admin_card");
        player.getScoreboard().set(2, "Tasks Completed: " + player.getTasksCompleted().size() + "/5");
        event.getWhoClicked().setItemOnCursor(new ItemStack(Material.AIR));
        player.getBukkitPlayer().closeInventory();

    }

}
