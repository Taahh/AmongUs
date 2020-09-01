package com.taahyt.amongus.game.states;

import com.taahyt.amongus.AmongUs;
import com.taahyt.amongus.game.AUGame;
import org.bukkit.Bukkit;
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
            Bukkit.broadcastMessage("The game will start in " + secondsRemaining + " seconds.");
        }
        if (secondsRemaining == 0 && game.getPlayers().size() < game.getMinPlayers()) {
            secondsRemaining = 10;
            Bukkit.broadcastMessage("Not enough players! Restarting countdown.");
        } else if (secondsRemaining == 0 && game.getPlayers().size() >= game.getMinPlayers())
        {
            HandlerList.unregisterAll(game.getLobbyListener());
            AmongUs.get().getServer().getPluginManager().registerEvents(game.getGameListener(), AmongUs.get());
            new InGameState(game).runTaskTimer(AmongUs.get(), 0, 20);

            game.getAlivePlayers().addAll(game.getPlayers());

            game.getAlivePlayers().get(ThreadLocalRandom.current().nextInt(game.getAlivePlayers().size())).setImposter(true);

            game.getAlivePlayers().forEach(player -> {
                player.getScoreboard().set(0, "ROLE: " + (player.isImposter() ? "IMPOSTER" : "PLAYER"));
            });
            Bukkit.getLogger().info("Switching to InGameState");
            this.cancel();
        }



    }
}
