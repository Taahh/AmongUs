package com.taahyt.amongus.game.states;


import com.taahyt.amongus.AmongUs;
import com.taahyt.amongus.game.AUGame;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

public class PermState extends BukkitRunnable {


    @Getter
    private AUGame game;
    public PermState(AUGame game)
    {
        this.game = game;
    }

    @Override
    public void run()
    {
        game.getPlayers().forEach(player -> {
            player.getScoreboard().set(2, "Tasks Completed: " + AmongUs.get().getTaskManager().getTasksCompleted(player).size() + "/" + AmongUs.get().getTaskManager().getTasks().size());
        });

        if (game.getAlivePlayers().size() == 2)
        {
            //switch to end state
        }
    }
}
