package com.taahyt.amongus.tasksystem.manager;

import com.taahyt.amongus.game.player.AUPlayer;
import com.taahyt.amongus.tasksystem.Task;
import com.taahyt.amongus.tasksystem.TaskStep;
import com.taahyt.amongus.tasksystem.data.DataTask;
import com.taahyt.amongus.tasksystem.filter.OxygenFilterTask;
import com.taahyt.amongus.tasksystem.wiring.WiringTask;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TaskManager
{
    private AUPlayer player;

    private List<Task> completedTasks = new ArrayList<>(), tasks = new ArrayList<>();
    private List<TaskStep> activeSteps = new ArrayList<>();

    private WiringTask wiringTask;
    private DataTask dataTask;
    private OxygenFilterTask oxygenFilterTask;

    public TaskManager(AUPlayer player)
    {
        this.player = player;
        this.wiringTask = new WiringTask();
        this.dataTask = new DataTask();
        this.oxygenFilterTask = new OxygenFilterTask();

        tasks.add(wiringTask);
        tasks.add(dataTask);
        tasks.add(oxygenFilterTask);
    }

    public boolean taskIsCompleted(Task task)
    {
        return getCompletedTasks().contains(task);
    }

    public void addToCompletedTasks(Task task)
    {
        getCompletedTasks().add(task);
    }

    public boolean stepIsCompleted(Task task, TaskStep step)
    {
        return task.getCompletedSteps().contains(step);
    }

    public void addToCompletedSteps(Task task, TaskStep step)
    {
        task.completeStep(step);
    }

    public boolean stepsOfTaskAreComplete(Task task)
    {
        return task.getCompletedSteps().size() == task.getSteps().size();
    }

    public boolean isActiveStep(TaskStep step)
    {
        return getActiveSteps().contains(step);
    }

}
