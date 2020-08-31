package com.taahyt.amongus.game.player;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
public class AUPlayer
{

    private UUID uuid;

    @Setter
    private boolean imposter;

    @Setter
    private int kills;

    public AUPlayer(UUID uuid)
    {
        this.uuid = uuid;
        this.imposter = false;
        this.kills = 0;
    }

    public Player getBukkitPlayer()
    {
        return Bukkit.getPlayer(uuid);
    }

}
