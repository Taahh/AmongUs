package com.taahyt.amongus.game.player;

import com.taahyt.amongus.customization.Kit;
import com.taahyt.amongus.tasksystem.manager.TaskManager;
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

    private TaskManager taskManager;

    private Kit kitColor;

    public AUPlayer(UUID uuid)
    {
        this.uuid = uuid;
        this.imposter = false;
        this.kills = 0;

        this.taskManager = new TaskManager(this);

        this.kitColor = null;

        this.voted = false;
        this.scoreboard = new AUScoreboard("AMONG US");
    }

    public Player getBukkitPlayer()
    {
        return Bukkit.getPlayer(uuid);
    }


}
