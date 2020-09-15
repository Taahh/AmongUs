package com.taahyt.amongus;

import com.taahyt.amongus.command.JoinCMD;
import com.taahyt.amongus.command.TasksCMD;
import com.taahyt.amongus.customization.KitManager;
import com.taahyt.amongus.game.AUGame;
import com.taahyt.amongus.meeting.ConfirmationMenu;
import com.taahyt.amongus.meeting.EmergencyMeetingHandler;
import com.taahyt.amongus.tasks.manager.TaskManager;
import com.taahyt.amongus.utils.packets.PacketInjector;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class AmongUs extends JavaPlugin
{

    private static AmongUs plugin;
    private AUGame game;

    @Getter
    private ConfirmationMenu confirmationMenu;

    @Getter
    private EmergencyMeetingHandler emergencyMeetingHandler;

    @Getter
    private KitManager kitManager;

    @Getter
    private PacketInjector injector;

    @Getter
    private TaskManager taskManager;

    @Override
    public void onEnable()
    {
        plugin = this;

        game = new AUGame();

        this.injector = new PacketInjector();
        this.taskManager = new TaskManager();

        this.kitManager = new KitManager();


        this.confirmationMenu = new ConfirmationMenu(game);
        this.emergencyMeetingHandler = new EmergencyMeetingHandler();


        getCommand("join").setExecutor(new JoinCMD());
        getCommand("tasks").setExecutor(new TasksCMD());

        getServer().getPluginManager().registerEvents(emergencyMeetingHandler, this);
        getServer().getPluginManager().registerEvents(confirmationMenu, this);
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
