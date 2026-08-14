package com.tickprayerflick;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

public class TickPrayerFlickInfoOverlay extends Overlay
{
    @Inject private Client client;
    @Inject private TickPrayerFlickPlugin plugin;
    @Inject private TickPrayerFlickConfig config;

    @Inject public TickPrayerFlickInfoOverlay(TickPrayerFlickPlugin plugin)
    {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    @Override public Dimension render(Graphics2D g)
    {
        if(client.getGameState()!=GameState.LOGGED_IN) return null;
        String tickText = "Tick: " + plugin.getGameTick() + " | Next: " + (plugin.getNextPrayer()!=null?plugin.getNextPrayer().name():"-");
        g.setColor(Color.WHITE);
        g.drawString(tickText, 10, 20);
        return new Dimension(200, 20);
    }
}
