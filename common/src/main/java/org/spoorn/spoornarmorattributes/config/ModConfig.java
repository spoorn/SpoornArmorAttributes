package org.spoorn.spoornarmorattributes.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import org.spoorn.spoornarmorattributes.SpoornArmorAttributes;

@Config(name = SpoornArmorAttributes.MODID)
public class ModConfig implements ConfigData {
    
    @Comment("Reroll item.  Pair an armor piece with Spoorn Armor Attributes on it with this item (vanilla or modded)\n" +
            "in an Anvil to reroll attributes [default = minecraft:lapis_lazuli]")
    public String rerollItem = "minecraft:lapis_lazuli";
    
    @Comment("Upgrade item.  Pair an armor piece with this item in an Anvil to roll bonus attributes and only adds\n" +
            "stats onto the armor if it is an upgrade [default = minecraft:diamond]")
    public String upgradeItem = "minecraft:diamond";


    public static void init() {
        AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
        Expressions.init();
    }

    public static ModConfig get() {
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }
}
