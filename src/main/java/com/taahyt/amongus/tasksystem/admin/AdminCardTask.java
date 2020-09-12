package com.taahyt.amongus.tasksystem.admin;

import com.taahyt.amongus.AmongUs;
import com.taahyt.amongus.tasksystem.Task;
import com.taahyt.amongus.tasksystem.TaskStep;

import java.util.ArrayList;
import java.util.List;

public class AdminCardTask extends Task<AdminCardTask>
{
    private List<TaskStep> completedStep = new ArrayList<>(), steps = new ArrayList<>();

    private AdminCardSliderTaskStep adminCardSliderTaskStep;

    public AdminCardTask()
    {
        adminCardSliderTaskStep = new AdminCardSliderTaskStep();
        steps.add(adminCardSliderTaskStep);
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
        return "admin_card_task";
    }

    @Override
    public void completeStep(TaskStep step) {
        getCompletedSteps().add(step);
    }

    @Override
    public AdminCardTask cloneTask() {
        try {
            return (AdminCardTask) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }


}
