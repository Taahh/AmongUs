package com.taahyt.amongus.listeners;

import com.taahyt.amongus.AmongUs;
import com.taahyt.amongus.customization.Kit;
import com.taahyt.amongus.game.player.AUPlayer;
import com.taahyt.amongus.game.states.LobbyState;
import io.netty.util.internal.ThreadLocalRandom;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.PlayerInventory;

public class LobbyListener implements Listener
{

    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(player.getHealthScale());
        player.setFoodLevel(20);
        player.getInventory().clear(); //for debug purposes



        AmongUs.get().getGame().getPlayers().add(new AUPlayer(player.getUniqueId()));
        Location b = AmongUs.get().getGame().getScanner().getLobbySeats().get(ThreadLocalRandom.current().nextInt(AmongUs.get().getGame().getScanner().getLobbySeats().size()));
        Arrow arrow = player.getWorld().spawn(new Location(player.getWorld(), b.getX() + 0.5, b.getY() - 0.1, b.getZ() - 0.15), Arrow.class);
        arrow.addPassenger(player);

        AUPlayer gamePlayer = AmongUs.get().getGame().getPlayer(player.getUniqueId());


        gamePlayer.getScoreboard().attach(player);
        gamePlayer.getScoreboard().set(0, "ROLE: ");
        gamePlayer.getScoreboard().set(1, "");
        gamePlayer.getScoreboard().set(2, "Tasks Completed: <>");
        gamePlayer.getScoreboard().set(3, "Game Starts In: ");

        Kit kit = AmongUs.get().getKitManager().getRandomKit();
        gamePlayer.setKitColor(kit);

        player.getInventory().setChestplate(kit.getArmorContents()[0]);
        player.getInventory().setLeggings(kit.getArmorContents()[1]);
        player.getInventory().setBoots(kit.getArmorContents()[2]);

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        AUPlayer gamePlayer = AmongUs.get().getGame().getPlayer(player.getUniqueId());
        AmongUs.get().getGame().getPlayers().remove(gamePlayer);
        AmongUs.get().getKitManager().getKits().add(gamePlayer.getKitColor()); //add back the kit to the pool

        if (AmongUs.get().getGame().getCurrentState() instanceof LobbyState)
        {
            LobbyState state = (LobbyState) AmongUs.get().getGame().getCurrentState();
            if (state.getSecondsLeft() < 10 && AmongUs.get().getGame().getPlayers().size() < AmongUs.get().getGame().getMinPlayers())
            {
                state.setSecondsRemaining(10);
            }
        }

        player.setScoreboard(Bukkit.getServer().getScoreboardManager().getNewScoreboard());
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
        event.setCancelled(true);
    }

}
