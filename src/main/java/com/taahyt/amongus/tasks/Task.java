package com.taahyt.amongus.tasks;

import com.taahyt.amongus.game.player.AUPlayer;

import java.util.List;

public interface Task
{

    List<AUPlayer> completedPlayers();

    List<TaskStep> getSteps();

    String getID();

    List<TaskStep> getCompletedSteps(AUPlayer player);

}
