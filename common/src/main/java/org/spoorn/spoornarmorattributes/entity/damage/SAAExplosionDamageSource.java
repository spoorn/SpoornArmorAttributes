package org.spoorn.spoornarmorattributes.entity.damage;

import net.minecraft.entity.damage.DamageSource;

/**
 * Used to identify if an explosion was from this mod.
 */
public class SAAExplosionDamageSource extends DamageSource {

    public SAAExplosionDamageSource(String name) {
        super(name);
    }
}
