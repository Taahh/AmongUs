package com.taahyt.amongus.tasksystem.medical;

import com.taahyt.amongus.AmongUs;
import com.taahyt.amongus.game.AUGame;
import com.taahyt.amongus.game.player.AUPlayer;
import com.taahyt.amongus.tasksystem.TaskStep;
import com.taahyt.amongus.utils.ItemBuilder;
import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public class MedScanTaskStep extends TaskStep<MedicalTask> {

    private MedScanTaskStep step;
    private BukkitTask task;

    private CompletableFuture<BukkitRunnable> futureTask;

    public MedScanTaskStep() {
        super("Med Bay: Scan yourself at the Med Bay");
        step = this;
    }

    @Override
    public MedicalTask getParent(AUPlayer player) {
        return player.getTaskManager().getMedicalTask();
    }

    @Override
    public AUGame getGame() {
        return AmongUs.get().getGame();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!getGame().isStarted()) return;
        if (event.getClickedBlock() == null) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock().getType() != Material.OAK_SIGN) return;
        if (!(event.getClickedBlock().getState() instanceof Sign)) return;


        Player player = event.getPlayer();

        AUPlayer gamePlayer = getGame().getPlayer(player.getUniqueId());
        Sign sign = (Sign) event.getClickedBlock().getState();


        //if (gamePlayer.isImposter()) return;
        if (!sign.getLocation().equals(AmongUs.get().getGame().getScanner().getMedScan())) return;

        if (gamePlayer.getTaskManager().taskIsCompleted(getParent(gamePlayer))) {
            player.sendMessage("This task was already completed!");
            return;
        }
        if (gamePlayer.getTaskManager().stepIsCompleted(getParent(gamePlayer), step)) {
            player.sendMessage("This step was already completed!");
            return;
        }

        if (!gamePlayer.getTaskManager().isActiveStep(step)) {
            player.sendMessage("Make sure you've done the other steps before proceeding to this task.");
            return;
        }

        if (task != null) return;
        if (futureTask != null) return;

        futureTask = startScan(player).whenComplete((task, ex) -> {
            Bukkit.getScheduler().cancelTask(this.task.getTaskId());

            int feet = ThreadLocalRandom.current().nextInt(3, 5);
            int inches = ThreadLocalRandom.current().nextInt(1, 12);
            int weight = ThreadLocalRandom.current().nextInt(90, 160);
            player.sendMessage("Height: " + feet + "'" + inches + "\"");
            player.sendMessage("Weight: " +  weight + " lbs." + (weight > 120 ? " DAMN YOU THICC!" : ""));
            gamePlayer.getTaskManager().addToCompletedSteps(getParent(gamePlayer), step);
            gamePlayer.getTaskManager().getActiveSteps().remove(step);
            gamePlayer.getTaskManager().addToCompletedTasks(getParent(gamePlayer));
            gamePlayer.getBukkitPlayer().closeInventory();
            gamePlayer.getBukkitPlayer().setItemOnCursor(new ItemStack(Material.AIR));
            player.sendMessage(ChatColor.GREEN + "Med Scan Completed (Med Bay - " + getParent(gamePlayer).getCompletedSteps().size() + "/" + getParent(gamePlayer).getSteps().size() + ")");
            this.task = null;
            futureTask = null;

        });
    }


    @EventHandler
    public void onClose(PlayerMoveEvent event) {
        AUPlayer player = getGame().getPlayer(event.getPlayer().getUniqueId());
        if (player.getTaskManager().stepIsCompleted(getParent(player), this)) return;
        if (player.getTaskManager().taskIsCompleted(getParent(player))) return;
        event.getPlayer().setItemOnCursor(new ItemBuilder(Material.AIR).build());
        if (task != null) {
            Bukkit.getScheduler().cancelTask(task.getTaskId());
            task = null;
        }
        if (futureTask != null)
        {
            event.getPlayer().sendMessage(ChatColor.RED + "Med Scan cancelled!");
            futureTask.cancel(true);
            futureTask = null;
        }
    }

    public CompletableFuture<BukkitRunnable> startScan(Player player)
    {
        CompletableFuture<BukkitRunnable> future = new CompletableFuture<>();
        task = new BukkitRunnable() {
            double radius = 0.8;
            Location l = player.getLocation().clone();
            @Override
            public void run() {
                for (double i = 0; i < Math.PI * 4; i += Math.PI / 24) {
                    double x = radius * Math.cos(i);
                    double y = i * 0.2; //vertically compress
                    double z = radius * Math.sin(i);
                    l.add(x, y, z);
                    player.spawnParticle(Particle.REDSTONE, l.getX(), l.getY(), l.getZ(), 0, 0.001, 1, 0, 1, new Particle.DustOptions(Color.GREEN, 1));
                    l.subtract(x, y, z);
                }

                if (this.isCancelled())
                {
                    this.cancel();
                }
            }
        }.runTaskTimer(AmongUs.get(), 0, 10);



        new BukkitRunnable() {
            @Override
            public void run() {
                future.complete(this);
            }
        }.runTaskLater(AmongUs.get(), 20 * 10);
        return future;
    }
}
