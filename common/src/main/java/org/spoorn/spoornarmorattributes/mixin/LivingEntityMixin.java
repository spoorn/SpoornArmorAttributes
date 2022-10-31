package org.spoorn.spoornarmorattributes.mixin;

import static org.spoorn.spoornarmorattributes.util.SpoornArmorAttributesUtil.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spoorn.spoornarmorattributes.att.Attribute;
import org.spoorn.spoornarmorattributes.config.ModConfig;
import org.spoorn.spoornarmorattributes.util.SpoornArmorAttributesUtil;

import java.util.Map;
import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow public abstract Iterable<ItemStack> getArmorItems();
    
    @Inject(method = "getAttributeValue", at = @At(value = "RETURN"), cancellable = true)
    private void modifyMaxHealth(EntityAttribute attribute, CallbackInfoReturnable<Double> cir) {
        if (attribute == EntityAttributes.GENERIC_MAX_HEALTH || attribute == EntityAttributes.GENERIC_MOVEMENT_SPEED || attribute == EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE) {
            try {
                // Only apply to players
                if ((Object) this instanceof PlayerEntity player && player.getInventory() != null) {
                    Iterable<ItemStack> armorItems = this.getArmorItems();
                    if (armorItems != null) {
                        double res = cir.getReturnValue();
                        double original = res;
                        for (ItemStack stack : armorItems) {
                            Optional<NbtCompound> optNbt = SpoornArmorAttributesUtil.getSAANbtIfPresent(stack);
                            
                            if (optNbt.isPresent()) {
                                NbtCompound nbt = optNbt.get();

                                for (Map.Entry<String, Attribute> entry : Attribute.VALUES.entrySet()) {
                                    String name = entry.getKey();
                                    EntityAttribute mappedEntityAttribute = ATTRIBUTE_TO_ENTITY_ATTRIBUTE.get(name);
                                    
                                    if (mappedEntityAttribute == attribute && nbt.contains(name)) {
                                        NbtCompound subNbt = nbt.getCompound(name);

                                        switch (name) {
                                            case Attribute.MAX_HEALTH_NAME:
                                                res += handleMaxHealth(subNbt);
                                                break;
                                            case Attribute.MOVEMENT_SPEED_NAME:
                                                res += handleMovementSpeed(subNbt, (float) original);
                                                break;
                                            case Attribute.KNOCKBACK_RESISTANCE_NAME:
                                                res += handleKnockbackResistance(subNbt);
                                                break;
                                            default:
                                                // Do nothing
                                        }
                                    }
                                }
                            }
                        }
                        cir.setReturnValue(res);
                    }
                }
            } catch (Exception e) {
                System.err.println("[SpoornArmorAttributes] Applying attribute effects to max health failed: " + e);
            }
        }
    }
    
    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;applyDamage(Lnet/minecraft/entity/damage/DamageSource;F)V"))
    private void applyBeforeDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        try {
            // Only apply to players
            if (((Object) this instanceof ServerPlayerEntity player) && player.getInventory() != null) {
                Iterable<ItemStack> armorItems = player.getArmorItems();
                if (armorItems != null) {
                    float thornsDamage = 0;
                    for (ItemStack stack : armorItems) {
                        Optional<NbtCompound> optNbt = SpoornArmorAttributesUtil.getSAANbtIfPresent(stack);
                        if (optNbt.isPresent()) {
                            NbtCompound nbt = optNbt.get();

                            if (nbt.contains(Attribute.THORNS_NAME)) {
                                NbtCompound subNbt = nbt.getCompound(Attribute.THORNS_NAME);
                                thornsDamage += handleThorns(subNbt, source, amount);
                            }
                        }
                    }

                    // Thorns damage
                    if (thornsDamage > 0) {
                        // this should be a PlayerEntity already
                        source.getAttacker().damage(DamageSource.player((PlayerEntity) (Object) this).setUsesMagic(), thornsDamage);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[SpoornArmorAttributes] Applying attribute effects to final damage failed: " + e);
        }
    }

    private float handleThorns(NbtCompound nbt, DamageSource source, float damage) {
        if (nbt.contains(THORNS)) {
            float returnDmgPercent = nbt.getFloat(THORNS);
            Entity attacker = source.getAttacker();
            if (attacker instanceof LivingEntity && !attacker.world.isClient) {
                return damage * (returnDmgPercent / 100);
            }
        }
        return 0;
    }
    
    private float handleMaxHealth(NbtCompound nbt) {
        if (nbt.contains(BONUS_MAX_HEALTH)) {
            float res = nbt.getFloat(BONUS_MAX_HEALTH);
            if (ModConfig.get().maxHealthConfig.roundBonusHealth) {
                return Math.round(res);
            }
            return res;
        }
        return 0;
    }

    private float handleMovementSpeed(NbtCompound nbt, float originalSpeed) {
        if (nbt.contains(MOVEMENT_SPEED)) {
            return originalSpeed * nbt.getFloat(MOVEMENT_SPEED) / 100;
        }
        return originalSpeed;
    }

    private float handleKnockbackResistance(NbtCompound nbt) {
        if (nbt.contains(KNOCKBACK_RESISTANCE)) {
            return nbt.getFloat(KNOCKBACK_RESISTANCE);
        }
        return 0;
    }
}
