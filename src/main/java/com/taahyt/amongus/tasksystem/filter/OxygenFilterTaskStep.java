package com.taahyt.amongus.tasksystem.filter;

import com.google.common.collect.Maps;
import com.taahyt.amongus.AmongUs;
import com.taahyt.amongus.game.AUGame;
import com.taahyt.amongus.game.player.AUPlayer;
import com.taahyt.amongus.tasksystem.TaskStep;
import com.taahyt.amongus.utils.Animations;
import com.taahyt.amongus.utils.item.ItemBuilder;
import lombok.Getter;
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
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class OxygenFilterTaskStep extends TaskStep<OxygenFilterTask>
{

    private OxygenFilterTaskStep step;

    private Inventory inventory;

    private Map<ItemStack, Integer> items = Maps.newHashMap();

    private List<ItemStack> leaves = Arrays.asList(
            new ItemBuilder(Material.ACACIA_LEAVES).setDisplayName("§aLeaves 1").build(),
            new ItemBuilder(Material.ACACIA_LEAVES).setDisplayName("§aLeaves 2").build(),
            new ItemBuilder(Material.ACACIA_LEAVES).setDisplayName("§aLeaves 3").build(),
            new ItemBuilder(Material.ACACIA_LEAVES).setDisplayName("§aLeaves 4").build(),
            new ItemBuilder(Material.ACACIA_LEAVES).setDisplayName("§aLeaves 5").build());

    private BukkitTask task, task1, task2, task3, task4;
    private CompletableFuture<String> animOne, animTwo, animThree, animFour, animFive;

    private boolean leafOne, leafTwo, leafThree, leafFour, leafFive;

    public OxygenFilterTaskStep() {
        super("O2: Empty Leaves from the O2 Filter");
        step = this;
        this.inventory = Bukkit.createInventory(null, 45, "§aO2 Filter");
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

        items.put(leaves.get(0), 13);
        items.put(leaves.get(1), 17);
        items.put(leaves.get(2), 24);
        items.put(leaves.get(3), 30);
        items.put(leaves.get(4), 35);

        inventory.setItem(items.get(leaves.get(0)), leaves.get(0));
        inventory.setItem(items.get(leaves.get(1)), leaves.get(1));
        inventory.setItem(items.get(leaves.get(2)), leaves.get(2));
        inventory.setItem(items.get(leaves.get(3)), leaves.get(3));
        inventory.setItem(items.get(leaves.get(4)), leaves.get(4));

        player.openInventory(inventory);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!getGame().isStarted()) return;
        if (event.getClickedBlock() == null) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock().getType() != Material.OAK_SIGN && event.getClickedBlock().getType() != Material.OAK_WALL_SIGN) return;
        if (!(event.getClickedBlock().getState() instanceof Sign)) return;


        Player player = event.getPlayer();

        AUPlayer gamePlayer = getGame().getPlayer(player.getUniqueId());
        Sign sign = (Sign) event.getClickedBlock().getState();


        if (!sign.getLocation().equals(AmongUs.get().getGame().getScanner().getO2filter())) return;

        //if (gamePlayer.isImposter()) return;

        if (gamePlayer.getTaskManager().taskIsCompleted(getParent(gamePlayer))) {
            player.sendMessage("This task was already completed!");
            return;
        }
        if (gamePlayer.getTaskManager().stepIsCompleted(getParent(gamePlayer), step)) {
            player.sendMessage("This step was already completed!");
            return;
        }

        if (!gamePlayer.getTaskManager().getActiveSteps().contains(step))
         {
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
        ItemStack item = event.getCurrentItem();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        if (!meta.hasDisplayName()) return;

        event.setCancelled(true);

        if (item.getType() == Material.ACACIA_LEAVES)
        {
            if (item.getItemMeta().getDisplayName().equalsIgnoreCase(leaves.get(0).getItemMeta().getDisplayName()))
            {
                leafOne = true;
                animOne = Animations.animate(items, Animations.getFromDisplayName(items, leaves.get(0).getItemMeta().getDisplayName()), 9, inventory, task, false).whenComplete((task, ex) -> {
                    inventory.setItem(9, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
                    animOne = null;
                });

            }
            else if (item.getItemMeta().getDisplayName().equalsIgnoreCase(leaves.get(1).getItemMeta().getDisplayName()))
            {
                if (!leafOne) return;
                leafTwo = true;
                animTwo = Animations.animate(items, Animations.getFromDisplayName(items, leaves.get(1).getItemMeta().getDisplayName()), 9, inventory, task1, false).whenComplete((task, ex) -> {
                    inventory.setItem(9, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
                    animTwo = null;
                });
            }
            else if (item.getItemMeta().getDisplayName().equalsIgnoreCase(leaves.get(2).getItemMeta().getDisplayName()))
            {
                if (!leafTwo) return;
                leafThree = true;
                animThree = Animations.animate(items, Animations.getFromDisplayName(items, leaves.get(2).getItemMeta().getDisplayName()), 18, inventory, task2, false).whenComplete((task, ex) -> {
                    inventory.setItem(18, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
                    animThree = null;
                });
            }
            else if (item.getItemMeta().getDisplayName().equalsIgnoreCase(leaves.get(3).getItemMeta().getDisplayName()))
            {
                if (!leafThree) return;
                leafFour = true;
                animFour = Animations.animate(items, Animations.getFromDisplayName(items, leaves.get(3).getItemMeta().getDisplayName()), 27, inventory, task3, false).whenComplete((task, ex) -> {
                    inventory.setItem(27, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
                    animFour = null;
                });
            }
            else if (item.getItemMeta().getDisplayName().equalsIgnoreCase(leaves.get(4).getItemMeta().getDisplayName()))
            {
                if (!leafFour) return;
                leafFive = true;
                animFive = Animations.animate(items, Animations.getFromDisplayName(items, leaves.get(4).getItemMeta().getDisplayName()), 27, inventory, task4, false).whenComplete((task, ex) -> {
                    inventory.setItem(27, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
                    AUPlayer player = getGame().getPlayer(event.getWhoClicked().getUniqueId());
                    player.getTaskManager().addToCompletedSteps(getParent(player), step);
                    player.getTaskManager().addToCompletedTasks(getParent(player));
                    player.getTaskManager().getActiveSteps().remove(step);
                    player.getBukkitPlayer().closeInventory();
                    player.getBukkitPlayer().setItemOnCursor(new ItemStack(Material.AIR));
                    event.getWhoClicked().sendMessage(ChatColor.GREEN + "O2 Filter Cleaned (Filter Task - " + getParent(player).getCompletedSteps().size() + "/" + getParent(player).getSteps().size() + ")");
                    animFive = null;
                });
            }
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
        if (task1 != null) task1.cancel();
        if (task2 != null) task2.cancel();
        if (task3 != null) task3.cancel();
        if (task4 != null) task4.cancel();

        if (animOne != null) animOne.cancel(true);
        if (animTwo != null) animTwo.cancel(true);
        if (animThree != null) animThree.cancel(true);
        if (animFour != null) animFour.cancel(true);
        if (animFive != null) animFive.cancel(true);

    }

    public ItemStack getItemStack()
    {
        ItemStack item = new ItemStack(Material.ACACIA_LEAVES);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.GREEN + "Leaves");
        item.setItemMeta(meta);
        return item;
    }

    public class Leaf {
        @Getter
        private ItemStack item;
        @Getter
        private Integer slot;
        public Leaf(ItemStack item, Integer slot)
        {
            this.item = item;
            this.slot = slot;
        }
    }


}
