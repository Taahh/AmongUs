package com.taahyt.amongus.listeners;

import com.taahyt.amongus.AmongUs;
import com.taahyt.amongus.game.player.AUPlayer;
import io.netty.util.internal.ThreadLocalRandom;
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

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        AUPlayer gamePlayer = AmongUs.get().getGame().getPlayer(player.getUniqueId());
        AmongUs.get().getGame().getPlayers().remove(gamePlayer);
    }

}
