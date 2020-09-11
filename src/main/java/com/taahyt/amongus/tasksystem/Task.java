package com.taahyt.amongus.tasksystem;

import java.util.List;

public abstract class Task<T extends Task> implements Cloneable
{

    public abstract List<TaskStep> getSteps();

    public abstract List<TaskStep> getCompletedSteps();

    public abstract String getID();

    public abstract void completeStep(TaskStep step);

    public abstract T cloneTask();


}
