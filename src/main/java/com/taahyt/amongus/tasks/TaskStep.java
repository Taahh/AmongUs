package com.taahyt.amongus.tasks;

import com.taahyt.amongus.game.AUGame;
import com.taahyt.amongus.game.player.AUPlayer;
import org.bukkit.event.Listener;

import java.util.List;

public abstract class TaskStep implements Listener
{

    private String description;
    public TaskStep(String description)
    {
        this.description = description;
    }


    /* Gets all the Players that have this Step Active */
    public abstract List<AUPlayer> activePlayers();

    public abstract List<AUPlayer> completedPlayers();

    public abstract Task getParent();

    public abstract AUGame getGame();

    public String getDescription() {
        return description;
    }

}