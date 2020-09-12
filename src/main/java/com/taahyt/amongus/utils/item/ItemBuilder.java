package com.taahyt.amongus.utils.item;

import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Arrays;

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

    public ItemBuilder setColor(Color color)
    {
        ((LeatherArmorMeta)meta).setColor(color);
        return this;
    }

    public ItemBuilder setLore(String... strings)
    {
        meta.setLore(Arrays.asList(strings));
        return this;
    }

    public ItemStack build()
    {
        this.itemStack.setItemMeta(meta);
        return itemStack;
    }

    @Override
    public int hashCode() {
        return Integer.parseInt(RandomStringUtils.randomNumeric(10));
    }
}
