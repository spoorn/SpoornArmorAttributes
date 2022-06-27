package org.spoorn.spoornarmorattributes.mixin;

import static org.spoorn.spoornarmorattributes.util.SpoornArmorAttributesUtil.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
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
        if (attribute == EntityAttributes.GENERIC_MAX_HEALTH || attribute == EntityAttributes.GENERIC_MOVEMENT_SPEED) {
            try {
                // Only apply to players
                if ((Object) this instanceof PlayerEntity player && player.getInventory() != null) {
                    Iterable<ItemStack> armorItems = this.getArmorItems();
                    if (armorItems != null) {
                        double res = cir.getReturnValue();
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
                                                res += handleMovementSpeed(subNbt);
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
    
    @Redirect(method = "applyDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setHealth(F)V"))
    private void modifyFinalDamage(LivingEntity instance, float health) {
        try {
            // Only apply to players
            if (instance instanceof PlayerEntity player && player.getInventory() != null) {
                Iterable<ItemStack> armorItems = this.getArmorItems();
                if (armorItems != null) {
                    float originalDamage = instance.getHealth() - health;
                    float newDamage = instance.getHealth() - health;  // Assume this will be positive
                    for (ItemStack stack : armorItems) {
                        Optional<NbtCompound> optNbt = SpoornArmorAttributesUtil.getSAANbtIfPresent(stack);
                        if (optNbt.isPresent()) {
                            NbtCompound nbt = optNbt.get();
                            if (nbt.contains(Attribute.DMG_REDUCTION_NAME)) {
                                NbtCompound subNbt = nbt.getCompound(Attribute.DMG_REDUCTION_NAME);
                                newDamage = handleDmgReduction(subNbt, newDamage);
                            }
                        }
                    }
                    health -= newDamage - originalDamage;
                }
            }
        } catch (Exception e) {
            System.err.println("[SpoornArmorAttributes] Applying attribute effects to final damage failed: " + e);
        }
        
        instance.setHealth(health);
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
    
    private float handleDmgReduction(NbtCompound nbt, float damage) {
        if (nbt.contains(DMG_REDUCTION)) {
            return damage * (1 - nbt.getFloat(DMG_REDUCTION)/100);
        }
        return damage;
    }

    private float handleMovementSpeed(NbtCompound nbt) {
        if (nbt.contains(MOVEMENT_SPEED)) {
            return nbt.getFloat(MOVEMENT_SPEED);
        }
        return 0;
    }
}
