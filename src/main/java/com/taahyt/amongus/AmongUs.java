package com.taahyt.amongus;

import com.taahyt.amongus.command.JoinCMD;
import com.taahyt.amongus.game.AUGame;
import org.bukkit.plugin.java.JavaPlugin;

public class AmongUs extends JavaPlugin
{

    private static AmongUs plugin;
    private AUGame game;

    @Override
    public void onEnable()
    {
        plugin = this;
        game = new AUGame();

        getCommand("join").setExecutor(new JoinCMD());
    }


    @Override
    public void onDisable()
    {

    }

    public static AmongUs get() {
        return plugin;
    }

    public AUGame getGame() {
        return game;
    }
}
