package com.taahyt.amongus.tasksystem.filter;

import com.google.common.collect.Maps;
import com.taahyt.amongus.AmongUs;
import com.taahyt.amongus.game.AUGame;
import com.taahyt.amongus.game.player.AUPlayer;
import com.taahyt.amongus.tasksystem.TaskStep;
import com.taahyt.amongus.utils.Animations;
import com.taahyt.amongus.utils.ItemBuilder;
import org.bukkit.Bukkit;
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
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;

public class OxygenFilterTaskStep extends TaskStep<OxygenFilterTask>
{

    private OxygenFilterTaskStep step;

    private Inventory inventory;

    private Map<ItemStack, Integer> items = Maps.newHashMap();

    private BukkitTask task;

    public OxygenFilterTaskStep() {
        super("O2: Empty Leaves from the O2 Filter");
        step = this;
        this.inventory = Bukkit.createInventory(null, 27, "§aO2 Filter");
    }


    @Override
    public OxygenFilterTask getParent(AUPlayer player) {
        return player.getTaskManager().getOxygenFilterTask();
    }

    @Override
    public AUGame getGame() {
        return AmongUs.get().getGame();
    }

    public void openGUI(Player player) {

        for (int i = 0; i <= inventory.getSize() - 1; i++) {
            if (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR) {
                inventory.setItem(i, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName("§r").build());
            }
        }

        ItemStack leaf = new ItemBuilder(Material.ACACIA_LEAVES).setDisplayName("§aLeaves").build();
        items.put(leaf, 10);
        inventory.setItem(items.get(leaf), leaf);

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


        //if (gamePlayer.isImposter()) return;
        if (!sign.getLocation().equals(AmongUs.get().getGame().getScanner().getO2filter())) return;

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
        openGUI(player);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (event.getClickedInventory().hashCode() != this.inventory.hashCode()) {
            return;
        }
        if (event.getCurrentItem() == null) return;

        if (event.getCurrentItem().getType() == Material.GRAY_STAINED_GLASS_PANE) {
            event.setCancelled(true);
            return;
        }


        if (event.getSlot() == 10 || event.getSlot() == 13 || event.getSlot() == 16) {
            event.setCancelled(true);
        }
        ItemStack item = event.getCurrentItem();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        if (!meta.hasDisplayName()) return;

        event.setCancelled(true);

        if (item.getType() == Material.ACACIA_LEAVES)
        {
            Animations.animate(items, new ItemBuilder(Material.ACACIA_LEAVES).setDisplayName("§aLeaves").build(), 17, inventory, task, true);
        }

    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory().hashCode() != inventory.hashCode()) return;

        AUPlayer player = getGame().getPlayer(event.getPlayer().getUniqueId());
        if (player.getTaskManager().stepIsCompleted(getParent(player), this)) return;
        if (player.getTaskManager().taskIsCompleted(getParent(player))) return;
        event.getPlayer().setItemOnCursor(new ItemBuilder(Material.AIR).build());

        for (Integer value : items.values())
        {
            inventory.setItem(value, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }
        items.clear();

        if (task != null) task.cancel();

    }

}
