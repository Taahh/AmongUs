package com.taahyt.amongus.tasks;

import com.taahyt.amongus.game.AUGame;
import org.bukkit.event.Listener;

public abstract class TaskStep implements Listener
{

    public abstract Task getParent();

    public abstract AUGame getGame();


}
