package com.taahyt.amongus.game.player;

import com.taahyt.amongus.utils.AUScoreboard;
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

    @Getter @Setter
    private boolean voted;

    @Getter
    private AUScoreboard scoreboard;

    public AUPlayer(UUID uuid)
    {
        this.uuid = uuid;
        this.imposter = false;
        this.kills = 0;
        this.voted = false;
        this.scoreboard = new AUScoreboard("AMONG US");
    }

    public Player getBukkitPlayer()
    {
        return Bukkit.getPlayer(uuid);
    }

}
