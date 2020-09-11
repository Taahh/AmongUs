package com.taahyt.amongus.game.player;

import com.taahyt.amongus.tasksystem.Task;
import com.taahyt.amongus.tasksystem.TaskStep;
import com.taahyt.amongus.tasksystem.manager.TaskManager;
import com.taahyt.amongus.utils.AUScoreboard;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
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

    public AUPlayer(UUID uuid)
    {
        this.uuid = uuid;
        this.imposter = false;
        this.kills = 0;

        this.taskManager = new TaskManager(this);

        this.voted = false;
        this.scoreboard = new AUScoreboard("AMONG US");
    }

    public Player getBukkitPlayer()
    {
        return Bukkit.getPlayer(uuid);
    }


}
