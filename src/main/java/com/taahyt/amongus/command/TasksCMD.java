package com.taahyt.amongus.command;

import com.taahyt.amongus.AmongUs;
import com.taahyt.amongus.game.player.AUPlayer;
import com.taahyt.amongus.tasksystem.Task;
import com.taahyt.amongus.tasksystem.TaskStep;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TasksCMD implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Player player = (Player) sender;
        AUPlayer gamePlayer = AmongUs.get().getGame().getPlayer(player.getUniqueId());
        for (Task task : gamePlayer.getTaskManager().getTasks())
        {
            String name = task.getID();
            name = name.replace("_", " ");
            name = StringUtils.capitalize(name);
            player.sendMessage(gamePlayer.getTaskManager().taskIsCompleted(task) ? ChatColor.GREEN + name + " (" + task.getCompletedSteps().size() + "/" + task.getSteps().size() + ")" : ChatColor.RED + name + " (" + task.getCompletedSteps().size() + "/" + task.getSteps().size() + ")");
        }

        for (TaskStep step : gamePlayer.getTaskManager().getActiveSteps())
        {
            player.sendMessage(ChatColor.GOLD + step.getDescription());
        }

        return true;
    }
}
