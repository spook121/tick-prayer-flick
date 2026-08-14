package com.tickprayerflick;

import java.awt.Color;
import net.runelite.api.Prayer;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("tickprayerflick")
public interface TickPrayerFlickConfig extends Config
{
    @ConfigItem(keyName = "prayerOne", name = "Prayer 1", description = "", position = 0)
    default Prayer prayerOne(){ return Prayer.PROTECT_FROM_MAGIC; }

    @ConfigItem(keyName = "prayerTwo", name = "Prayer 2", description = "", position = 1)
    default Prayer prayerTwo(){ return Prayer.PROTECT_FROM_MISSILES; }

    @ConfigItem(keyName = "prayerThree", name = "Prayer 3", description = "", position = 2)
    default Prayer prayerThree(){ return Prayer.PROTECT_FROM_MELEE; }

    @ConfigItem(keyName = "prayerFour", name = "Prayer 4", description = "", position = 3)
    default Prayer prayerFour(){ return Prayer.EAGLE_EYE; }

    @ConfigItem(keyName = "sequence", name = "Sequence", description = "Order", position = 4)
    default Sequence sequence(){ return Sequence.A_B_C; }

    @ConfigItem(keyName = "tickOffset", name = "Tick Offset", description = "", position = 5)
    default int tickOffset(){ return 0; }

    @ConfigItem(keyName = "highlightNext", name = "Highlight NEXT (1-tick)", description = "Show NEXT prayer to avoid damage", position = 6)
    default boolean highlightNext(){ return true; }

    @ConfigItem(keyName = "highlightColor", name = "Color", description = "", position = 7)
    default Color highlightColor(){ return new Color(0x00,0xFF,0xFF); }

    @ConfigItem(keyName = "borderWidth", name = "Border", description = "", position = 8)
    default int borderWidth(){ return 2; }
}
