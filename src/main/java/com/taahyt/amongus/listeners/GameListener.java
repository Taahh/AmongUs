package com.taahyt.amongus.listeners;

import com.taahyt.amongus.AmongUs;
import com.taahyt.amongus.game.player.AUPlayer;
import com.taahyt.amongus.utils.NMSUtils;
import com.taahyt.amongus.utils.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameListener implements Listener
{

    private Map<UUID, Integer> killCooldown = new HashMap<>();

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

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
    {
        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getDamager() instanceof Player)) return;

        AUPlayer damager = AmongUs.get().getGame().getPlayer(event.getDamager().getUniqueId());
        AUPlayer entity = AmongUs.get().getGame().getPlayer(event.getEntity().getUniqueId());

        if (!damager.isImposter())
        {
            event.setCancelled(true);
            return;
        }

        if (!AmongUs.get().getGame().getAlivePlayers().contains(damager))
        {
            event.setCancelled(true);
            return;
        }

        if (damager.getBukkitPlayer().getGameMode() == GameMode.SPECTATOR) {
            event.setCancelled(true);
            return;
        }

        if (killCooldown.containsKey(damager.getUuid())) {
            event.setCancelled(true);
            return;

        }
        event.setCancelled(true);
        Location location = entity.getBukkitPlayer().getLocation();

        AmongUs.get().getGame().kill(entity);

        new BukkitRunnable() {
            @Override
            public void run() {
                AmongUs.get().getGame().getPlayers().stream().map(AUPlayer::getBukkitPlayer).forEach(player -> NMSUtils.secondSecondCorpse(entity.getBukkitPlayer(), location, player));
            }
        }.runTaskLater(AmongUs.get(), 9);

        killCooldown.put(damager.getUuid(), 15);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!killCooldown.containsKey(damager.getUuid()))
                {
                    this.cancel();
                    return;
                }

                killCooldown.put(damager.getUuid(), killCooldown.get(damager.getUuid()) - 1);
                damager.getBukkitPlayer().getInventory().setItem(0, new ItemBuilder(Material.DIAMOND_SWORD).setDisplayName("§4Murder Weapon (" + killCooldown.get(damager.getUuid()) + ")").build());

                if (killCooldown.get(damager.getUuid()) == 0)
                {
                    damager.getBukkitPlayer().getInventory().setItem(0, new ItemBuilder(Material.DIAMOND_SWORD).setDisplayName("§4Murder Weapon").build());
                    killCooldown.remove(damager.getUuid());
                    this.cancel();
                }

            }
        }.runTaskTimer(AmongUs.get(), 0, 20);

    }


    @EventHandler
    public void onAnimation(PlayerAnimationEvent event)
    {
        if (event.getAnimationType() == PlayerAnimationType.ARM_SWING)
        {
            Player player = event.getPlayer();
            AUPlayer gamePlayer = AmongUs.get().getGame().getPlayer(player.getUniqueId());
            if (gamePlayer.isImposter() && killCooldown.containsKey(gamePlayer.getUuid()))
            {
                event.setCancelled(true);
                return;
            }
            else if (!gamePlayer.isImposter())
            {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event)
    {
        event.setCancelled(true);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event)
    {
        event.setCancelled(true);
    }

}
