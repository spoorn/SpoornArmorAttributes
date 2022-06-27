package org.spoorn.spoornarmorattributes.att;

import lombok.AllArgsConstructor;
import org.spoorn.spoornarmorattributes.config.ModConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains generic information of the attributes.
 * 
 * - Max HP
 * - dmg reduction
 * - protection
 * - Cold Protection
 * - Fire Protection
 * - Thorns
 * - Explosive
 */
@AllArgsConstructor
public class Attribute {

    // WARNING: Changing the name in the static initialization will break existing Nbt data
    public static final String MAX_HEALTH_NAME = "MAX_HEALTH";
    
    public static Attribute MAX_HEALTH;

    public static Map<String, Attribute> VALUES = new HashMap<>();
    public static List<String> TOOLTIPS = new ArrayList<>();

    public final String name;
    public final double chance;

    public static void init() {
        MAX_HEALTH = new Attribute(MAX_HEALTH_NAME, ModConfig.get().maxHealthConfig.attributeChance);
        VALUES.put(MAX_HEALTH.name, MAX_HEALTH);
        TOOLTIPS.add(MAX_HEALTH.name);
    }
}
