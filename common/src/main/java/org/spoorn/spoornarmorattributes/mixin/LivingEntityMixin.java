package org.spoorn.spoornarmorattributes.mixin;

import static org.spoorn.spoornarmorattributes.util.SpoornArmorAttributesUtil.BONUS_MAX_HEALTH;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spoorn.spoornarmorattributes.att.Attribute;
import org.spoorn.spoornarmorattributes.config.ModConfig;
import org.spoorn.spoornarmorattributes.util.SpoornArmorAttributesUtil;

import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow public abstract Iterable<ItemStack> getArmorItems();
    
    @Inject(method = "getAttributeValue", at = @At(value = "RETURN"), cancellable = true)
    private void modifyMaxHealth(EntityAttribute attribute, CallbackInfoReturnable<Double> cir) {
        if (attribute == EntityAttributes.GENERIC_MAX_HEALTH) {
            try {
                // Only apply to players on the server
                if ((Object) this instanceof PlayerEntity player && player.getInventory() != null) {
                    Iterable<ItemStack> armorItems = this.getArmorItems();
                    if (armorItems != null) {
                        for (ItemStack stack : armorItems) {
                            Optional<NbtCompound> optNbt = SpoornArmorAttributesUtil.getSAANbtIfPresent(stack);
                            if (optNbt.isPresent()) {
                                NbtCompound nbt = optNbt.get();
                                if (nbt.contains(Attribute.MAX_HEALTH_NAME)) {
                                    NbtCompound subNbt = nbt.getCompound(Attribute.MAX_HEALTH_NAME);
                                    double res = cir.getReturnValue();
                                    float bonusHealth = handleMaxHealth(subNbt);
                                    cir.setReturnValue(res + bonusHealth);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("[SpoornArmorAttributes] Applying base attribute effects failed: " + e);
            }
        }
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
}
