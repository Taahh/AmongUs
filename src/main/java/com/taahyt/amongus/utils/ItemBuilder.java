package com.taahyt.amongus.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemBuilder
{
    private ItemStack itemStack;
    private ItemMeta meta;

    public ItemBuilder(Material material)
    {
        this.itemStack = new ItemStack(material);
        this.meta = itemStack.getItemMeta();
    }

    public ItemBuilder setAmount(int amount)
    {
        this.itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder setDisplayName(String name)
    {
        this.meta.setDisplayName(name);
        return this;
    }

    public ItemStack build()
    {
        this.itemStack.setItemMeta(meta);
        return itemStack;
    }

}
