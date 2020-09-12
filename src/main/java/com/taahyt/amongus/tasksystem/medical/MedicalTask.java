package com.taahyt.amongus.tasksystem.medical;

import com.taahyt.amongus.AmongUs;
import com.taahyt.amongus.tasksystem.Task;
import com.taahyt.amongus.tasksystem.TaskStep;

import java.util.ArrayList;
import java.util.List;

public class MedicalTask extends Task<MedicalTask>
{

    private List<TaskStep> completedStep = new ArrayList<>(), steps = new ArrayList<>();

    private MedScanTaskStep medScanTaskStep;

    public MedicalTask()
    {
        medScanTaskStep = new MedScanTaskStep();
        steps.add(medScanTaskStep);

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
        return "medical_task";
    }


    @Override
    public void completeStep(TaskStep step) {
        getCompletedSteps().add(step);
    }

    @Override
    public MedicalTask cloneTask() {
        try {
            return (MedicalTask) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

}
