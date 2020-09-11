package com.taahyt.amongus.tasksystem.wiring;

import com.taahyt.amongus.AmongUs;
import com.taahyt.amongus.tasksystem.Task;
import com.taahyt.amongus.tasksystem.TaskStep;

import java.util.ArrayList;
import java.util.List;

public class WiringTask extends Task
{

    private List<TaskStep> completedStep = new ArrayList<>(), steps = new ArrayList<>();

    private ElectricalWiringTaskStep electricalWiringTaskStep;
    private CafeteriaWiringTaskStep cafeteriaWiringTaskStep;

    public WiringTask()
    {
        electricalWiringTaskStep = new ElectricalWiringTaskStep();
        cafeteriaWiringTaskStep = new CafeteriaWiringTaskStep();
        steps.add(electricalWiringTaskStep);
        steps.add(cafeteriaWiringTaskStep);

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
        return "wiring_task";
    }

    @Override
    public void completeStep(TaskStep step)
    {
        getCompletedSteps().add(step);
    }

    @Override
    public WiringTask cloneTask() {
        try {
            return (WiringTask) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
