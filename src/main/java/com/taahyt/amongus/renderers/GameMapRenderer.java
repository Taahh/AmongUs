package com.taahyt.amongus.renderers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class GameMapRenderer extends MapRenderer
{
    
    public GameMapRenderer(MapView view, Player player)
    {
        view.setWorld(Bukkit.getWorld("world"));
        view.setScale(MapView.Scale.CLOSE);
        view.setCenterX(player.getLocation().getBlockX());
        view.setCenterZ(player.getLocation().getBlockZ());
    }
    
    @Override
    public void render(MapView mapView, MapCanvas mapCanvas, Player player)
    {
        mapCanvas.getCursors().addCursor(player.getLocation().getBlockX(), player.getLocation().getBlockY(), (byte) 12);
    }
}
