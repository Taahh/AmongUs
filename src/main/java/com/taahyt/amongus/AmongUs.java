package com.taahyt.amongus;

import com.taahyt.amongus.command.JoinCMD;
import com.taahyt.amongus.game.AUGame;
import com.taahyt.amongus.menus.EmergencyMeetingConfirmMenu;
import com.taahyt.amongus.menus.EmergencyMeetingMenu;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class AmongUs extends JavaPlugin
{

    private static AmongUs plugin;
    private AUGame game;

    @Getter
    private EmergencyMeetingConfirmMenu emergencyMeetingConfirmMenu;

    @Getter
    private EmergencyMeetingMenu emergencyMeetingMenu;

    @Override
    public void onEnable()
    {
        plugin = this;

        game = new AUGame();

        this.emergencyMeetingConfirmMenu = new EmergencyMeetingConfirmMenu();
        this.emergencyMeetingMenu = new EmergencyMeetingMenu();


        getCommand("join").setExecutor(new JoinCMD());

        getServer().getPluginManager().registerEvents(emergencyMeetingMenu, this);
        getServer().getPluginManager().registerEvents(emergencyMeetingConfirmMenu, this);
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
