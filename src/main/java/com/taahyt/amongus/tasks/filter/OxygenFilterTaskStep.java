package com.taahyt.amongus.tasks.filter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.taahyt.amongus.AmongUs;
import com.taahyt.amongus.game.AUGame;
import com.taahyt.amongus.game.player.AUPlayer;
import com.taahyt.amongus.tasks.Task;
import com.taahyt.amongus.tasks.TaskStep;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class OxygenFilterTaskStep extends TaskStep {

    private OxygenFilterTaskStep step;

    private Map<AUPlayer, Inventory> inventories = Maps.newHashMap();

    private Map<AUPlayer, Map<ItemStack, Integer>> itemsMap = Maps.newHashMap();


    private List<Integer> neededSlots = Arrays.asList(13, 17, 24, 30, 35);

    private Map<AUPlayer, List<BukkitTask>> runnables = Maps.newHashMap();
    private Map<AUPlayer, List<CompletableFuture<String>>> futures = Maps.newHashMap();

    private Map<AUPlayer, List<Boolean>> leavesCompleted = Maps.newHashMap();

    private List<AUPlayer> activePlayers = new ArrayList<>(), completedPlayers = new ArrayList<>();


    public OxygenFilterTaskStep() {
        super("O2: Empty Leaves from the O2 Filter");
        step = this;
    }


    @Override
    public OxygenFilterTask getParent() {
        return AmongUs.get().getTaskManager().getOxygenFilterTask();
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

    public void openGUI(Player player) {

        inventories.put(getGame().getPlayer(player.getUniqueId()), Bukkit.createInventory(null, 45, "§aO2 Filter"));
        Inventory inventory = inventories.get(getGame().getPlayer(player.getUniqueId()));

        for (int i = 0; i <= inventory.getSize() - 1; i++) {
            if (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR) {
                inventory.setItem(i, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName("§r").build());
            }
        }

        itemsMap.put(getGame().getPlayer(player.getUniqueId()), Maps.newHashMap());

        List<BukkitTask> runnableList = Lists.newArrayList();
        List<CompletableFuture<String>> futuresList = Lists.newArrayList();

        for (int i = 0; i <= 4; i++) {
            runnableList.add(null);
        }
        runnables.put(getGame().getPlayer(player.getUniqueId()), runnableList);

        for (int i = 0; i <= 4; i++) {
            futuresList.add(null);
        }
        futures.put(getGame().getPlayer(player.getUniqueId()), futuresList);

        for (int i = 0; i <= neededSlots.size() - 1; i++) {
            int slot = neededSlots.get(i);
            int number = i + 1;
            setLeaf(new ItemBuilder(Material.ACACIA_LEAVES).setDisplayName("§aLeaves " + number).build(), slot, getGame().getPlayer(player.getUniqueId()));
        }

        getPlayerLeaves(getGame().getPlayer(player.getUniqueId())).forEach((key, value) -> {
            inventory.setItem(value, key);
        });


        List<Boolean> booleans = Lists.newArrayList();
        booleans.add(false);

        for (int i = 0; i <= 4; i++) {
            booleans.add(false);
        }

        leavesCompleted.put(getGame().getPlayer(player.getUniqueId()), booleans);

        player.openInventory(inventory);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() == null) return;
        if (!event.getHand().equals(EquipmentSlot.HAND)) return;
        if (!getGame().isStarted()) return;
        if (event.getClickedBlock() == null) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock().getType() != Material.OAK_SIGN && event.getClickedBlock().getType() != Material.OAK_WALL_SIGN)
            return;
        if (!(event.getClickedBlock().getState() instanceof Sign)) return;


        Player player = event.getPlayer();

        AUPlayer gamePlayer = getGame().getPlayer(player.getUniqueId());
        Sign sign = (Sign) event.getClickedBlock().getState();


        if (!sign.getLocation().equals(AmongUs.get().getGame().getScanner().getO2filter())) return;

        //if (gamePlayer.isImposter()) return;

        if (AmongUs.get().getTaskManager().taskIsCompleted(getParent(), gamePlayer)) {
            player.sendMessage("This task was already completed!");
            return;
        }
        if (AmongUs.get().getTaskManager().stepIsCompleted(this, gamePlayer)) {
            player.sendMessage("This step was already completed!");
            return;
        }

        if (!AmongUs.get().getTaskManager().isActiveStep(this, gamePlayer)) {
            player.sendMessage("Make sure you've done the other steps before proceeding to this task.");
            return;
        }

        openGUI(player);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;

        AUPlayer auPlayer = getGame().getPlayer(event.getWhoClicked().getUniqueId());
        if (!inventories.containsKey(auPlayer)) return;

        Inventory inventory = inventories.get(auPlayer);

        if (event.getClickedInventory().hashCode() != inventory.hashCode()) {
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

        if (item.getType() != Material.ACACIA_LEAVES) return;


        Bukkit.getLogger().info("check 1");
        if (item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Leaves 1")) {
            Bukkit.getLogger().info("check 2");
            setBoolean(0, true, auPlayer);
            setCompletableFuture(0, Animations.animate(getPlayerLeaves(auPlayer), Animations.getFromDisplayName(getPlayerLeaves(auPlayer), ChatColor.GREEN + "Leaves 1"), 9, inventory, getPlayerBukkitTasks(auPlayer), 0, false).whenComplete((task, ex) -> {
                inventory.setItem(9, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
                setCompletableFuture(0, null, auPlayer);
            }), auPlayer);

            runnables.put(auPlayer, getPlayerBukkitTasks(auPlayer));
            itemsMap.put(auPlayer, getPlayerLeaves(auPlayer));

        }
        else if (item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Leaves 2")) {
            if (!getLeafCurrentValue(0, auPlayer)) return;
            setBoolean(1, true, auPlayer);
            setCompletableFuture(1, Animations.animate(getPlayerLeaves(auPlayer), Animations.getFromDisplayName(getPlayerLeaves(auPlayer), ChatColor.GREEN + "Leaves 2"), 9, inventory, getPlayerBukkitTasks(auPlayer), 1, false).whenComplete((task, ex) -> {
                inventory.setItem(9, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
                setCompletableFuture(1, null, auPlayer);
            }), auPlayer);

            runnables.put(auPlayer, getPlayerBukkitTasks(auPlayer));
            itemsMap.put(auPlayer, getPlayerLeaves(auPlayer));
        }
        else if (item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Leaves 3")) {
            if (!getLeafCurrentValue(1, auPlayer)) return;
            setBoolean(2, true, auPlayer);
            setCompletableFuture(2, Animations.animate(getPlayerLeaves(auPlayer), Animations.getFromDisplayName(getPlayerLeaves(auPlayer), ChatColor.GREEN + "Leaves 3"), 18, inventory, getPlayerBukkitTasks(auPlayer), 2, false).whenComplete((task, ex) -> {
                inventory.setItem(18, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
                setCompletableFuture(2, null, auPlayer);
            }), auPlayer);

            runnables.put(auPlayer, getPlayerBukkitTasks(auPlayer));
            itemsMap.put(auPlayer, getPlayerLeaves(auPlayer));
        }
        else if (item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Leaves 4")) {
            if (!getLeafCurrentValue(2, auPlayer)) return;
            setBoolean(3, true, auPlayer);
            setCompletableFuture(3, Animations.animate(getPlayerLeaves(auPlayer), Animations.getFromDisplayName(getPlayerLeaves(auPlayer), ChatColor.GREEN + "Leaves 4"), 27, inventory, getPlayerBukkitTasks(auPlayer), 3, false).whenComplete((task, ex) -> {
                inventory.setItem(27, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
                setCompletableFuture(3, null, auPlayer);
            }), auPlayer);

            runnables.put(auPlayer, getPlayerBukkitTasks(auPlayer));
            itemsMap.put(auPlayer, getPlayerLeaves(auPlayer));
        }
        else if (item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GREEN + "Leaves 5")) {
            if (!getLeafCurrentValue(3, auPlayer)) return;
            setBoolean(4, true, auPlayer);
            setCompletableFuture(4, Animations.animate(getPlayerLeaves(auPlayer), Animations.getFromDisplayName(getPlayerLeaves(auPlayer), ChatColor.GREEN + "Leaves 5"), 27, inventory, getPlayerBukkitTasks(auPlayer), 4, false).whenComplete((task, ex) -> {
                inventory.setItem(27, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
                AmongUs.get().getTaskManager().addToCompletedSteps(step, auPlayer);
                AmongUs.get().getTaskManager().addToCompletedTasks(getParent(), auPlayer);
                activePlayers.remove(auPlayer);
                auPlayer.getBukkitPlayer().closeInventory();
                auPlayer.getBukkitPlayer().setItemOnCursor(new ItemStack(Material.AIR));
                event.getWhoClicked().sendMessage(ChatColor.GREEN + "O2 Filter Cleaned (Filter Task - " + getParent().getCompletedSteps(auPlayer).size() + "/" + getParent().getSteps().size() + ")");
                setCompletableFuture(4, null, auPlayer);
                inventories.remove(auPlayer);
            }), auPlayer);

            runnables.put(auPlayer, getPlayerBukkitTasks(auPlayer));
            itemsMap.put(auPlayer, getPlayerLeaves(auPlayer));
        }

    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {

        AUPlayer auPlayer = getGame().getPlayer(event.getPlayer().getUniqueId());

        if (!inventories.containsKey(auPlayer)) return;

        if (event.getInventory().hashCode() != inventories.get(auPlayer).hashCode()) return;

        if (AmongUs.get().getTaskManager().stepIsCompleted(this, auPlayer)) return;
        if (AmongUs.get().getTaskManager().taskIsCompleted(getParent(), auPlayer)) return;
        event.getPlayer().setItemOnCursor(new ItemBuilder(Material.AIR).build());

        for (Integer value : getPlayerLeaves(auPlayer).values()) {
            inventories.get(auPlayer).setItem(value, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }

        inventories.remove(auPlayer);

        itemsMap.remove(auPlayer);

        for (BukkitTask bukkitTask : getPlayerBukkitTasks(auPlayer)) {
            if (bukkitTask != null) bukkitTask.cancel();
        }
        runnables.remove(auPlayer);

        for (CompletableFuture<String> future : getPlayerFutures(auPlayer)) {
            if (future != null) future.cancel(true);
        }

        leavesCompleted.remove(auPlayer);
        inventories.remove(auPlayer);

    }

    private List<BukkitTask> getPlayerBukkitTasks(AUPlayer player) {
        return runnables.get(player);
    }

    private List<CompletableFuture<String>> getPlayerFutures(AUPlayer player) {
        return futures.get(player);
    }

    private Map<ItemStack, Integer> getPlayerLeaves(AUPlayer player) {
        return itemsMap.get(player);
    }

    private void setLeaf(ItemStack item, int slot, AUPlayer player) {
        getPlayerLeaves(player).put(item, slot);
        itemsMap.put(player, getPlayerLeaves(player));
    }

    private List<Boolean> getLeavesCurrentValues(AUPlayer player) {
        return leavesCompleted.get(player);
    }

    private Boolean getLeafCurrentValue(int index, AUPlayer player) {
        return getLeavesCurrentValues(player).get(index);
    }

    private void setBoolean(int index, boolean value, AUPlayer player) {
        getLeavesCurrentValues(player).set(index, value);
        leavesCompleted.put(player, getLeavesCurrentValues(player));
    }

    private void setCompletableFuture(int i, CompletableFuture<String> newFuture, AUPlayer player) {
        List<CompletableFuture<String>> futuresList = getPlayerFutures(player);
        futuresList.set(i, newFuture);
        futures.put(player, futuresList);
    }


}
