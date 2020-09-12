package com.taahyt.amongus.command;

import com.taahyt.amongus.AmongUs;
import com.taahyt.amongus.game.player.AUPlayer;
import com.taahyt.amongus.tasksystem.Task;
import com.taahyt.amongus.tasksystem.TaskStep;
import com.taahyt.amongus.utils.NMSUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

public class TasksCMD implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Player player = (Player) sender;
        AUPlayer gamePlayer = AmongUs.get().getGame().getPlayer(player.getUniqueId());
        player.sendMessage(ChatColor.GRAY + "-----------------------------------");
        for (Task task : gamePlayer.getTaskManager().getTasks())
        {
            String name = task.getID();
            name = name.replace("_", " ");
            name = WordUtils.capitalizeFully(name);
            player.sendMessage(gamePlayer.getTaskManager().taskIsCompleted(task) ? ChatColor.GREEN + name + " (" + task.getCompletedSteps().size() + "/" + task.getSteps().size() + ")" : ChatColor.RED + name + " (" + task.getCompletedSteps().size() + "/" + task.getSteps().size() + ")");
        }

        for (TaskStep step : gamePlayer.getTaskManager().getActiveSteps())
        {
            player.sendMessage(ChatColor.GOLD + step.getDescription());
        }
        player.sendMessage(ChatColor.GRAY + "-----------------------------------");
        return true;
    }
}
