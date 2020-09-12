package com.taahyt.amongus.tasksystem.fuel;

import com.taahyt.amongus.AmongUs;
import com.taahyt.amongus.tasksystem.Task;
import com.taahyt.amongus.tasksystem.TaskStep;

import java.util.ArrayList;
import java.util.List;

public class FuelTask extends Task<FuelTask>
{

    private List<TaskStep> completedStep = new ArrayList<>(), steps = new ArrayList<>();

    private GasolineTankTaskStep gasolineTankTaskStep;
    public FuelTask()
    {
        gasolineTankTaskStep = new GasolineTankTaskStep();
        steps.add(gasolineTankTaskStep);
        getSteps().forEach(step -> {
             AmongUs.get().getServer().getPluginManager().registerEvents(step, AmongUs.get());
        });
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
        return "fuel_task";
    }


    @Override
    public void completeStep(TaskStep step) {
        getCompletedSteps().add(step);
    }

    @Override
    public FuelTask cloneTask() {
        try {
            return (FuelTask) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

}
