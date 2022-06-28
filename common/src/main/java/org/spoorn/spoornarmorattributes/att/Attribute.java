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
    public static final String DMG_REDUCTION_NAME = "DMG_REDUCTION";
    public static final String MOVEMENT_SPEED_NAME = "MOVE_SPEED";
    public static final String KNOCKBACK_RESISTANCE_NAME = "KNOCKBACK_RESIST";
    
    public static Attribute MAX_HEALTH;
    public static Attribute DMG_REDUCTION;
    public static Attribute MOVEMENT_SPEED;
    public static Attribute KNOCKBACK_RESISTANCE;

    public static Map<String, Attribute> VALUES = new HashMap<>();
    public static List<String> TOOLTIPS = new ArrayList<>();

    public final String name;
    public final double chance;

    public static void init() {
        MAX_HEALTH = new Attribute(MAX_HEALTH_NAME, ModConfig.get().maxHealthConfig.attributeChance);
        DMG_REDUCTION = new Attribute(DMG_REDUCTION_NAME, ModConfig.get().dmgReductionConfig.attributeChance);
        MOVEMENT_SPEED = new Attribute(MOVEMENT_SPEED_NAME, ModConfig.get().movementSpeedConfig.attributeChance);
        KNOCKBACK_RESISTANCE = new Attribute(KNOCKBACK_RESISTANCE_NAME, ModConfig.get().knockbackResistanceConfig.attributeChance);
        VALUES.put(MAX_HEALTH.name, MAX_HEALTH);
        VALUES.put(DMG_REDUCTION.name, DMG_REDUCTION);
        VALUES.put(MOVEMENT_SPEED.name, MOVEMENT_SPEED);
        VALUES.put(KNOCKBACK_RESISTANCE.name, KNOCKBACK_RESISTANCE);
        TOOLTIPS.add(MAX_HEALTH.name);
        TOOLTIPS.add(DMG_REDUCTION.name);
        TOOLTIPS.add(MOVEMENT_SPEED.name);
        TOOLTIPS.add(KNOCKBACK_RESISTANCE.name);
    }
}
