package com.taahyt.amongus.tasks.filter;

import com.taahyt.amongus.AmongUs;
import com.taahyt.amongus.game.player.AUPlayer;
import com.taahyt.amongus.tasks.Task;
import com.taahyt.amongus.tasks.TaskStep;

import java.util.ArrayList;
import java.util.List;

public class OxygenFilterTask implements Task
{

    private List<TaskStep> steps = new ArrayList<>();
    private List<AUPlayer> completedPlayers = new ArrayList<>();

    private OxygenFilterTaskStep oxygenFilterTaskStep;

    public OxygenFilterTask()
    {
        oxygenFilterTaskStep = new OxygenFilterTaskStep();
        steps.add(oxygenFilterTaskStep);

        getSteps().forEach(step -> {
             AmongUs.get().getServer().getPluginManager().registerEvents(step, AmongUs.get());
        });
    }

    @Override
    public List<AUPlayer> completedPlayers() {
        return completedPlayers;
    }

    @Override
    public List<TaskStep> getSteps() {
        return steps;
    }

    @Override
    public List<TaskStep> getCompletedSteps(AUPlayer player) {
        List<TaskStep> completedSteps = new ArrayList<>();
        steps.forEach(step -> {
            if (step.completedPlayers().contains(player))
            {
                completedSteps.add(step);
            }
        });
        return completedSteps;
    }

    @Override
    public String getID() {
        return "oxygen_filter_task";
    }
}
