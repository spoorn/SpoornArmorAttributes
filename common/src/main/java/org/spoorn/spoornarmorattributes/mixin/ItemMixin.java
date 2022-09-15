package org.spoorn.spoornarmorattributes.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spoorn.spoornarmorattributes.config.ModConfig;
import org.spoorn.spoornarmorattributes.util.SpoornArmorAttributesUtil;

@Mixin(Item.class)
public class ItemMixin {

    /**
     * Generates the NBT data for our mod on an item.
     */
    @Inject(method = "inventoryTick", at = @At(value = "HEAD"))
    public void addCustomNbt(ItemStack stack, World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (ModConfig.get().applyOnInventoryTick && !world.isClient() && SpoornArmorAttributesUtil.shouldTryGenAttr(stack)) {
            NbtCompound root = stack.getOrCreateNbt();

            SpoornArmorAttributesUtil.rollAttributes(root);
        }
    }
}
