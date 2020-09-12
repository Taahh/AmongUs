package com.taahyt.amongus.customization.kits;

import com.taahyt.amongus.customization.Kit;
import com.taahyt.amongus.utils.item.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class YellowKit extends Kit
{

    private Color color = Color.YELLOW;

    @Override
    public String getName() {
        return ChatColor.YELLOW + "Yellow";
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.LEATHER_CHESTPLATE).setDisplayName(getName()).setColor(color).build();
    }

    @Override
    public ItemStack[] getArmorContents() {
        return new ItemStack[]{
                new ItemBuilder(Material.LEATHER_CHESTPLATE).setDisplayName(getName() + " Chestplate").setColor(color).build(),
                new ItemBuilder(Material.LEATHER_LEGGINGS).setDisplayName(getName() + " Leggings").setColor(color).build(),
                new ItemBuilder(Material.LEATHER_BOOTS).setDisplayName(getName() + " Boots").setColor(color).build(),
        };
    }
}
