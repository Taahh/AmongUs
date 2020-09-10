package com.taahyt.amongus.tasks;

import com.taahyt.amongus.AmongUs;
import org.bukkit.event.Listener;

import java.util.List;

public abstract class Task implements Listener
{

    public Task()
    {
        AmongUs.get().getServer().getPluginManager().registerEvents(this, AmongUs.get());
        for (TaskStep step : getSteps())
        {
            AmongUs.get().getServer().getPluginManager().registerEvents(step, AmongUs.get());
        }
    }

    public abstract List<TaskStep> getSteps();

    public abstract List<TaskStep> getCompletedSteps();

    public abstract String getID();

    public abstract void completeStep(TaskStep step);

}
