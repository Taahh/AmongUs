package com.taahyt.amongus.tasks.admin;

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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AdminCardSliderTaskStep extends TaskStep
{

    private Map<AUPlayer, Inventory> inventories = Maps.newHashMap();


    private List<AUPlayer> activePlayers = new ArrayList<>(), completedPlayers = new ArrayList<>();

    public AdminCardSliderTaskStep()
    {
        super("Admin: Slide the Card to gain access to the Systems");
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
    public AdminCardTask getParent() {
        return AmongUs.get().getTaskManager().getAdminCardTask();
    }

    @Override
    public AUGame getGame() {
        return AmongUs.get().getGame();
    }

    public void openGUI(Player player)
    {

        AUPlayer auPlayer = getGame().getPlayer(player.getUniqueId());
        inventories.put(auPlayer, Bukkit.createInventory(null, 27, "§aAdmin Card (Slide)"));
        Inventory inventory = inventories.get(auPlayer);

        for (int i = 0; i <= inventory.getSize() - 1; i++)
        {
            if (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR)
            {
                inventory.setItem(i, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName("§r").build());
            }
        }

        inventory.setItem(10, new ItemBuilder(Material.MUSIC_DISC_STRAD).setDisplayName("§7Card").build());
        inventory.setItem(13, new ItemBuilder(Material.DISPENSER).setDisplayName("§9Card Machine").build());

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


        if (!sign.getLocation().equals(AmongUs.get().getGame().getScanner().getAdminCardSlider())) return;


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
        if (!inventories.containsKey(getGame().getPlayer(event.getWhoClicked().getUniqueId()))) return;
        if (event.getClickedInventory().hashCode() != inventories.get(getGame().getPlayer(event.getWhoClicked().getUniqueId())).hashCode())
        {
            return;
        }
        if (event.getCurrentItem() == null) return;

        if (event.getCurrentItem().getType() == Material.GRAY_STAINED_GLASS_PANE)
        {
            event.setCancelled(true);
            return;
        }

        if (event.getCurrentItem().getType() != Material.DISPENSER) return;
        if (event.getCurrentItem().getType() == Material.DISPENSER)
        {
            event.setCancelled(true);
        }

        if (event.getCursor() == null) return;
        if (event.getCursor().getType() != Material.MUSIC_DISC_STRAD) return;


        event.getWhoClicked().setItemOnCursor(new ItemStack(Material.AIR));
        AUPlayer player = getGame().getPlayer(event.getWhoClicked().getUniqueId());
        AmongUs.get().getTaskManager().addToCompletedSteps(this, player);
        AmongUs.get().getTaskManager().addToCompletedTasks(getParent(), player);
        activePlayers.remove(player);
        player.getBukkitPlayer().closeInventory();
        player.getBukkitPlayer().setItemOnCursor(new ItemStack(Material.AIR));
        event.getWhoClicked().sendMessage(ChatColor.GREEN + "Access to Admin Console Granted (Admin Card Task - " + getParent().getCompletedSteps(player).size() + "/" + getParent().getSteps().size() + ")");
        inventories.remove(player);
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
        inventories.remove(player);
    }

}
