package org.spoorn.spoornarmorattributes.mixin;

import static org.spoorn.spoornarmorattributes.util.SpoornArmorAttributesUtil.DMG_REDUCTION;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spoorn.spoornarmorattributes.att.Attribute;
import org.spoorn.spoornarmorattributes.util.SpoornArmorAttributesUtil;

import java.util.Optional;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Redirect(method = "applyDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setHealth(F)V"))
    private void modifyFinalDamage(PlayerEntity instance, float health, DamageSource source, float amount) {
        try {
            // Only apply to players
            if (instance instanceof ServerPlayerEntity && instance.getInventory() != null) {
                Iterable<ItemStack> armorItems = instance.getArmorItems();
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
    
    private float handleDmgReduction(NbtCompound nbt, float damage) {
        if (nbt.contains(DMG_REDUCTION)) {
            return damage * (1 - nbt.getFloat(DMG_REDUCTION)/100);
        }
        return damage;
    }
}
