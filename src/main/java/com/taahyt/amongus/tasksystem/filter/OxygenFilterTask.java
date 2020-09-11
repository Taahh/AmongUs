package com.taahyt.amongus.tasksystem.filter;

import com.taahyt.amongus.AmongUs;
import com.taahyt.amongus.tasksystem.Task;
import com.taahyt.amongus.tasksystem.TaskStep;

import java.util.ArrayList;
import java.util.List;

public class OxygenFilterTask extends Task<OxygenFilterTask>
{

    private List<TaskStep> completedStep = new ArrayList<>(), steps = new ArrayList<>();

    private OxygenFilterTaskStep oxygenFilterTaskStep;

    public OxygenFilterTask()
    {
        oxygenFilterTaskStep = new OxygenFilterTaskStep();
        steps.add(oxygenFilterTaskStep);

        for (TaskStep step : getSteps())
        {
            AmongUs.get().getServer().getPluginManager().registerEvents(step, AmongUs.get());
        }
    }

    @Override
    public List<TaskStep> getSteps() {
        return steps;
    }

    @Override
    public List<TaskStep> getCompletedSteps() {
        return completedStep;
    }

    @Override
    public String getID() {
        return "oxygen_filter_task";
    }


    @Override
    public void completeStep(TaskStep step) {
        getCompletedSteps().add(step);
    }

    @Override
    public OxygenFilterTask cloneTask() {
        try {
            return (OxygenFilterTask) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
