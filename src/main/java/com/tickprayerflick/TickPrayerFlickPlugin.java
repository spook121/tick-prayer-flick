package com.tickprayerflick;

import com.google.inject.Provides;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Prayer;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(name = "Tick Prayer Flick", description = "1:1 Report Button - NEXT highlight for 1-tick no damage", tags = {"prayer","pvm","tick","inferno"})
public class TickPrayerFlickPlugin extends Plugin
{
    @Inject private Client client;
    @Inject private OverlayManager overlayManager;
    @Inject private TickPrayerFlickOverlay overlay;
    @Inject private TickPrayerFlickInfoOverlay infoOverlay;
    @Inject private TickPrayerFlickConfig config;

    private int loginTick = 0;

    @Provides TickPrayerFlickConfig provideConfig(ConfigManager configManager){ return configManager.getConfig(TickPrayerFlickConfig.class); }
    @Override protected void startUp(){ overlayManager.add(overlay); overlayManager.add(infoOverlay); 
        if(client.getGameState() == GameState.LOGGED_IN) loginTick = client.getTickCount();
    }
    @Override protected void shutDown(){ overlayManager.remove(overlay); overlayManager.remove(infoOverlay); }

    @Subscribe public void onGameStateChanged(GameStateChanged e){
        if(e.getGameState() == GameState.LOGGED_IN){
            loginTick = client.getTickCount();
        }
        if(e.getGameState() == GameState.LOGIN_SCREEN || e.getGameState() == GameState.HOPPING || e.getGameState() == GameState.CONNECTION_LOST){
            loginTick = 0;
        }
    }

    // Client tick since login - matches AutoZuk timer which resets on login
    public int getGameTick(){ 
        if(client.getGameState() != GameState.LOGGED_IN) return 0;
        int t = client.getTickCount() - loginTick;
        return t < 0 ? 0 : t;
    }
    
    // FIXED: NO highlightNext here. Overlay decides CURRENT vs NEXT
    public int getEffectiveTick(){
        int base = getGameTick();
        return base + config.tickOffset();
    }
    
    public int getClientTickCount(){ return client.getTickCount(); }
    public Client getClient(){ return client; }
    public int getLoginTick(){ return loginTick; }

    public Prayer getCurrentPrayer(){
        if(client.getGameState() != GameState.LOGGED_IN) return null;
        int tick = getEffectiveTick();
        if(tick < 0) tick = 0;
        return getPrayerAtTick(tick);
    }
    
    public Prayer getNextPrayer(){
        if(client.getGameState() != GameState.LOGGED_IN) return null;
        int tick = getEffectiveTick() + 1;
        if(tick < 0) tick = 0;
        return getPrayerAtTick(tick);
    }

    private Prayer getPrayerAtTick(int tick){
        Prayer a = config.prayerOne(); 
        Prayer b = config.prayerTwo(); 
        Prayer c = config.prayerThree();
        Prayer d = config.prayerFour();
        List<Prayer> order = new ArrayList<>();
        switch(config.sequence()){
            case A_B: order.add(a); order.add(b); break;
            case A_C: order.add(a); order.add(c); break;
            case B_C: order.add(b); order.add(c); break;
            case A_B_C: order.add(a); order.add(b); order.add(c); break;
            case A_C_B: order.add(a); order.add(c); order.add(b); break;
            case B_A_C: order.add(b); order.add(a); order.add(c); break;
            case A_B_C_B: order.add(a); order.add(b); order.add(c); order.add(b); break;
            case A_B_A_C: order.add(a); order.add(b); order.add(a); order.add(c); break;
            case A_B_C_A: order.add(a); order.add(b); order.add(c); order.add(a); break;
            case A_B_C_D: order.add(a); order.add(b); order.add(c); order.add(d); break;
            case A_B_D_C: order.add(a); order.add(b); order.add(d); order.add(c); break;
            case A_C_B_D: order.add(a); order.add(c); order.add(b); order.add(d); break;
            case A_C_D_B: order.add(a); order.add(c); order.add(d); order.add(b); break;
            case A_D_B_C: order.add(a); order.add(d); order.add(b); order.add(c); break;
            case D_C_B_A: order.add(d); order.add(c); order.add(b); order.add(a); break;
            case A_B_C_D_A: order.add(a); order.add(b); order.add(c); order.add(d); order.add(a); break;
            case A_B_C_D_B: order.add(a); order.add(b); order.add(c); order.add(d); order.add(b); break;
            default: order.add(a); order.add(b); order.add(c); break;
        }
        if(order.isEmpty()) return a;
        return order.get(Math.abs(tick % order.size()));
    }
}
