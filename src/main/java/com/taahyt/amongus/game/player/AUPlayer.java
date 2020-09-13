package com.taahyt.amongus.game.player;

import com.taahyt.amongus.customization.Kit;
import com.taahyt.amongus.utils.scoreboard.AUScoreboard;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
@Setter
public class AUPlayer
{

    @Setter(AccessLevel.NONE)
    private UUID uuid;

    private boolean imposter;

    private int kills;

    private boolean voted;

    private AUScoreboard scoreboard;

    private Kit kitColor;

    private boolean scanning;
    private boolean scanned;

    public AUPlayer(UUID uuid)
    {
        this.uuid = uuid;
        this.imposter = false;
        this.kills = 0;

        this.kitColor = null;

        this.scanning = false;
        this.scanned = false;

        this.voted = false;
        this.scoreboard = new AUScoreboard("AMONG US");
    }

    public Player getBukkitPlayer()
    {
        return Bukkit.getPlayer(uuid);
    }


}
