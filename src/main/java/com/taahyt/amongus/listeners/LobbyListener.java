package com.taahyt.amongus.listeners;

import com.taahyt.amongus.AmongUs;
import com.taahyt.amongus.game.player.AUPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class LobbyListener implements Listener
{

    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        AmongUs.get().getGame().getPlayers().add(new AUPlayer(event.getPlayer().getUniqueId()));
    }

}
