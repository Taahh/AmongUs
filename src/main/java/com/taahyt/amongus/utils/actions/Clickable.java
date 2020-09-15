package com.taahyt.amongus.utils.actions;

import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

public abstract class Clickable implements ClickAction
{
    @Override
    public void onClick(InventoryClickEvent event) {

    }

    @EventHandler
    public void onClickInv(InventoryClickEvent event)
    {
        onClick(event);
    }
}
