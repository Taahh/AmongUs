package com.taahyt.amongus.tasks.wiring;

import com.google.common.collect.Lists;
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
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ElectricalWiringTaskStep extends TaskStep
{
    private ElectricalWiringTaskStep step;

    private Map<AUPlayer, Inventory> inventories = Maps.newHashMap();
    private Map<AUPlayer, Integer> wiresLeftPerPlayer = Maps.newHashMap();
    private Map<AUPlayer, List<ItemStack>> RANDOM_COLORS = Maps.newHashMap();



    private List<AUPlayer> activePlayers = new ArrayList<>(), completedPlayers = new ArrayList<>();

    private List<UUID> clickCooldown = Lists.newArrayList();

    public ElectricalWiringTaskStep()
    {
        super("Electrical: Fix the Wiring");
        step = this;
    }


    @Override
    public WiringTask getParent() {
        return AmongUs.get().getTaskManager().getWiringTask();
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

    public ItemStack getRandomItem(AUPlayer player)
    {
        List<ItemStack> randomColors = RANDOM_COLORS.get(player);
        ItemStack item = randomColors.get(ThreadLocalRandom.current().nextInt(randomColors.size()));
        randomColors.remove(item);
        RANDOM_COLORS.put(player, randomColors);
        return item;
    }

    public void openGUI(Player player)
    {
        AUPlayer auPlayer = getGame().getPlayer(player.getUniqueId());
        inventories.put(auPlayer, Bukkit.createInventory(null, 54, "§aWiring Task PT 1"));
        Inventory inventory = inventories.get(auPlayer);

        for (int i = 0; i <= inventory.getSize() - 1; i++)
        {
            if (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR)
            {
                inventory.setItem(i, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName("§r").build());
            }
        }

        List<ItemStack> randomColors = Lists.newArrayList();
        randomColors.add(new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).setDisplayName("§e§lYELLOW").build());
        randomColors.add(new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayName("§c§lRED").build());
        randomColors.add(new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE).setDisplayName("§9§lBLUE").build());
        RANDOM_COLORS.put(auPlayer, randomColors);


        inventory.setItem(18, new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayName("§c§lRED").build());
        inventory.setItem(27, new ItemBuilder(Material.BLUE_STAINED_GLASS_PANE).setDisplayName("§9§lBLUE").build());
        inventory.setItem(36, new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).setDisplayName("§e§lYELLOW").build());


        inventory.setItem(26, getRandomItem(auPlayer));
        inventory.setItem(35, getRandomItem(auPlayer));
        inventory.setItem(44, getRandomItem(auPlayer));

        player.openInventory(inventory);

        wiresLeftPerPlayer.put(auPlayer, 3);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
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
        if (!sign.getLocation().equals(AmongUs.get().getGame().getScanner().getWiringTask1())) return;

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


        if (event.getSlot() == 26 || event.getSlot() == 35 || event.getSlot() == 44)
        {
            event.setCancelled(true);
        }

        if (event.getCursor() == null) return;

        if (event.getClick() != ClickType.LEFT)
        {
            event.setCancelled(true);
            return;
        }

        ItemStack item = event.getCursor();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        if (!meta.hasDisplayName()) return;

        event.setCancelled(true);

        if (clickCooldown.contains(auPlayer.getUuid()))
        {
            return;
        }

        if (meta.getDisplayName().equalsIgnoreCase("§c§lRED"))
        {
            if (event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§c§lRED"))
            {
                wiresLeftPerPlayer.put(auPlayer, wiresLeftPerPlayer.get(auPlayer) - 1);
                event.getWhoClicked().setItemOnCursor(new ItemBuilder(Material.AIR).build());
                setCooldown(auPlayer.getUuid());
            }
        }

        if (meta.getDisplayName().equalsIgnoreCase("§9§lBLUE"))
        {
            if (event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§9§lBLUE"))
            {
                wiresLeftPerPlayer.put(auPlayer, wiresLeftPerPlayer.get(auPlayer) - 1);
                event.getWhoClicked().setItemOnCursor(new ItemBuilder(Material.AIR).build());
                setCooldown(auPlayer.getUuid());
            }
        }
        if (meta.getDisplayName().equalsIgnoreCase("§e§lYELLOW"))
        {
            if (event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§e§lYELLOW"))
            {
                wiresLeftPerPlayer.put(auPlayer, wiresLeftPerPlayer.get(auPlayer) - 1);
                event.getWhoClicked().setItemOnCursor(new ItemBuilder(Material.AIR).build());
                setCooldown(auPlayer.getUuid());
            }
        }

        if (wiresLeftPerPlayer.get(auPlayer) == 0)
        {
            event.getWhoClicked().closeInventory();

            AmongUs.get().getTaskManager().addToCompletedSteps(step, auPlayer);
            activePlayers.remove(auPlayer);
            getParent().getSteps().get(1).activePlayers().add(auPlayer);
            event.getWhoClicked().sendMessage(ChatColor.GREEN + "Fixed Wiring Part 1 - (Wiring Task - " + getParent().getCompletedSteps(auPlayer).size() + "/" + getParent().getSteps().size() + ")");
            inventories.remove(auPlayer);
        }


    }

    @EventHandler
    public void onClose(InventoryCloseEvent event)
    {

        AUPlayer auPlayer = getGame().getPlayer(event.getPlayer().getUniqueId());
        if (!inventories.containsKey(auPlayer)) return;

        if (event.getInventory().hashCode() != inventories.get(auPlayer).hashCode()) return;

        AUPlayer player = getGame().getPlayer(event.getPlayer().getUniqueId());
        if (AmongUs.get().getTaskManager().stepIsCompleted(step, player)) return;
        if (AmongUs.get().getTaskManager().taskIsCompleted(getParent(), player)) return;
        event.getPlayer().setItemOnCursor(new ItemBuilder(Material.AIR).build());
        RANDOM_COLORS.remove(player);
        inventories.remove(player);
        wiresLeftPerPlayer.remove(player);

    }

    public void setCooldown(UUID uuid)
    {
        clickCooldown.add(uuid);
        new BukkitRunnable() {
            @Override
            public void run() {
                clickCooldown.remove(uuid);
            }
        }.runTaskLater(AmongUs.get(), 20);
    }
}
