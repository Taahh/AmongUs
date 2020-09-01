package com.taahyt.amongus.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;

public class AUScoreboard
{

    private Scoreboard board;
    private Objective objective;

    private List<String> entries = new ArrayList<>();

    public AUScoreboard(String name)
    {
        board = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
        objective = board.registerNewObjective(ChatColor.stripColor(name.toLowerCase().replace(" ", "_")), "dummy", name);
        objective.setDisplayName(name);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

    }


    public void attach(Player player)
    {
        player.setScoreboard(board);
    }

    public void set(int line, String value)
    {
        entries.forEach(entry -> Bukkit.getLogger().info(entry));

        Bukkit.getLogger().info(String.valueOf(entries.size()));

        if (!entries.isEmpty() && entries.size() > line && entries.get(line) != null && !entries.get(line).isEmpty())
        {
            board.resetScores(entries.get(line));
        }

        entries.add(line, value);

        objective.getScore(value).setScore(line);
    }

}
