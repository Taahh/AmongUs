package com.taahyt.amongus.tasks.medical;

import com.google.common.collect.Maps;
import com.taahyt.amongus.AmongUs;
import com.taahyt.amongus.game.AUGame;
import com.taahyt.amongus.game.player.AUPlayer;
import com.taahyt.amongus.tasks.TaskStep;
import com.taahyt.amongus.utils.item.ItemBuilder;
import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public class MedScanTaskStep extends TaskStep {

    private MedScanTaskStep step;
    private Map<AUPlayer, BukkitTask> tasks = Maps.newHashMap();

    private Map<AUPlayer, CompletableFuture<BukkitRunnable>> futureTasks = Maps.newHashMap();

    private List<AUPlayer> activePlayers = new ArrayList<>(), completedPlayers = new ArrayList<>();

    public MedScanTaskStep() {
        super("Med Bay: Scan yourself at the Med Bay");
        step = this;
    }

    @Override
    public MedicalTask getParent() {
        return AmongUs.get().getTaskManager().getMedicalTask();
    }

    @Override
    public List<AUPlayer> activePlayers() {
        return activePlayers;
    }

    @Override
    public List<AUPlayer> completedPlayers() {
        return completedPlayers;
    }


    @Override
    public AUGame getGame() {
        return AmongUs.get().getGame();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() == null) return;
        if (!event.getHand().equals(EquipmentSlot.HAND)) return;
        if (!getGame().isStarted()) return;
        if (event.getClickedBlock() == null) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock().getType() != Material.OAK_SIGN && event.getClickedBlock().getType() != Material.OAK_WALL_SIGN) return;
        if (!(event.getClickedBlock().getState() instanceof Sign)) return;


        Player player = event.getPlayer();



        AUPlayer gamePlayer = getGame().getPlayer(player.getUniqueId());
        Sign sign = (Sign) event.getClickedBlock().getState();

        if (!sign.getLocation().equals(AmongUs.get().getGame().getScanner().getMedScan())) return;

        //if (gamePlayer.isImposter()) return;

        if (AmongUs.get().getTaskManager().taskIsCompleted(getParent(), gamePlayer)) {
            player.sendMessage("This task was already completed!");
            return;
        }
        if (AmongUs.get().getTaskManager().stepIsCompleted(this, gamePlayer))
        {
            player.sendMessage("This step was already completed!");
            return;
        }

         if (!AmongUs.get().getTaskManager().isActiveStep(this, gamePlayer))
         {
            player.sendMessage("Make sure you've done the other steps before proceeding to this task.");
            return;
         }

         if (gamePlayer.isScanning()) return;
         if (tasks.get(gamePlayer) != null) return;

        futureTasks.put(gamePlayer, startScan(player).whenComplete((task, ex) -> {
            Bukkit.getScheduler().cancelTask(this.tasks.get(gamePlayer).getTaskId());

            int feet = ThreadLocalRandom.current().nextInt(3, 5);
            int inches = ThreadLocalRandom.current().nextInt(1, 12);
            int weight = ThreadLocalRandom.current().nextInt(90, 160);
            player.sendMessage("Height: " + feet + "'" + inches + "\"");
            player.sendMessage("Weight: " +  weight + " lbs." + (weight > 120 ? " DAMN YOU THICC!" : ""));
            AmongUs.get().getTaskManager().addToCompletedSteps(step, gamePlayer);
            activePlayers.remove(gamePlayer);
            AmongUs.get().getTaskManager().addToCompletedTasks(getParent(), gamePlayer);
            gamePlayer.getBukkitPlayer().closeInventory();
            gamePlayer.getBukkitPlayer().setItemOnCursor(new ItemStack(Material.AIR));
            gamePlayer.setScanning(false);
            gamePlayer.setScanned(true);
            player.sendMessage(ChatColor.GREEN + "Med Scan Completed (Med Bay - " + getParent().getCompletedSteps(gamePlayer).size() + "/" + getParent().getSteps().size() + ")");
            this.tasks.remove(gamePlayer);
            futureTasks.remove(gamePlayer);
        }));
    }


    @EventHandler
    public void onClose(PlayerMoveEvent event) {
        if (event.getFrom().getWorld() == event.getTo().getWorld()
                && event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ()
                && event.getFrom().getBlockY() == event.getTo().getBlockY())
            return;
        AUPlayer player = getGame().getPlayer(event.getPlayer().getUniqueId());
        if (AmongUs.get().getTaskManager().stepIsCompleted(step, player)) return;
        if (AmongUs.get().getTaskManager().taskIsCompleted(getParent(), player)) return;
        if (player.isScanned()) return;
        if (!player.isScanning()) return;
        player.setScanning(false);
        player.getBukkitPlayer().setItemOnCursor(new ItemBuilder(Material.AIR).build());
        if (tasks.get(player) != null) {
            Bukkit.getScheduler().cancelTask(tasks.get(player).getTaskId());
            tasks.remove(player);
        }
        if (futureTasks.get(player) != null)
        {
            player.getBukkitPlayer().sendMessage(ChatColor.RED + "Med Scan was Cancelled!");
            futureTasks.get(player).cancel(true);
            futureTasks.remove(player);
        }

    }

    public CompletableFuture<BukkitRunnable> startScan(Player player)
    {

        AUPlayer auPlayer = AmongUs.get().getGame().getPlayer(player.getUniqueId());
        auPlayer.setScanning(true);

        CompletableFuture<BukkitRunnable> future = new CompletableFuture<>();
        tasks.put(auPlayer,new BukkitRunnable() {
            double radius = 0.8;
            Location l = player.getLocation().clone();
            @Override
            public void run() {
                for (double i = 0; i < Math.PI * 4; i += Math.PI / 24) {
                    double x = radius * Math.cos(i);
                    double y = i * 0.2; //vertically compress
                    double z = radius * Math.sin(i);
                    l.add(x, y, z);
                    //player.getWorld().spawnParticle(Particle.REDSTONE, l.getX(), l.getY(), l.getZ(), 0, 0.001, 1, 0, 1, new Particle.DustOptions(Color.GREEN, 1));
                    player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, l.getX(), l.getY(), l.getZ(), 1);
                    l.subtract(x, y, z);
                }

                if (this.isCancelled())
                {
                    this.cancel();
                }
            }
        }.runTaskTimer(AmongUs.get(), 0, 10));



        new BukkitRunnable() {
            @Override
            public void run() {
                future.complete(this);
            }
        }.runTaskLater(AmongUs.get(), 20 * 10);
        return future;
    }
}
