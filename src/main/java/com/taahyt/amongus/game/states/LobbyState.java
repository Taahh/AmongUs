package com.taahyt.amongus.game.states;

import com.taahyt.amongus.AmongUs;
import com.taahyt.amongus.game.AUGame;
import com.taahyt.amongus.game.player.AUPlayer;
import com.taahyt.amongus.utils.GlowAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ThreadLocalRandom;

public class LobbyState extends BukkitRunnable
{

    private AUGame game;
    public LobbyState(AUGame game)
    {
        this.game = game;
    }

    private int secondsRemaining = 10;

    @Override
    public void run() {

        if (game.getPlayers().size() >= game.getMinPlayers() && secondsRemaining > 0) {
            secondsRemaining--;
            game.getPlayers().forEach(player -> player.getScoreboard().set(3, "Game Starts In: " + secondsRemaining));
            if (secondsRemaining < 4)
            {
                for (AUPlayer player : game.getPlayers()) {
                    if (secondsRemaining == 3)
                    {
                        player.getBukkitPlayer().sendTitle(ChatColor.GREEN + "3", "", 20, 20, 20);
                    }
                    if (secondsRemaining == 2)
                    {
                        player.getBukkitPlayer().sendTitle(ChatColor.RED + "2", "", 20, 20, 20);
                    }
                    if (secondsRemaining == 1)
                    {
                        player.getBukkitPlayer().sendTitle(ChatColor.BLUE + "1", "", 20, 20, 20);
                    }
                }
            }
        }
        if (secondsRemaining == 0 && game.getPlayers().size() < game.getMinPlayers()) {
            secondsRemaining = 10;
            Bukkit.broadcastMessage("Not enough players! Restarting countdown.");
        } else if (secondsRemaining == 0 && game.getPlayers().size() >= game.getMinPlayers())
        {
            game.getPlayers().forEach(player -> player.getBukkitPlayer().sendTitle(ChatColor.YELLOW + "GO!", "", 20, 20, 20));
            game.getPlayers().forEach(player -> player.getScoreboard().set(3, "Game Started!!"));


            HandlerList.unregisterAll(game.getLobbyListener());
            AmongUs.get().getServer().getPluginManager().registerEvents(game.getGameListener(), AmongUs.get());
            new InGameState(game).runTaskTimer(AmongUs.get(), 0, 20);

            game.getAlivePlayers().addAll(game.getPlayers());

            game.getAlivePlayers().get(ThreadLocalRandom.current().nextInt(game.getAlivePlayers().size())).setImposter(true);

            game.getAlivePlayers().forEach(player -> player.getScoreboard().set(0, "ROLE: " + (player.isImposter() ? "IMPOSTER" : "CREWMATE")));
            game.getAlivePlayers().forEach(player -> player.getTasks().addAll(AmongUs.get().getTaskManager().getTotalTasks()));
            game.getAlivePlayers().forEach(player -> GlowAPI.addGlowToBlock(player.getBukkitPlayer(), game.getScanner().getAdminCardSlider()));
            game.setStarted(true);
            Bukkit.getLogger().info("Switching to InGameState");
            this.cancel();
        }
    }
}
