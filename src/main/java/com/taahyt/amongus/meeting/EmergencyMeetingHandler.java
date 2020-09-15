package com.taahyt.amongus.meeting;

import com.google.common.collect.Maps;
import com.taahyt.amongus.AmongUs;
import com.taahyt.amongus.game.AUGame;
import com.taahyt.amongus.game.player.AUPlayer;
import com.taahyt.amongus.utils.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EmergencyMeetingHandler implements Listener
{

    private Map<Player, Inventory> inventories = Maps.newHashMap();


    public Inventory getInventory(Player player)
    {
        return inventories.get(player);
    }

    public void openInventory(Player player)
    {
        AUGame game = AmongUs.get().getGame();

        if (inventories.containsKey(player))
        {
            player.openInventory(inventories.get(player));
            return;
        }

        inventories.put(player, Bukkit.createInventory(null, 36, "Emergency Meeting"));
        Inventory inventory = inventories.get(player);
        for (int i = 0; i <= inventory.getSize() - 1; i++)
        {
            if (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR)
            {
                inventory.setItem(i, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName("§r").build());
            }
        }

        int j = 10;

        for (AUPlayer gamePlayer : game.getAlivePlayers())
        {
            if (j == 17)
            {
                j = 21;
            }

            ItemStack item = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            assert meta != null;
            meta.setOwningPlayer(gamePlayer.getBukkitPlayer());
            meta.setDisplayName(ChatColor.GOLD + gamePlayer.getBukkitPlayer().getName());
            meta.setLore(Arrays.asList(ChatColor.YELLOW + "Color: " + gamePlayer.getKitColor().getName()));
            item.setItemMeta(meta);
            inventory.setItem(j, item);
            j++;

        }

        inventory.setItem(35, new ItemBuilder(Material.SUNFLOWER).setDisplayName("§r<> Left: <>").build());
        inventory.setItem(34, new ItemBuilder(Material.REDSTONE_TORCH).setDisplayName("§rNot Voted").build());

        player.openInventory(inventory);


    }

    @EventHandler
    public void onClick(InventoryClickEvent event)
    {
        Player player = (Player) event.getWhoClicked();
        if (!inventories.containsKey(player)) return;
        if (event.getClickedInventory().hashCode() != inventories.get(player).hashCode()) return;
        if (event.getCurrentItem() == null) return;
        event.setCancelled(true);

        if (!AmongUs.get().getGame().isAlive(event.getWhoClicked().getUniqueId())) return;

        if (event.getCurrentItem().getType() != Material.PLAYER_HEAD) return;

        if (AmongUs.get().getGame().isWaitingOnVote()) return;
        if (!AmongUs.get().getGame().isVoting()) return;

        SkullMeta meta = (SkullMeta) event.getCurrentItem().getItemMeta();

        assert meta != null;

        AUPlayer auPlayer = AmongUs.get().getGame().getAlivePlayer(meta.getOwningPlayer().getUniqueId());


        AUPlayer clicker = AmongUs.get().getGame().getAlivePlayer(event.getWhoClicked().getUniqueId());

        if (clicker.isVoted()) return; // CHECK IF THE PERSON WHO CLICKED VOTED

        AmongUs.get().getGame().vote(auPlayer); //ADD A VOTE TO THE PERSON WHO WAS CLICKED
        clicker.setVoted(true); // SET THE CLICKER TO VOTED

    }

}
