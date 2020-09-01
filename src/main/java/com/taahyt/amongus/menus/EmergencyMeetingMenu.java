package com.taahyt.amongus.menus;

import com.taahyt.amongus.AmongUs;
import com.taahyt.amongus.game.AUGame;
import com.taahyt.amongus.game.player.AUPlayer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;

public class EmergencyMeetingMenu implements Listener
{

    @Getter
    private Inventory inventory;

    private AUGame game;

    @Setter @Getter
    private int countingTime = 15;

    @Getter
    private ItemStack counterItem;

    @Getter @Setter
    private int votingTime = 20;

    public EmergencyMeetingMenu()
    {
        this.inventory = Bukkit.createInventory(null, 27, ChatColor.RED + "EMERGENCY MEETING");
        this.game = AmongUs.get().getGame();
    }

    public void openInventory(Player player)
    {
        int i = 10;
        for (AUPlayer gamePlayers : game.getAlivePlayers())
        {

            ItemStack item = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            assert meta != null;
            meta.setOwningPlayer(gamePlayers.getBukkitPlayer());
            meta.setDisplayName(ChatColor.GOLD + gamePlayers.getBukkitPlayer().getName());
            meta.setLore(Arrays.asList(ChatColor.YELLOW + "Votes: 0"));
            item.setItemMeta(meta);
            getInventory().setItem(i, item);
            i++;

        }

        counterItem = new ItemStack(Material.SUNFLOWER);
        ItemMeta meta = counterItem.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.RESET + "Waiting Left: " + countingTime);
        counterItem.setItemMeta(meta);
        getInventory().setItem(26, counterItem);

        for (int j = 0; j <= getInventory().getSize() - 1; j++)
        {
            if (getInventory().getItem(j) == null || getInventory().getItem(j).getType() == Material.AIR)
            {
                getInventory().setItem(j, new ItemStack(Material.PURPLE_STAINED_GLASS_PANE));
            }
        }

        player.openInventory(getInventory());
    }

    @EventHandler
    public void onClick(InventoryClickEvent event)
    {
        if (event.getClickedInventory() == null) return;
        if (event.getClickedInventory().hashCode() != this.inventory.hashCode()) return;
        if (event.getCurrentItem() == null) return;
        event.setCancelled(true);

        if (!AmongUs.get().getGame().isAlive(event.getWhoClicked().getUniqueId())) return;

        if (event.getCurrentItem().getType() != Material.PLAYER_HEAD) return;

        if (AmongUs.get().getGame().isWaitingOnVote()) return;
        if (!AmongUs.get().getGame().isVoting()) return;

        SkullMeta meta = (SkullMeta) event.getCurrentItem().getItemMeta();

        assert meta != null;

        AUPlayer player = AmongUs.get().getGame().getAlivePlayer(meta.getOwningPlayer().getUniqueId());


        AUPlayer clicker = AmongUs.get().getGame().getAlivePlayer(event.getWhoClicked().getUniqueId());

        if (clicker.isVoted()) return; // CHECK IF THE PERSON WHO CLICKED VOTED

        AmongUs.get().getGame().vote(player); //ADD A VOTE TO THE PERSON WHO WAS CLICKED
        clicker.setVoted(true); // SET THE CLICKER TO VOTED

        ItemStack item = counterItem;
        ItemMeta counterMeta = item.getItemMeta();
        assert counterMeta != null;
        counterMeta.setDisplayName("VOTED!");
        item.setItemMeta(counterMeta);
        counterItem = item;

        getInventory().setItem(26, item);

    }
}
