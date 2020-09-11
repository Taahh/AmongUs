package com.taahyt.amongus.listeners;

import com.taahyt.amongus.AmongUs;
import com.taahyt.amongus.game.player.AUPlayer;
import com.taahyt.amongus.game.states.LobbyState;
import io.netty.util.internal.ThreadLocalRandom;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LobbyListener implements Listener
{

    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(player.getHealthScale());
        player.setFoodLevel(20);

        AmongUs.get().getGame().getPlayers().add(new AUPlayer(player.getUniqueId()));
        Location b = AmongUs.get().getGame().getScanner().getLobbySeats().get(ThreadLocalRandom.current().nextInt(AmongUs.get().getGame().getScanner().getLobbySeats().size()));
        Arrow arrow = player.getWorld().spawn(new Location(player.getWorld(), b.getX() + 0.5, b.getY() - 0.2, b.getZ() - 0.15), Arrow.class);
        arrow.addPassenger(player);


        AmongUs.get().getGame().getPlayer(player.getUniqueId()).getScoreboard().attach(player);
        AmongUs.get().getGame().getPlayer(player.getUniqueId()).getScoreboard().set(0, "ROLE: ");
        AmongUs.get().getGame().getPlayer(player.getUniqueId()).getScoreboard().set(1, "");
        AmongUs.get().getGame().getPlayer(player.getUniqueId()).getScoreboard().set(2, "Tasks Completed: <>");
        AmongUs.get().getGame().getPlayer(player.getUniqueId()).getScoreboard().set(3, "Game Starts In: ");


    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        AUPlayer gamePlayer = AmongUs.get().getGame().getPlayer(player.getUniqueId());
        AmongUs.get().getGame().getPlayers().remove(gamePlayer);

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

}
