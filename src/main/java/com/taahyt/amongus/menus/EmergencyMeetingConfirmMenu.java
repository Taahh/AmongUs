package com.taahyt.amongus.menus;

import com.taahyt.amongus.AmongUs;
import com.taahyt.amongus.game.AUGame;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;

public class EmergencyMeetingConfirmMenu implements Listener
{

    @Getter
    private Inventory inventory;

    private AUGame game;
    public EmergencyMeetingConfirmMenu()
    {
        this.inventory = Bukkit.createInventory(null, 27, ChatColor.RED + "EMERGENCY MEETING");
        this.game = AmongUs.get().getGame();
    }

    public void openInventory(Player player)
    {
        ItemStack confirm = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta confirmMeta = confirm.getItemMeta();
        assert confirmMeta != null;
        confirmMeta.setDisplayName(ChatColor.GREEN + "Confirm!");
        confirmMeta.setLore(Arrays.asList(ChatColor.DARK_GREEN +  "Clicking this will initiate", ChatColor.DARK_GREEN + "an emergency meeting!"));
        confirm.setItemMeta(confirmMeta);

        ItemStack cancel = new ItemStack(Material.BARRIER);
        ItemMeta cancelMeta = cancel.getItemMeta();
        assert cancelMeta != null;
        cancelMeta.setDisplayName(ChatColor.RED + "Cancel!");
        cancelMeta.setLore(Arrays.asList(ChatColor.DARK_RED +  "Clicking this will close", ChatColor.DARK_RED + "this menu!"));
        cancel.setItemMeta(cancelMeta);

        getInventory().setItem(11, confirm);
        getInventory().setItem(15, cancel);

        for (int i = 0; i<= getInventory().getSize() - 1; i++)
        {
            if (getInventory().getItem(i) == null || getInventory().getItem(i).getType() == Material.AIR)
            {
                getInventory().setItem(i, new ItemStack(Material.PURPLE_STAINED_GLASS_PANE));
            }
        }

        player.openInventory(getInventory());
    }

