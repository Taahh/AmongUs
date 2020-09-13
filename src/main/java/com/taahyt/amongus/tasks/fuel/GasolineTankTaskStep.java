package com.taahyt.amongus.tasks.fuel;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GasolineTankTaskStep extends TaskStep {

    private GasolineTankTaskStep step;

    private Map<AUPlayer, Inventory> inventories = Maps.newHashMap();
    private Map<AUPlayer, Double> minimumValues = Maps.newHashMap();

    private Integer[] fuelSlots = new Integer[]{
            5, 6, 14, 16, 23, 24, 25, 26, 32, 33, 34, 35, 41, 42, 43, 44, 50, 51, 52, 53
    };

    private double increment = 16.5;
    private int maxValue = 99;

    private List<AUPlayer> activePlayers = new ArrayList<>(), completedPlayers = new ArrayList<>();


    public GasolineTankTaskStep() {
        super("Storage: Refill the Gasoline Tank");
        step = this;
    }

    @Override
    public FuelTask getParent() {
        return AmongUs.get().getTaskManager().getFuelTask();
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

        inventories.put(auPlayer, Bukkit.createInventory(null, 54, "§aGasoline Fuel Task"));

        Inventory inventory = inventories.get(auPlayer);

        for (int i = 0; i <= inventory.getSize() - 1; i++)
        {
            if (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR)
            {
                inventory.setItem(i, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName("§r").build());
            }
        }

        for (Integer slot : fuelSlots)
        {
            inventory.setItem(slot, new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).setDisplayName("§rEmpty Tank").build());
        }

        inventory.setItem(29, new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayName("§6Click to Fill").build());

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



        if (!sign.getLocation().equals(AmongUs.get().getGame().getScanner().getGasTank())) return;

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

        Inventory inventory = inventories.get(getGame().getPlayer(event.getWhoClicked().getUniqueId()));

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

        event.setCancelled(true);
        if (event.getCurrentItem().getType() == Material.RED_STAINED_GLASS_PANE)
        {
            AUPlayer player = getGame().getPlayer(event.getWhoClicked().getUniqueId());
            minimumValues.put(player, (minimumValues.containsKey(player) ? minimumValues.get(player) + increment : increment));
            double minValue = minimumValues.get(player);
            if (minValue == 16.5)
            {
                setSlots(inventory, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setDisplayName("§aFilled Tank").build(), 50, 51, 52, 53);
            }
            else if (minValue == 33)
            {
                setSlots(inventory, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setDisplayName("§aFilled Tank").build(), 41, 42, 43, 44);
            }
            else if (minValue == 49.5)
            {
                setSlots(inventory, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setDisplayName("§aFilled Tank").build(), 32, 33, 34, 35);
            }
            else if (minValue == 66)
            {
                setSlots(inventory, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setDisplayName("§aFilled Tank").build(), 23, 24, 25, 26);
            }
            else if (minValue == 82.5)
            {
                setSlots(inventory, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setDisplayName("§aFilled Tank").build(), 14, 16);
            }
            else if (minValue == maxValue)
            {
                setSlots(inventory, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setDisplayName("§aFilled Tank").build(), 5, 6);
                AmongUs.get().getTaskManager().addToCompletedSteps(step, player);
                AmongUs.get().getTaskManager().addToCompletedTasks(getParent(), player);
                activePlayers.remove(player);
                player.getBukkitPlayer().closeInventory();
                player.getBukkitPlayer().setItemOnCursor(new ItemStack(Material.AIR));
                event.getWhoClicked().sendMessage(ChatColor.GREEN + "Gas Tank Refilled (Fuel Task - " + getParent().getCompletedSteps(player).size() + "/" + getParent().getSteps().size() + ")");
                inventories.remove(player);
            }
        }

    }

    @EventHandler
    public void onClose(InventoryCloseEvent event)
    {
        if (!inventories.containsKey(getGame().getPlayer(event.getPlayer().getUniqueId()))) return;

        Inventory inventory = inventories.get(getGame().getPlayer(event.getPlayer().getUniqueId()));
        if (event.getInventory().hashCode() != inventory.hashCode()) return;

        AUPlayer player = getGame().getPlayer(event.getPlayer().getUniqueId());
        if (AmongUs.get().getTaskManager().stepIsCompleted(step, player)) return;
        if (AmongUs.get().getTaskManager().taskIsCompleted(getParent(), player)) return;
        event.getPlayer().setItemOnCursor(new ItemBuilder(Material.AIR).build());
        minimumValues.remove(player);
        inventories.remove(player);
        inventories.remove(player);
    }

    private void setSlots(Inventory inv, ItemStack item, Integer... slots)
    {
        for (Integer i : slots)
        {
            inv.setItem(i, item);
        }
    }


}
