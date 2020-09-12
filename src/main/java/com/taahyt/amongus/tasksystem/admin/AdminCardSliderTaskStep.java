package com.taahyt.amongus.tasksystem.admin;

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

public class AdminCardSliderTaskStep extends TaskStep<AdminCardTask>
{

    private AdminCardSliderTaskStep step;

    private Inventory inventory;

    public AdminCardSliderTaskStep()
    {
        super("Admin: Slide the Card to gain access to the Systems");
        step = this;
        this.inventory = Bukkit.createInventory(null, 27, "§aAdmin Card (Slide)");
    }

    @Override
    public AdminCardTask getParent(AUPlayer player) {
        return player.getTaskManager().getAdminCardTask();
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

        inventory.setItem(10, new ItemBuilder(Material.MUSIC_DISC_STRAD).setDisplayName("§7Card").build());
        inventory.setItem(13, new ItemBuilder(Material.DISPENSER).setDisplayName("§9Card Machine").build());

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
        if (!sign.getLocation().equals(AmongUs.get().getGame().getScanner().getAdminCardSlider())) return;

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

        if (event.getCurrentItem().getType() != Material.DISPENSER) return;
        if (event.getCurrentItem().getType() == Material.DISPENSER)
        {
            event.setCancelled(true);
        }

        if (event.getCursor() == null) return;
        if (event.getCursor().getType() != Material.MUSIC_DISC_STRAD) return;


        event.getWhoClicked().setItemOnCursor(new ItemStack(Material.AIR));
        AUPlayer player = getGame().getPlayer(event.getWhoClicked().getUniqueId());
        player.getTaskManager().addToCompletedSteps(getParent(player), step);
        player.getTaskManager().addToCompletedTasks(getParent(player));
        player.getTaskManager().getActiveSteps().remove(step);
        player.getBukkitPlayer().closeInventory();
        player.getBukkitPlayer().setItemOnCursor(new ItemStack(Material.AIR));
        event.getWhoClicked().sendMessage(ChatColor.GREEN + "Access to Admin Console Granted (Admin Card Task - " + getParent(player).getCompletedSteps().size() + "/" + getParent(player).getSteps().size() + ")");
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event)
    {
        if (event.getInventory().hashCode() != inventory.hashCode()) return;

        AUPlayer player = getGame().getPlayer(event.getPlayer().getUniqueId());
        if (player.getTaskManager().stepIsCompleted(getParent(player), this)) return;
        if (player.getTaskManager().taskIsCompleted(getParent(player))) return;
        event.getPlayer().setItemOnCursor(new ItemBuilder(Material.AIR).build());
    }

}
