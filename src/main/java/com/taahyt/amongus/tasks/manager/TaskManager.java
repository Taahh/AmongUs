package com.taahyt.amongus.tasks.manager;

import com.taahyt.amongus.tasks.Task;
import com.taahyt.amongus.tasks.wiring.WiringTask;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TaskManager
{

    private List<Task> totalTasks = new ArrayList<>();
    private WiringTask wiringTask;

    public TaskManager()
    {
        totalTasks.add(wiringTask = new WiringTask());
    }

}
