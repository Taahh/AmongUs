package com.taahyt.amongus;

import com.taahyt.amongus.command.JoinCMD;
import com.taahyt.amongus.command.TasksCMD;
import com.taahyt.amongus.game.AUGame;
import com.taahyt.amongus.menus.AnimatedMenuTest;
import com.taahyt.amongus.menus.EmergencyMeetingConfirmMenu;
import com.taahyt.amongus.menus.EmergencyMeetingMenu;
import com.taahyt.amongus.menus.fixing.AdminCardSliderMenu;
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

    @Getter
    private AdminCardSliderMenu adminCardSliderMenu;

    @Getter
    private AnimatedMenuTest animatedMenuTest;



    @Override
    public void onEnable()
    {
        plugin = this;

        game = new AUGame();


        this.emergencyMeetingConfirmMenu = new EmergencyMeetingConfirmMenu();
        this.emergencyMeetingMenu = new EmergencyMeetingMenu();
        this.adminCardSliderMenu = new AdminCardSliderMenu();
        this.animatedMenuTest = new AnimatedMenuTest();


        getCommand("join").setExecutor(new JoinCMD());
        getCommand("tasks").setExecutor(new TasksCMD());

        getServer().getPluginManager().registerEvents(emergencyMeetingMenu, this);
        getServer().getPluginManager().registerEvents(emergencyMeetingConfirmMenu, this);
        getServer().getPluginManager().registerEvents(adminCardSliderMenu, this);
        getServer().getPluginManager().registerEvents(animatedMenuTest, this);
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