    @EventHandler
    public void onClick(InventoryClickEvent event)
    {
        if (event.getClickedInventory() == null) return;
        if (event.getClickedInventory().hashCode() != this.inventory.hashCode()) return;
        if (event.getCurrentItem() == null) return;
         event.setCancelled(true);
         if (event.getCurrentItem().getType() == Material.EMERALD_BLOCK)
         {
             event.getWhoClicked().closeInventory();

             // ANNOUNCE THE MEETING //

             game.getPlayers().forEach(gamePlayer -> gamePlayer.getBukkitPlayer().sendTitle(ChatColor.RED + "EMERGENCY MEETING", "Inititated by " + event.getWhoClicked().getName(), 40, 40, 40));
             new BukkitRunnable() {
                 @Override
                 public void run() {
                     game.setWaitingOnVote(true); //SET THE GAME TO WAITING ON VOTES
                     game.getPlayers().forEach(gamePlayer -> AmongUs.get().getEmergencyMeetingMenu().openInventory(gamePlayer.getBukkitPlayer())); //OPEN THE VOTING MENU

                     BukkitTask task = new BukkitRunnable() {
                         @Override
                         public void run() {
                             if (AmongUs.get().getEmergencyMeetingMenu().getCountingTime() > 0)
                             {
                                 AmongUs.get().getEmergencyMeetingMenu().setCountingTime(AmongUs.get().getEmergencyMeetingMenu().getCountingTime() - 1); //START COUNTING DOWN ON THE WAIT TIMER
                             }


                             ItemMeta counterMeta = AmongUs.get().getEmergencyMeetingMenu().getCounterItem().getItemMeta();
                             assert counterMeta != null;
                             counterMeta.setDisplayName(ChatColor.RESET + "Waiting Time: " + AmongUs.get().getEmergencyMeetingMenu().getCountingTime());
                             AmongUs.get().getEmergencyMeetingMenu().getCounterItem().setItemMeta(counterMeta);
                             AmongUs.get().getEmergencyMeetingMenu().getInventory().setItem(26, AmongUs.get().getEmergencyMeetingMenu().getCounterItem());

                         }
                     }.runTaskTimer(AmongUs.get(), 0, 20); // voting time task

                     // AFTER WAIT TIMER IS OVER //

                     new BukkitRunnable() {
                         @Override
                         public void run() {
                             task.cancel(); //CANCEL THE TIMER TASK ABOVE THAT DECREASED THE WAITING TIME
                             game.setWaitingOnVote(false); // STOP WAITING ON THE VOTING
                             game.setVoting(true); // START VOTING
                             BukkitTask task = new BukkitRunnable() { //run voting task
                                 @Override
                                 public void run() {
                                     if (AmongUs.get().getEmergencyMeetingMenu().getVotingTime() > 0)
                                     {
                                         AmongUs.get().getEmergencyMeetingMenu().setVotingTime(AmongUs.get().getEmergencyMeetingMenu().getVotingTime() - 1); //DECREASE THE TIMER FOR VOTING
                                     }

                                     ItemMeta counterMeta = AmongUs.get().getEmergencyMeetingMenu().getCounterItem().getItemMeta();
                                     assert counterMeta != null;
                                     if (!counterMeta.getDisplayName().equalsIgnoreCase("VOTED!"))
                                     {
                                         counterMeta.setDisplayName(ChatColor.RESET + "Voting Time: " + AmongUs.get().getEmergencyMeetingMenu().getVotingTime());
                                     }
                                     AmongUs.get().getEmergencyMeetingMenu().getCounterItem().setItemMeta(counterMeta);
                                     AmongUs.get().getEmergencyMeetingMenu().getInventory().setItem(26, AmongUs.get().getEmergencyMeetingMenu().getCounterItem());

                                 }
                             }.runTaskTimer(AmongUs.get(), 0, 20);

                             // AFTER VOTING TASK //

                             new BukkitRunnable() {
                                 @Override
                                 public void run() {
                                     task.cancel(); // CANCEL VOTING TIMER TASK THAT DECREASED THE VOTING TIME
                                     game.setVoting(false); // SET GAME STATE VOTING TO FALSE
                                     game.getPlayers().forEach(auPlayer -> auPlayer.getBukkitPlayer().closeInventory()); //CLOSE EVERYONE'S INVENTORY
                                     if (game.getVotes().isEmpty()) //check if nobody voted
                                     {
                                         Bukkit.broadcastMessage("Nobody voted.");
                                     }
                                     else if (game.voteTie() && !game.getVotes().isEmpty())
                                     {
                                         Bukkit.broadcastMessage("IT WAS A TIE!"); // SORTED BY VOTES, IF THE TOP 2 HAD THE SAME VOTES, SAY IT WAS A TIE
                                     } else if (!game.voteTie() && !game.getVotes().isEmpty()){ //OR IF IT WASN'T A TIE, ANNOUNCE THE PEOPLE AND THEIR VOTES
                                         game.getAlivePlayers().forEach(auPlayer -> {
                                             if (auPlayer.hashCode() == game.getVoted().hashCode())
                                             {
                                                 Bukkit.broadcastMessage(ChatColor.BOLD + game.getVoted().getBukkitPlayer().getName() + " - " + game.getVotes().get(game.getVoted())); //ANNOUNCE THE MOST VOTED
                                             } else {
                                                 Bukkit.broadcastMessage(game.getVotes().containsKey(auPlayer) ? auPlayer.getBukkitPlayer().getName() + " - " + game.getVotes().get(auPlayer) : auPlayer.getBukkitPlayer().getName() + " - 0"); //ANNOUNCE EVERONE ELSE
                                             }
                                         });
                                         new BukkitRunnable() {
                                             @Override
                                             public void run() {
                                                 game.kill(game.getVoted()); // 2 SECONDS LATER, KILL THE MOST VOTED
                                                 Bukkit.broadcastMessage(game.getVoted().getBukkitPlayer().getName() + (game.getVoted().isImposter() ? " was the" : " was not the") + " Imposter!"); // ANNOUNCE WHETHER THEY WERE AN IMPOSTER OR NOT
                                                 game.getVotes().clear(); //CLEAR THE VOTES
                                             }
                                         }.runTaskLater(AmongUs.get(), 40);
                                     }

                                     // RESET THE TIMERS //
                                     AmongUs.get().getEmergencyMeetingMenu().setVotingTime(20);
                                     AmongUs.get().getEmergencyMeetingMenu().setCountingTime(15);
                                     game.setEmergencyCooldown(true); // TURN ON THE COOLDOWN FOR THE EMERGENCY MEETING BUTTON
                                     new BukkitRunnable() {
                                         @Override
                                         public void run() {
                                             game.setEmergencyCooldown(false); // 15 SECONDS LATER, TURN THE COOLDOWN OFF.
                                         }
                                     }.runTaskLater(AmongUs.get(), 15 * 20);
                                 }
                             }.runTaskLater(AmongUs.get(), 20 * 20); //voting original time * 20 (one second in ticks)

                         }
                     }.runTaskLater(AmongUs.get(),15 * 20); //counting original time * 20 (one second in ticks)

                 }
             }.runTaskLater(AmongUs.get(), 40);
             return;
         }
    }
}
