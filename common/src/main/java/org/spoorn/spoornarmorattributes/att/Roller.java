package org.spoorn.spoornarmorattributes.att;

import org.spoorn.spoornarmorattributes.config.ModConfig;
import org.spoorn.spoornarmorattributes.config.attribute.DamageReductionConfig;
import org.spoorn.spoornarmorattributes.config.attribute.MaxHealthConfig;
import org.spoorn.spoornarmorattributes.config.attribute.MovementSpeedConfig;
import org.spoorn.spoornarmorattributes.util.SpoornArmorAttributesUtil;

public class Roller {

    public static float rollMaxHealth() {
        MaxHealthConfig config = ModConfig.get().maxHealthConfig;
        return SpoornArmorAttributesUtil.drawRandom(config.useGaussian, config.mean, config.standardDeviation, config.minHealth, config.maxHealth);
    }

    public static float rollDmgReduction() {
        DamageReductionConfig config = ModConfig.get().dmgReductionConfig;
        return SpoornArmorAttributesUtil.drawRandom(config.useGaussian, config.mean, config.standardDeviation, config.minDmgReduction, config.maxDmgReduction);
    }

    public static float rollMovementSpeed() {
        MovementSpeedConfig config = ModConfig.get().movementSpeedConfig;
        return SpoornArmorAttributesUtil.drawRandom(config.useGaussian, config.mean, config.standardDeviation, config.minSpeed, config.maxSpeed);
    }
}
