package com.taahyt.amongus.meeting;

import com.google.common.collect.Maps;
import com.taahyt.amongus.AmongUs;
import com.taahyt.amongus.game.AUGame;
import com.taahyt.amongus.game.player.AUPlayer;
import com.taahyt.amongus.utils.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;

public class ConfirmationMenu implements Listener
{

    private AUGame game;

    private Map<Player, Inventory> inventories = Maps.newHashMap();

    private int blindnessDuration = 5;

    private int votingTime = 15;
    private int discussionTime = 15;

    private int emergencyCooldownTime = 15;

    private int votingTimeLeft = votingTime;
    private int discussionTimeLeft = discussionTime;

    public ConfirmationMenu(AUGame game)
    {
        this.game = game;
    }


    public void openInventory(Player player)
    {
        inventories.put(player, Bukkit.createInventory(null, 27, "Initiate Meeting?"));

        Inventory inventory = inventories.get(player);

        for (int i = 2; i <= 6; i++)
        {
            inventory.setItem(i, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setDisplayName("§r").build());
        }
        for (int i = 11; i <= 15; i++)
        {
            inventory.setItem(i, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setDisplayName("§r").build());
        }
        for (int i = 20; i <= 24; i++)
        {
            inventory.setItem(i, new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).setDisplayName("§r").build());
        }

        for (int i = 0; i <= inventory.getSize() - 1; i++)
        {
            if (inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR)
            {
                inventory.setItem(i, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName("§r").build());
            }
        }

        inventory.setItem(13, new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                .setDisplayName("§cInitiate Meeting")
                .setLore(
                        "§4Pressing this will initiate a new meeting.",
                        String.format("§4You have %s/%s meetings left.", game.getMeetingsLeft(), game.getTotalMeetings())
                )
                .build());
        player.openInventory(inventory);

    }

    @EventHandler
    public void onClick(InventoryClickEvent event)
    {
        Inventory inventory = event.getClickedInventory();
        Player player = (Player) event.getWhoClicked();
        AUPlayer auPlayer = game.getPlayer(player.getUniqueId());

        if (inventory == null) return;

        if (inventory.hashCode() != inventories.get(player).hashCode()) return;

        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        event.setCancelled(true);


        if (item.getType() != Material.RED_STAINED_GLASS_PANE) return;

        game.getPlayers().forEach(p -> {
            p.getBukkitPlayer().sendTitle(ChatColor.RED + "EMERGENCY MEETING", "Inititated by " + event.getWhoClicked().getName(), 30, 30, 10);
            p.getBukkitPlayer().teleport(player.getLocation());

            new BukkitRunnable() {
                @Override
                public void run() {
                    game.setInMeeting(true);

                    game.setWaitingOnVote(true);


                    AmongUs.get().getEmergencyMeetingHandler().openInventory(p.getBukkitPlayer());

                    Inventory emergencyInventory = AmongUs.get().getEmergencyMeetingHandler().getInventory(p.getBukkitPlayer());

                    BukkitTask discussionTask = new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (discussionTimeLeft > 0) {
                                discussionTimeLeft--;
                            }

                            ItemStack discussionItem = new ItemBuilder(Material.SUNFLOWER).setDisplayName("§rDiscussion Time: " + discussionTimeLeft).build();
                            emergencyInventory.setItem(26, discussionItem);
                        }
                    }.runTaskTimer(AmongUs.get(), 0, 20);

                    BukkitTask endEmergencyTask = new BukkitRunnable() {
                        @Override
                        public void run() {
                            discussionTask.cancel();
                            game.setWaitingOnVote(false);
                            game.setVoting(true);

                            BukkitTask votingTask = new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (votingTimeLeft > 0) {
                                        votingTimeLeft--;
                                    }

                                    ItemStack votingItem = new ItemBuilder(Material.SUNFLOWER).setDisplayName("§rVoting Time: " + votingTimeLeft).build();
                                    emergencyInventory.setItem(35, votingItem);

                                    if (auPlayer.isVoted()) {
                                        ItemStack voted = new ItemBuilder(Material.REDSTONE_TORCH).setDisplayName("§rVoted").build();
                                        emergencyInventory.setItem(34, voted);
                                    }
                                }
                            }.runTaskTimer(AmongUs.get(), 0, 20);

                            BukkitTask afterVotingTask = new BukkitRunnable() {
                                @Override
                                public void run() {
                                    votingTask.cancel();
                                    game.setVoting(false);

                                    game.getPlayers().forEach(gp -> {
                                        if (gp.getBukkitPlayer().getOpenInventory().getTopInventory().hashCode() == emergencyInventory.hashCode()) {
                                            gp.getBukkitPlayer().closeInventory();
                                        }
                                        game.setEmergencyCooldown(true);
                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                game.setEmergencyCooldown(false);
                                            }
                                        }.runTaskLater(AmongUs.get(), emergencyCooldownTime * 20);

                                        gp.getBukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, blindnessDuration * 20, Integer.MAX_VALUE, false, false));
                                        if (game.getVotes().isEmpty())
                                        {
                                            gp.getBukkitPlayer().sendTitle("Nobody voted", "", 30, 30, 10);
                                        } else if (!game.getVotes().isEmpty() && game.voteTie())
                                        {
                                            gp.getBukkitPlayer().sendTitle("Nobody was", "ejected (Tie).", 30, 30, 10);
                                        } else if (!game.getVotes().isEmpty() && !game.voteTie())
                                        {
                                            if (auPlayer.hashCode() == game.getVoted().hashCode())
                                            {
                                                Bukkit.broadcastMessage(ChatColor.BOLD + game.getVoted().getBukkitPlayer().getName() + " - " + game.getVotes().get(game.getVoted())); //ANNOUNCE THE MOST VOTED
                                            } else {
                                                Bukkit.broadcastMessage(game.getVotes().containsKey(auPlayer) ? auPlayer.getBukkitPlayer().getName() + " - " + game.getVotes().get(auPlayer) : auPlayer.getBukkitPlayer().getName() + " - 0"); //ANNOUNCE EVERONE ELSE
                                            }
                                            new BukkitRunnable() {
                                                @Override
                                                public void run() {
                                                    game.kill(game.getVoted()); // 2 SECONDS LATER, KILL THE MOST VOTED
                                                    game.getPlayers().stream().map(AUPlayer::getBukkitPlayer).forEach(player -> player.sendTitle(game.getVoted().getBukkitPlayer().getName() + " was", (game.getVoted().isImposter() ? ChatColor.RED + "AN " : ChatColor.RED + "NOT AN ") + ChatColor.WHITE +  "IMPOSTER", 30, 30, 10)); // ANNOUNCE WHETHER THEY WERE AN IMPOSTER OR NOT
                                                    game.getVotes().clear(); //CLEAR THE VOTES
                                                }
                                            }.runTaskLater(AmongUs.get(), 40);
                                        }

                                    });
                                    votingTimeLeft = votingTime;
                                    discussionTimeLeft = discussionTime;
                                    game.setInMeeting(false);
                                }
                            }.runTaskLater(AmongUs.get(), votingTime * 20);

                        }
                    }.runTaskLater(AmongUs.get(), discussionTime * 20);
                }
            }.runTaskLater(AmongUs.get(), 40);



        });

    }

}
