package com.taahyt.amongus.game.states;


import com.taahyt.amongus.game.AUGame;
import lombok.Getter;
import org.bukkit.Bukkit;
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

    }
}
