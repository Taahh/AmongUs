package com.taahyt.amongus.tasksystem.wiring;

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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ElectricalWiringTaskStep extends TaskStep<WiringTask>
{
    private ElectricalWiringTaskStep step;

    private Inventory inventory;

    public List<ItemStack> RANDOM_COLORS = new ArrayList<>();

    private int wiresLeft = 3;

    public ElectricalWiringTaskStep()
    {
        super("Electrical: Fix the Wiring");
        step = this;
        this.inventory = Bukkit.createInventory(null, 54, "§aWiring Task PT 1");
        RANDOM_COLORS.add(new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).setDisplayName("§e§lYELLOW").build());
        RANDOM_COLORS.add(new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayName("§c§lRED").build());
        RANDOM_COLORS.add(new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE).setDisplayName("§9§lBLUE").build());
    }


    @Override
    public WiringTask getParent(AUPlayer player) {
        return player.getTaskManager().getWiringTask();
    }

    @Override
    public AUGame getGame() {
        return AmongUs.get().getGame();
    }

    public ItemStack getRandomItem()
    {
        ItemStack item = RANDOM_COLORS.get(ThreadLocalRandom.current().nextInt(RANDOM_COLORS.size()));
        RANDOM_COLORS.remove(item);
        return item;
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

        inventory.setItem(18, new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayName("§c§lRED").build());
        inventory.setItem(27, new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE).setDisplayName("§9§lBLUE").build());
        inventory.setItem(36, new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).setDisplayName("§e§lYELLOW").build());

        inventory.setItem(26, getRandomItem());
        inventory.setItem(35, getRandomItem());
        inventory.setItem(44, getRandomItem());

        player.openInventory(inventory);
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
        if (!sign.getLocation().equals(AmongUs.get().getGame().getScanner().getWiringTask1())) return;
        //if (gamePlayer.isImposter()) return;
        if (gamePlayer.getTaskManager().taskIsCompleted(getParent(gamePlayer))) {
            player.sendMessage("This task was already completed!");
            return;
        }

        if (gamePlayer.getTaskManager().stepIsCompleted(getParent(gamePlayer), this))
        {
            player.sendMessage("This step was already completed!");
            return;
        }

         if (!gamePlayer.getTaskManager().isActiveStep(this))
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


        if (event.getSlot() == 26 || event.getSlot() == 35 || event.getSlot() == 44)
        {
            event.setCancelled(true);
        }

        if (event.getCursor() == null) return;

        ItemStack item = event.getCursor();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        if (!meta.hasDisplayName()) return;

        event.setCancelled(true);

        if (meta.getDisplayName().equalsIgnoreCase("§c§lRED"))
        {
            if (event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§c§lRED"))
            {
                wiresLeft--;
                event.getWhoClicked().setItemOnCursor(new ItemBuilder(Material.AIR).build());
            }
        }

        if (meta.getDisplayName().equalsIgnoreCase("§9§lBLUE"))
        {
            if (event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§9§lBLUE"))
            {
                wiresLeft--;
                event.getWhoClicked().setItemOnCursor(new ItemBuilder(Material.AIR).build());
            }
        }
        if (meta.getDisplayName().equalsIgnoreCase("§e§lYELLOW"))
        {
            if (event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§e§lYELLOW"))
            {
                wiresLeft--;
                event.getWhoClicked().setItemOnCursor(new ItemBuilder(Material.AIR).build());
            }
        }

        if (wiresLeft == 0)
        {
            event.getWhoClicked().closeInventory();
            AUPlayer player = getGame().getPlayer(event.getWhoClicked().getUniqueId());

            player.getTaskManager().addToCompletedSteps(getParent(player), this);
            player.getTaskManager().getActiveSteps().remove(step);
            player.getTaskManager().getActiveSteps().add(getParent(player).getSteps().get(1));
            event.getWhoClicked().sendMessage(ChatColor.GREEN + "Finished Wiring Task (" + getParent(player).getCompletedSteps().size() + "/" + getParent(player).getSteps().size() + ")");
            if (player.getTaskManager().stepsOfTaskAreComplete(getParent(player)))
            {
                player.getTaskManager().addToCompletedTasks(getParent(player));
            }
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
        RANDOM_COLORS.add(new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).setDisplayName("§e§lYELLOW").build());
        RANDOM_COLORS.add(new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayName("§c§lRED").build());
        RANDOM_COLORS.add(new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE).setDisplayName("§9§lBLUE").build());
        wiresLeft = 3;

    }
}
