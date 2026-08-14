package com.tickprayerflick;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Prayer;
import net.runelite.api.SpriteID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

public class TickPrayerFlickOverlay extends Overlay
{
    @Inject private Client client;
    @Inject private TickPrayerFlickPlugin plugin;
    @Inject private TickPrayerFlickConfig config;

    @Inject public TickPrayerFlickOverlay(TickPrayerFlickPlugin plugin)
    {
        super(plugin);
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGHEST);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    @Override public Dimension render(Graphics2D g)
    {
        if(client.getGameState()!=GameState.LOGGED_IN) return null;
        Prayer p = config.highlightNext() ? plugin.getNextPrayer() : plugin.getCurrentPrayer();
        if(p==null) return null;
        Widget tab = client.getWidget(541, 0);
        if(tab==null || tab.isHidden() || tab.getBounds()==null) return null;
        if(tab.getBounds().width<100) return null;
        Widget w = findUniversal(p, tab);
        if(w==null || w.getBounds()==null) return null;

        Color c = config.highlightColor();
        if(c==null) c = new Color(0x30,0x00,0xFF);
        int thickness = config.highlightNext() ? config.borderWidth()+3 : config.borderWidth()+2;
        g.setColor(c);
        g.setStroke(new java.awt.BasicStroke(thickness));
        g.draw(w.getBounds());
        g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 90));
        g.fill(w.getBounds());
        g.setColor(Color.WHITE);
        g.setStroke(new java.awt.BasicStroke(1));
        g.draw(w.getBounds());
        return null;
    }

    private Widget findUniversal(Prayer prayer, Widget tab){
        int id = getDirectId(prayer);
        if(id!=-1){
            Widget w = client.getWidget(541, id);
            if(w!=null && !w.isHidden() && w.getBounds()!=null && w.getBounds().width>10) return w;
        }
        int sprite = getSprite(prayer);
        int disabled = getDisabledSprite(prayer);
        if(sprite!=-1){
            for(int i=0;i<100;i++){
                Widget w = client.getWidget(541, i);
                if(w==null) continue;
                Widget found = findBySpriteRecursive(w, sprite, disabled);
                if(found!=null && !found.isHidden() && found.getBounds()!=null) return found;
            }
            Widget found = findBySpriteRecursive(tab, sprite, disabled);
            if(found!=null) return found;
        }
        return findByGrid(prayer, tab);
    }

    private Widget findBySpriteRecursive(Widget parent, int sprite, int disabled){
        if(parent==null) return null;
        if(parent.getSpriteId()==sprite || parent.getSpriteId()==disabled) return parent;
        Widget[] children = parent.getStaticChildren();
        if(children!=null){
            for(Widget c: children){
                if(c==null) continue;
                if(c.getSpriteId()==sprite || c.getSpriteId()==disabled) return c;
                Widget f = findBySpriteRecursive(c, sprite, disabled);
                if(f!=null) return f;
            }
        }
        Widget[] dyn = parent.getDynamicChildren();
        if(dyn!=null){
            for(Widget c: dyn){
                if(c==null) continue;
                if(c.getSpriteId()==sprite || c.getSpriteId()==disabled) return c;
                Widget f = findBySpriteRecursive(c, sprite, disabled);
                if(f!=null) return f;
            }
        }
        return null;
    }

    private Widget findByGrid(Prayer prayer, Widget tab){
        int[] rc = getRowCol(prayer);
        if(rc==null) return null;
        Rectangle tb = tab.getBounds();
        int cellW = tb.width/5;
        int cellH = tb.height/6;
        int expX = tb.x + rc[1]*cellW + cellW/2;
        int expY = tb.y + rc[0]*cellH + cellH/2;
        Widget best=null;
        double bestD=9999;
        for(int i=0;i<100;i++){
            Widget w=client.getWidget(541,i);
            if(w==null||w.isHidden()||w.getBounds()==null) continue;
            if(w.getBounds().width<12) continue;
            Rectangle b=w.getBounds();
            if(!tb.contains(b)) continue;
            int cx=b.x+b.width/2, cy=b.y+b.height/2;
            double d=Math.hypot(cx-expX, cy-expY);
            if(d<bestD && d<cellW){ bestD=d; best=w; }
        }
        return best;
    }

    private int getSprite(Prayer p){
        switch(p){
            case PROTECT_FROM_MAGIC: return SpriteID.PRAYER_PROTECT_FROM_MAGIC;
            case PROTECT_FROM_MISSILES: return SpriteID.PRAYER_PROTECT_FROM_MISSILES;
            case PROTECT_FROM_MELEE: return SpriteID.PRAYER_PROTECT_FROM_MELEE;
            case EAGLE_EYE: return SpriteID.PRAYER_EAGLE_EYE;
            case MYSTIC_MIGHT: return SpriteID.PRAYER_MYSTIC_MIGHT;
            case THICK_SKIN: return SpriteID.PRAYER_THICK_SKIN;
            case BURST_OF_STRENGTH: return SpriteID.PRAYER_BURST_OF_STRENGTH;
            case CLARITY_OF_THOUGHT: return SpriteID.PRAYER_CLARITY_OF_THOUGHT;
            case SHARP_EYE: return SpriteID.PRAYER_SHARP_EYE;
            case MYSTIC_WILL: return SpriteID.PRAYER_MYSTIC_WILL;
            case ROCK_SKIN: return SpriteID.PRAYER_ROCK_SKIN;
            case SUPERHUMAN_STRENGTH: return SpriteID.PRAYER_SUPERHUMAN_STRENGTH;
            case IMPROVED_REFLEXES: return SpriteID.PRAYER_IMPROVED_REFLEXES;
            case RAPID_RESTORE: return SpriteID.PRAYER_RAPID_RESTORE;
            case RAPID_HEAL: return SpriteID.PRAYER_RAPID_HEAL;
            case PROTECT_ITEM: return SpriteID.PRAYER_PROTECT_ITEM;
            case HAWK_EYE: return SpriteID.PRAYER_HAWK_EYE;
            case MYSTIC_LORE: return SpriteID.PRAYER_MYSTIC_LORE;
            case STEEL_SKIN: return SpriteID.PRAYER_STEEL_SKIN;
            case ULTIMATE_STRENGTH: return SpriteID.PRAYER_ULTIMATE_STRENGTH;
            case INCREDIBLE_REFLEXES: return SpriteID.PRAYER_INCREDIBLE_REFLEXES;
            case RETRIBUTION: return SpriteID.PRAYER_RETRIBUTION;
            case REDEMPTION: return SpriteID.PRAYER_REDEMPTION;
            case SMITE: return SpriteID.PRAYER_SMITE;
            case PRESERVE: return SpriteID.PRAYER_PRESERVE;
            case CHIVALRY: return SpriteID.PRAYER_CHIVALRY;
            case PIETY: return SpriteID.PRAYER_PIETY;
            case RIGOUR: return SpriteID.PRAYER_RIGOUR;
            case AUGURY: return SpriteID.PRAYER_AUGURY;
            default: return -1;
        }
    }
    private int getDisabledSprite(Prayer p){
        switch(p){
            case PROTECT_FROM_MAGIC: return SpriteID.PRAYER_PROTECT_FROM_MAGIC_DISABLED;
            case PROTECT_FROM_MISSILES: return SpriteID.PRAYER_PROTECT_FROM_MISSILES_DISABLED;
            case PROTECT_FROM_MELEE: return SpriteID.PRAYER_PROTECT_FROM_MELEE_DISABLED;
            default: return -1;
        }
    }
    private int getDirectId(Prayer p){
        switch(p){
            case THICK_SKIN: return 3;
            case BURST_OF_STRENGTH: return 10;
            case CLARITY_OF_THOUGHT: return 11;
            case SHARP_EYE: return 27;
            case MYSTIC_WILL: return 30;
            case ROCK_SKIN: return 12;
            case SUPERHUMAN_STRENGTH: return 13;
            case IMPROVED_REFLEXES: return 14;
            case RAPID_RESTORE: return 15;
            case RAPID_HEAL: return 16;
            case PROTECT_ITEM: return 17;
            case HAWK_EYE: return 28;
            case MYSTIC_LORE: return 31;
            case STEEL_SKIN: return 18;
            case ULTIMATE_STRENGTH: return 19;
            case INCREDIBLE_REFLEXES: return 20;
            case PROTECT_FROM_MAGIC: return 21;
            case PROTECT_FROM_MISSILES: return 22;
            case PROTECT_FROM_MELEE: return 23;
            case EAGLE_EYE: return 24;
            case MYSTIC_MIGHT: return 32;
            case RETRIBUTION: return 24;
            case REDEMPTION: return 25;
            case SMITE: return 26;
            case PRESERVE: return 37;
            case CHIVALRY: return 34;
            case PIETY: return 35;
            case RIGOUR: return 33;
            case AUGURY: return 36;
            default: return -1;
        }
    }
    private int[] getRowCol(Prayer p){
        switch(p){
            case THICK_SKIN: return new int[]{0,0};
            case BURST_OF_STRENGTH: return new int[]{0,1};
            case CLARITY_OF_THOUGHT: return new int[]{0,2};
            case SHARP_EYE: return new int[]{0,3};
            case MYSTIC_WILL: return new int[]{0,4};
            case ROCK_SKIN: return new int[]{1,0};
            case SUPERHUMAN_STRENGTH: return new int[]{1,1};
            case IMPROVED_REFLEXES: return new int[]{1,2};
            case RAPID_RESTORE: return new int[]{1,3};
            case RAPID_HEAL: return new int[]{1,4};
            case PROTECT_ITEM: return new int[]{2,0};
            case HAWK_EYE: return new int[]{2,1};
            case MYSTIC_LORE: return new int[]{2,2};
            case STEEL_SKIN: return new int[]{2,3};
            case ULTIMATE_STRENGTH: return new int[]{2,4};
            case INCREDIBLE_REFLEXES: return new int[]{3,0};
            case PROTECT_FROM_MAGIC: return new int[]{3,1};
            case PROTECT_FROM_MISSILES: return new int[]{3,2};
            case PROTECT_FROM_MELEE: return new int[]{3,3};
            case EAGLE_EYE: return new int[]{3,4};
            case MYSTIC_MIGHT: return new int[]{4,0};
            case RETRIBUTION: return new int[]{4,1};
            case REDEMPTION: return new int[]{4,2};
            case SMITE: return new int[]{4,3};
            case PRESERVE: return new int[]{4,4};
            case CHIVALRY: return new int[]{5,0};
            case PIETY: return new int[]{5,1};
            case RIGOUR: return new int[]{5,2};
            case AUGURY: return new int[]{5,3};
            default: return null;
        }
    }
}
