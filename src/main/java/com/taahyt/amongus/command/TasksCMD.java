package com.taahyt.amongus.command;

import com.taahyt.amongus.AmongUs;
import com.taahyt.amongus.game.player.AUPlayer;
import com.taahyt.amongus.tasks.Task;
import com.taahyt.amongus.tasks.TaskStep;
import org.apache.commons.lang.WordUtils;
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
        player.sendMessage(ChatColor.GRAY + "-----------------------------------");
        for (Task task : AmongUs.get().getTaskManager().getTasks())
        {
            String name = task.getID();
            name = name.replace("_", " ");
            name = WordUtils.capitalizeFully(name);
            player.sendMessage(task.completedPlayers().contains(gamePlayer) ? ChatColor.GREEN + name + " (" + task.getCompletedSteps(gamePlayer).size() + "/" + task.getSteps().size() + ")" : ChatColor.RED + name + " (" + task.getCompletedSteps(gamePlayer).size() + "/" + task.getSteps().size() + ")");
        }

        for (TaskStep step : AmongUs.get().getTaskManager().getActiveSteps(gamePlayer))
        {
            player.sendMessage(ChatColor.GOLD + step.getDescription());
        }
        player.sendMessage(ChatColor.GRAY + "-----------------------------------");
        return true;
    }
}
