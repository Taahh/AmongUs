package com.taahyt.amongus.listeners;

import com.taahyt.amongus.AmongUs;
import com.taahyt.amongus.game.player.AUPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

public class GameListener implements Listener
{

    @EventHandler
    public void onButtonClick(PlayerInteractEvent event)
    {
        if (event.getClickedBlock() == null) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock().getType() != Material.STONE_BUTTON) return;
        if (!event.getClickedBlock().hasMetadata("emergency_meeting")) return;

        Player player = event.getPlayer();
        if (AmongUs.get().getGame().isEmergencyCooldown())
        {
            player.sendMessage("The emergency meeting button is currently on cooldown!");
            return;
        }
        AmongUs.get().getEmergencyMeetingConfirmMenu().openInventory(player);
    }
    @EventHandler
    public void onClose(InventoryCloseEvent event)
    {
        if (!(event.getPlayer() instanceof Player)) return;
        Inventory inv = event.getPlayer().getOpenInventory().getTopInventory();
        if (inv.hashCode() != AmongUs.get().getEmergencyMeetingConfirmMenu().getInventory().hashCode() &&
            inv.hashCode() != AmongUs.get().getEmergencyMeetingMenu().getInventory().hashCode()) return;
        if (!AmongUs.get().getGame().isVoting() && !AmongUs.get().getGame().isWaitingOnVote()) return;
        if (AmongUs.get().getGame().isWaitingOnVote() || AmongUs.get().getGame().isVoting())
        {
            new BukkitRunnable() {
                @Override
                public void run() {
                    AmongUs.get().getEmergencyMeetingMenu().openInventory((Player) event.getPlayer());
                }
            }.runTaskLater(AmongUs.get(), 2);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        AUPlayer gamePlayer = AmongUs.get().getGame().getPlayer(player.getUniqueId());
        AmongUs.get().getGame().getAlivePlayers().remove(gamePlayer);
        AmongUs.get().getGame().getPlayers().remove(gamePlayer);
    }

    @EventHandler
    public void onArmorRemove(InventoryClickEvent event)
    {
        if (!(event.getClickedInventory() instanceof PlayerInventory)) return;

        if (event.getSlotType() == InventoryType.SlotType.ARMOR) event.setCancelled(true);

    }

}
