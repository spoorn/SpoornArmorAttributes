package org.spoorn.spoornarmorattributes.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spoorn.spoornarmorattributes.entity.damage.SAAExplosionDamageSource;

/**
 * TODO: configurations for these
 */
@Mixin(Explosion.class)
public class ExplosionMixin {

    @Shadow @Final private DamageSource damageSource;
    
    @Redirect(method = "collectBlocksAndDamageEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isImmuneToExplosion()Z"))
    private boolean disableExplosionOnNonLivingEntitiesAndPlayers(Entity instance) {
        if ((this.damageSource instanceof SAAExplosionDamageSource) && (!instance.isLiving() || instance.isPlayer())) {
            return true;
        }
        return instance.isImmuneToExplosion();
    }
}
