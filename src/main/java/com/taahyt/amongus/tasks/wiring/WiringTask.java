package com.taahyt.amongus.tasks.wiring;

import com.taahyt.amongus.tasks.Task;
import com.taahyt.amongus.tasks.TaskStep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WiringTask extends Task
{

    private List<TaskStep> completedTasks = new ArrayList<>();
    public WiringTask()
    {
        super();
    }

    @Override
    public List<TaskStep> getSteps() {
        return Arrays.asList(new WiringTask1());
    }

    @Override
    public List<TaskStep> getCompletedSteps() {
        return completedTasks;
    }


    @Override
    public String getID() {
        return "wiring_task";
    }

    @Override
    public void completeStep(TaskStep step) {
        getCompletedSteps().add(step);
    }
}
