package com.taahyt.amongus.game;

import com.google.common.collect.Maps;
import com.taahyt.amongus.AmongUs;
import com.taahyt.amongus.game.player.AUPlayer;
import com.taahyt.amongus.game.states.LobbyState;
import com.taahyt.amongus.game.states.PermState;
import com.taahyt.amongus.listeners.GameListener;
import com.taahyt.amongus.listeners.LobbyListener;
import com.taahyt.amongus.map.MapScanner;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Getter
@Setter
public class AUGame
{

    private int maxPlayers;
    private int minPlayers;

    private int maxImposters = 1;

    private Location cornerA;
    private Location cornerB;

    private Location lobbyA;
    private Location lobbyB;

    private Location gameA;
    private Location gameB;

    private List<AUPlayer> players = new ArrayList<AUPlayer>();
    private List<AUPlayer> alivePlayers = new ArrayList<AUPlayer>();

    private Map<AUPlayer, Integer> votes = Maps.newHashMap();

    private MapScanner scanner;

    private boolean started;
    private boolean waitingOnVote;
    private boolean voting;

    private LobbyListener lobbyListener;
    private GameListener gameListener;

    private boolean emergencyCooldown;


    public AUGame()
    {
        setMaxPlayers(6);
        setMinPlayers(1);

        this.lobbyA = new Location(Bukkit.getWorld("world"), 126.29, 78.00, -8.46);
        this.lobbyB = new Location(Bukkit.getWorld("world"), 110.70, 71.00, -25.42);

        this.gameA = new Location(Bukkit.getWorld("world"), 110.54, 78.00, 7.40);
        this.gameB = new Location(Bukkit.getWorld("world"), 126.30, 72.00, -8.30);


        new PermState(this).runTaskTimer(AmongUs.get(), 0, 20);
        new LobbyState(this).runTaskTimer(AmongUs.get(), 0, 20);


        scanner = new MapScanner();
        scanner.parseLobby(this);
        scanner.parseMap(this);

        this.lobbyListener = new LobbyListener();
        this.gameListener = new GameListener();
        AmongUs.get().getServer().getPluginManager().registerEvents(lobbyListener, AmongUs.get());

    }

    public AUPlayer getPlayer(UUID uuid)
    {
        for (AUPlayer players : getPlayers())
        {
            if (players.getUuid().toString().equalsIgnoreCase(uuid.toString()))
            {
                return players;
            }
        }
        return null;
    }

    public AUPlayer getAlivePlayer(UUID uuid)
    {
        for (AUPlayer players : getAlivePlayers())
        {
            if (players.getUuid().toString().equalsIgnoreCase(uuid.toString()))
            {
                return players;
            }
        }
        return null;
    }

    public AUPlayer getAlivePlayer(String name)
    {
        for (AUPlayer players : getAlivePlayers())
        {
            if (players.getBukkitPlayer().getName().equalsIgnoreCase(name))
            {
                return players;
            }
        }
        return null;
    }

    public boolean isAlive(UUID uuid)
    {
        for (AUPlayer players : getAlivePlayers())
        {
            if (players.getUuid().toString().equalsIgnoreCase(uuid.toString()))
            {
                return true;
            }
        }
        return false;
    }

    public void vote(AUPlayer voted)
    {
        if (getVotes().containsKey(voted))
        {
            int votes = getVotes().get(voted);
            votes+=1;
            getVotes().put(voted, votes);
        } else {
            int votes = 1;
            getVotes().put(voted, votes);
        }
    }

    public boolean allVoted()
    {
        List<AUPlayer> players = getAlivePlayers()
                .stream()
                .filter(AUPlayer::isVoted)
                .collect(toList());
        if (players.size() == getAlivePlayers().size())
        {
            return true;
        }
        return false;
    }

    public AUPlayer getVoted()
    {
        return sortMap(getVotes()).entrySet().iterator().next().getKey();
    }

    public boolean voteTie()
    {
        List<Map.Entry<AUPlayer, Integer>> votes = sortMap(getVotes()).entrySet().stream().limit(2).collect(toList());
        try {
            votes.get(1);
        } catch (Exception e)
        {
            return false;
        }
        if (votes.get(0).getValue() == votes.get(1).getValue())
        {
            return true;
        }
        return false;
    }

    public Map<AUPlayer, Integer> sortMap(Map<AUPlayer, Integer> unsortedMap) {
        return unsortedMap.entrySet()
            .stream()
            .sorted(Map.Entry.<AUPlayer, Integer>comparingByValue().reversed())
            .collect(Collectors.toMap(
                Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public void kill(AUPlayer player)
    {
        player.getBukkitPlayer().setHealth(0);
        player.getBukkitPlayer().spigot().respawn();
        player.getBukkitPlayer().setGameMode(GameMode.SPECTATOR);
        getAlivePlayers().remove(player);
    }


}
