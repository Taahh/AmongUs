package com.taahyt.amongus.tasksystem.data;

import com.taahyt.amongus.AmongUs;
import com.taahyt.amongus.tasksystem.Task;
import com.taahyt.amongus.tasksystem.TaskStep;

import java.util.ArrayList;
import java.util.List;

public class DataTask extends Task<DataTask>
{

    private List<TaskStep> completedStep = new ArrayList<>(), steps = new ArrayList<>();

    private DownloadTaskStep downloadTaskStep;
    private UploadTaskStep uploadTaskStep;


    public DataTask()
    {
        downloadTaskStep = new DownloadTaskStep();
        uploadTaskStep = new UploadTaskStep();
        steps.add(downloadTaskStep);
        steps.add(uploadTaskStep);
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
        return "data_task";
    }

    @Override
    public void completeStep(TaskStep step) {
        getCompletedSteps().add(step);
    }

    @Override
    public DataTask cloneTask() {
        try {
            return (DataTask) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

}
