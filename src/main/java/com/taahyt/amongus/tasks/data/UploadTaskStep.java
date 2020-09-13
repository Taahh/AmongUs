package com.taahyt.amongus.tasks.data;

import com.google.common.collect.Maps;
import com.taahyt.amongus.AmongUs;
import com.taahyt.amongus.game.AUGame;
import com.taahyt.amongus.game.player.AUPlayer;
import com.taahyt.amongus.tasks.TaskStep;
import com.taahyt.amongus.utils.item.ItemBuilder;
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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UploadTaskStep extends TaskStep
{

    private UploadTaskStep step;

    private Map<AUPlayer, Inventory> inventories = Maps.newHashMap();

    private Map<AUPlayer, BukkitTask> runnables = Maps.newHashMap();
    private Map<AUPlayer, Integer> uploadPercentages = Maps.newHashMap();

    private List<AUPlayer> activePlayers = new ArrayList<>(), completedPlayers = new ArrayList<>();

    public UploadTaskStep()
    {
        super("Admin: Upload data from your tablet to headquarters");
        step = this;
    }

    @Override
    public DataTask getParent() {
        return AmongUs.get().getTaskManager().getDataTask();
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

    public void openGUI(Player player)
    {

        AUPlayer auPlayer = getGame().getPlayer(player.getUniqueId());
        inventories.put(auPlayer, Bukkit.createInventory(null, 27, "§aUpload Task"));
        Inventory inventory = inventories.get(auPlayer);

        for (int i = 0; i <= inventory.getSize() - 1; i++)
        {
            if (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR)
            {
                inventory.setItem(i, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName("§r").build());
            }
        }

        inventory.setItem(10, new ItemBuilder(Material.CHEST).setDisplayName("§6Tablet").build());
        inventory.setItem(13, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setDisplayName("§9Upload").build());
        inventory.setItem(16, new ItemBuilder(Material.REDSTONE_TORCH).setDisplayName("§aHeadquarters (0%)").build());

        uploadPercentages.put(auPlayer, 0);

        player.openInventory(inventory);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event)
    {
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

        if (!sign.getLocation().equals(AmongUs.get().getGame().getScanner().getUploadTask())) return;


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
        openGUI(player);

    }

    @EventHandler
    public void onClick(InventoryClickEvent event)
    {
        if (event.getClickedInventory() == null) return;

        AUPlayer auPlayer = getGame().getPlayer(event.getWhoClicked().getUniqueId());

        if (!inventories.containsKey(auPlayer)) return;

        Inventory inventory = inventories.get(auPlayer);

        if (event.getClickedInventory().hashCode() != inventory.hashCode())
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

        if (meta.getDisplayName().equalsIgnoreCase("§9Upload"))
        {
            if (hasBukkitTask(auPlayer)) return;
            setBukkitTask(auPlayer, new BukkitRunnable() {
                @Override
                public void run() {
                    setUploadPercentage(auPlayer, 10);
                    inventory.setItem(16, new ItemBuilder(Material.REDSTONE_TORCH).setDisplayName("§aHeadquarters (" + getUploadPercentage(auPlayer) + "%)").build());
                    if (getUploadPercentage(auPlayer) == 100)
                    {
                        event.getWhoClicked().closeInventory();

                        AmongUs.get().getTaskManager().addToCompletedSteps(step, auPlayer);
                        activePlayers.remove(auPlayer);
                        event.getWhoClicked().sendMessage(ChatColor.GREEN + "Uploaded Data to HQ (Data Task - " + getParent().getCompletedSteps(auPlayer).size() + "/" + getParent().getSteps().size() + ")");
                        AmongUs.get().getTaskManager().addToCompletedTasks(getParent(), auPlayer);
                        inventories.remove(auPlayer);
                        setBukkitTask(auPlayer, null);
                        this.cancel();
                    }
                }
            }.runTaskTimer(AmongUs.get(), 0, 20));
            event.setCancelled(true);
        }




    }

    @EventHandler
    public void onClose(InventoryCloseEvent event)
    {
        if (!inventories.containsKey(getGame().getPlayer(event.getPlayer().getUniqueId()))) return;

        if (event.getInventory().hashCode() != inventories.get(getGame().getPlayer(event.getPlayer().getUniqueId())).hashCode()) return;

        AUPlayer player = getGame().getPlayer(event.getPlayer().getUniqueId());
        if (AmongUs.get().getTaskManager().stepIsCompleted(this, player)) return;
        if (AmongUs.get().getTaskManager().taskIsCompleted(getParent(), player)) return;
        event.getPlayer().setItemOnCursor(new ItemBuilder(Material.AIR).build());
        if (hasBukkitTask(player)) runnables.get(player).cancel();
        runnables.remove(player);
        uploadPercentages.remove(player);
        inventories.remove(player);

    }

    private int getUploadPercentage(AUPlayer player)
    {
        return uploadPercentages.get(player);
    }

    private void setUploadPercentage(AUPlayer player, int increment)
    {
        uploadPercentages.put(player, getUploadPercentage(player) + increment);
    }

    private void setBukkitTask(AUPlayer player, BukkitTask task)
    {
        runnables.put(player, task);
    }

    private boolean hasBukkitTask(AUPlayer player)
    {
        return runnables.containsKey(player);
    }


}
