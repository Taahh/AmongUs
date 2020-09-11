package com.taahyt.amongus.tasksystem;

import com.taahyt.amongus.game.AUGame;
import com.taahyt.amongus.game.player.AUPlayer;
import org.bukkit.event.Listener;

public abstract class TaskStep<T extends Task> implements Listener
{

    private String description;
    public TaskStep(String description)
    {
        this.description = description;
    }

    public abstract T getParent(AUPlayer player);

    public abstract AUGame getGame();

    public String getDescription() {
        return description;
    }

}
