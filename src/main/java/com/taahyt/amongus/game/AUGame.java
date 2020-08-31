package com.taahyt.amongus.game;

import com.google.common.collect.Maps;
import com.taahyt.amongus.AmongUs;
import com.taahyt.amongus.game.player.AUPlayer;
import com.taahyt.amongus.game.states.PermState;
import com.taahyt.amongus.map.MapScanner;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class AUGame
{

    private int maxPlayers;
    private int minPlayers;

    private Location cornerA;
    private Location cornerB;

    private Location lobbyA;
    private Location lobbyB;

    private List<AUPlayer> players = new ArrayList<AUPlayer>();
    private List<AUPlayer> alivePlayers = new ArrayList<AUPlayer>();

    private Map<AUPlayer, Integer> votes = Maps.newHashMap();

    private MapScanner scanner;

    public AUGame()
    {
        setMaxPlayers(6);
        setMinPlayers(1);

        this.lobbyA = new Location(Bukkit.getWorld("world"), 126.29, 78.00, -8.46);
        this.lobbyB = new Location(Bukkit.getWorld("world"), 110.70, 71.00, -25.42);


        new PermState(this).runTaskTimer(AmongUs.get(), 0, 20);
        scanner = new MapScanner();
        scanner.parseLobby(this);

        Bukkit.getLogger().info(String.valueOf(scanner.getLobbySeats().size()));

    }


}
