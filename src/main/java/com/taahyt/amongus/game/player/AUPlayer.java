package com.taahyt.amongus.game.player;

import com.taahyt.amongus.tasks.Task;
import com.taahyt.amongus.tasks.TaskStep;
import com.taahyt.amongus.utils.AUScoreboard;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
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

    private List<Task> tasksCompleted;
    private List<Task> tasks;

    public AUPlayer(UUID uuid)
    {
        this.uuid = uuid;
        this.imposter = false;
        this.kills = 0;
        this.tasksCompleted = new ArrayList<>();
        this.tasks = new ArrayList<>();
        this.voted = false;
        this.scoreboard = new AUScoreboard("AMONG US");
    }

    public Player getBukkitPlayer()
    {
        return Bukkit.getPlayer(uuid);
    }

    public Task getTask(Task taskk)
    {
        for (Task taskz : getTasks())
        {
            if (taskz == taskk)
            {
                return taskz;
            }
        }
        return null;
    }

    public TaskStep getTaskStep(TaskStep step)
    {
        for (Task taskz : getTasks())
        {
            for (TaskStep steps : taskz.getSteps())
            {
                if (steps == step)
                {
                    return steps;
                }
            }
        }
        return null;
    }

}
