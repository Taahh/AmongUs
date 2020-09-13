package com.taahyt.amongus.tasks.manager;

import com.taahyt.amongus.game.player.AUPlayer;
import com.taahyt.amongus.tasks.Task;
import com.taahyt.amongus.tasks.TaskStep;
import com.taahyt.amongus.tasks.admin.AdminCardTask;
import com.taahyt.amongus.tasks.data.DataTask;
import com.taahyt.amongus.tasks.filter.OxygenFilterTask;
import com.taahyt.amongus.tasks.fuel.FuelTask;
import com.taahyt.amongus.tasks.medical.MedicalTask;
import com.taahyt.amongus.tasks.wiring.WiringTask;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class TaskManager
{

    private List<Task> tasks = new ArrayList<>();

    private WiringTask wiringTask;
    private DataTask dataTask;
    private OxygenFilterTask oxygenFilterTask;
    private AdminCardTask adminCardTask;
    private FuelTask fuelTask;
    private MedicalTask medicalTask;

    public TaskManager()
    {
        this.wiringTask = new WiringTask();
        this.dataTask = new DataTask();
        this.oxygenFilterTask = new OxygenFilterTask();
        this.adminCardTask = new AdminCardTask();
        this.fuelTask = new FuelTask();
        this.medicalTask = new MedicalTask();

        tasks.add(wiringTask);
        tasks.add(dataTask);
        tasks.add(oxygenFilterTask);
        tasks.add(adminCardTask);
        tasks.add(fuelTask);
        tasks.add(medicalTask);
    }

    public boolean taskIsCompleted(Task task, AUPlayer player)
    {
        return task.completedPlayers().contains(player);
    }

    public void addToCompletedTasks(Task task, AUPlayer player)
    {
        task.completedPlayers().add(player);
    }

    public boolean stepIsCompleted(TaskStep step, AUPlayer player)
    {
        return step.completedPlayers().contains(player);
    }

    public void addToCompletedSteps(TaskStep step, AUPlayer player)
    {
        step.completedPlayers().add(player);
    }

    public boolean stepsOfTaskAreComplete(Task task, AUPlayer player)
    {
        List<TaskStep> taskList = new ArrayList<>();
        for (Task getTasks : getTasks())
        {
            for (TaskStep steps : getTasks.getSteps())
            {
                if (steps.completedPlayers().contains(player))
                {
                    taskList.add(steps);
                }
            }
        }

        return taskList.size() == task.getSteps().size();
    }

    public List<Task> getTasksCompleted(AUPlayer player)
    {
        List<Task> tasks = new ArrayList<>();
        for (Task taskList : getTasks())
        {
            if (taskList.completedPlayers().contains(player))
            {
                tasks.add(taskList);
            }
        }
        return tasks;
    }

    public List<TaskStep> getActiveSteps(AUPlayer player)
    {
        List<TaskStep> taskList = new ArrayList<>();
        for (Task getTasks : getTasks())
        {
            for (TaskStep steps : getTasks.getSteps())
            {
                if (steps.activePlayers().contains(player))
                {
                    taskList.add(steps);
                }
            }
        }
        return taskList;
    }

    public void assignDefaultSteps(AUPlayer player)
    {
        getTasks().forEach(task -> {
            task.getSteps().get(0).activePlayers().add(player);
        });
    }

    public boolean isActiveStep(TaskStep step, AUPlayer player)
    {
        return step.activePlayers().contains(player);
    }

}
