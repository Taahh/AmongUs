package com.taahyt.amongus.tasksystem.data;

import com.taahyt.amongus.AmongUs;
import com.taahyt.amongus.game.AUGame;
import com.taahyt.amongus.game.player.AUPlayer;
import com.taahyt.amongus.tasksystem.TaskStep;
import com.taahyt.amongus.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class DownloadTaskStep extends TaskStep<DataTask>
{

    private DownloadTaskStep step;

    private Inventory inventory;

    private BukkitTask task;
    private int downloadPercentage = 0;
    private boolean started = false;

    public DownloadTaskStep()
    {
        super("Weapons: Download data from the ship to your tablet.");
        step = this;
        this.inventory = Bukkit.createInventory(null, 27, "§aDownload Task");
    }

    @Override
    public DataTask getParent(AUPlayer player) {
        return player.getTaskManager().getDataTask();
    }

    @Override
    public AUGame getGame() {
        return AmongUs.get().getGame();
    }

    public void openGUI(Player player)
    {

        for (int i = 0; i <= inventory.getSize() - 1; i++)
        {
            if (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR)
            {
                inventory.setItem(i, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName("§r").build());
            }
        }

        inventory.setItem(10, new ItemBuilder(Material.REDSTONE_TORCH).setDisplayName("§aNetwork").build());
        inventory.setItem(13, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setDisplayName("§9Download").build());
        inventory.setItem(16, new ItemBuilder(Material.CHEST).setDisplayName("§6Tablet (0%)").build());

        player.openInventory(inventory);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event)
    {
        if (!getGame().isStarted()) return;
        if (event.getClickedBlock() == null) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock().getType() != Material.OAK_SIGN) return;
        if (!(event.getClickedBlock().getState() instanceof Sign)) return;


        Player player = event.getPlayer();

        AUPlayer gamePlayer = getGame().getPlayer(player.getUniqueId());
        Sign sign = (Sign) event.getClickedBlock().getState();


        //if (gamePlayer.isImposter()) return;
        if (!sign.getLocation().equals(AmongUs.get().getGame().getScanner().getDownloadTask())) return;

        if (gamePlayer.getTaskManager().taskIsCompleted(getParent(gamePlayer))) {
            player.sendMessage("This task was already completed!");
            return;
        }
        if (gamePlayer.getTaskManager().stepIsCompleted(getParent(gamePlayer), step))
        {
            player.sendMessage("This step was already completed!");
            return;
        }

         if (!gamePlayer.getTaskManager().isActiveStep(step))
         {
             player.sendMessage("Make sure you've done the other steps before proceeding to this task.");
             event.setCancelled(true);
             return;
         }
        openGUI(player);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event)
    {
        if (event.getClickedInventory() == null) return;
        if (event.getClickedInventory().hashCode() != this.inventory.hashCode())
        {
            return;
        }
        if (event.getCurrentItem() == null) return;

        if (event.getCurrentItem().getType() == Material.GRAY_STAINED_GLASS_PANE)
        {
            event.setCancelled(true);
            return;
        }


        if (event.getSlot() == 10 || event.getSlot() == 13 || event.getSlot() == 16)
        {
            event.setCancelled(true);
        }
        ItemStack item = event.getCurrentItem();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        if (!meta.hasDisplayName()) return;

        event.setCancelled(true);

        if (meta.getDisplayName().equalsIgnoreCase("§9Download"))
        {
            if (task != null) return;
            task = new BukkitRunnable() {
                @Override
                public void run() {
                    downloadPercentage+=10;
                    inventory.setItem(16, new ItemBuilder(Material.CHEST).setDisplayName("§6Tablet (" + downloadPercentage + "%)").build());
                    if (downloadPercentage == 100)
                    {
                        event.getWhoClicked().closeInventory();
                        AUPlayer player = getGame().getPlayer(event.getWhoClicked().getUniqueId());



                        player.getTaskManager().addToCompletedSteps(getParent(player), step);
                        player.getTaskManager().getActiveSteps().remove(step);
                        player.getTaskManager().getActiveSteps().add(getParent(player).getSteps().get(1));
                        event.getWhoClicked().sendMessage(ChatColor.GREEN + "Downloaded Data from Network (Data Task - " + getParent(player).getCompletedSteps().size() + "/" + getParent(player).getSteps().size() + ")");
                        if (player.getTaskManager().stepsOfTaskAreComplete(getParent(player)))
                        {
                            player.getTaskManager().addToCompletedTasks(getParent(player));
                        }
                        task = null;
                        this.cancel();
                    }
                }
            }.runTaskTimer(AmongUs.get(), 0, 20);
            event.setCancelled(true);
        }




    }

    @EventHandler
    public void onClose(InventoryCloseEvent event)
    {
        if (event.getInventory().hashCode() != inventory.hashCode()) return;

        AUPlayer player = getGame().getPlayer(event.getPlayer().getUniqueId());
        if (player.getTaskManager().stepIsCompleted(getParent(player), this)) return;
        if (player.getTaskManager().taskIsCompleted(getParent(player))) return;
        event.getPlayer().setItemOnCursor(new ItemBuilder(Material.AIR).build());
        if (task != null) task.cancel();
        task = null;
        downloadPercentage = 0;

    }

}
