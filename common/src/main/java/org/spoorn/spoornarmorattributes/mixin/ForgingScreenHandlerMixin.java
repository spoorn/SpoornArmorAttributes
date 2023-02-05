package org.spoorn.spoornarmorattributes.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spoorn.spoornarmorattributes.util.SpoornArmorAttributesUtil;

/**
 * Note: If all this code starts running into problems, we can migrate to using inventoryTicks, like we did with rerolls
 * in AnvilScreenHandlerMixin - by removing the NBT_KEY and triggering rerolls/upgrades during the inventoryTick.
 */
@Mixin(ForgingScreenHandler.class)
public class ForgingScreenHandlerMixin {

    /**
     * This is a backup method on top of {@link AnvilScreenHandlerMixin}.  When the user Shift+Clicks on the output item
     * instead of a simple Left Click, slot.onTakeItem() will be called with an empty ItemStack which causes the code in
     * {@link AnvilScreenHandlerMixin} to not properly apply the attribute logic.  Instead, this transferSlot() is called here.
     * 
     * Grabs the original ItemStack local variable, for compatibility with SpoornWeaponAttributes which does a @Redirect
     */
    @Inject(method = "quickMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ForgingScreenHandler;insertItem(Lnet/minecraft/item/ItemStack;IIZ)Z", ordinal = 0),
        locals = LocalCapture.CAPTURE_FAILHARD)
    private void rollOrUpgradeShiftClick(PlayerEntity player, int index, CallbackInfoReturnable<ItemStack> cir, ItemStack itemStack, Slot slot, ItemStack itemStack2) {
        if ((Object)this instanceof AnvilScreenHandler && itemStack2 != null) {
            // index == 2 when transferring from output to player inventory
            if (index == 2 && player instanceof ServerPlayerEntity && itemStack2.hasNbt()) {
                NbtCompound root = itemStack2.getNbt();
                SpoornArmorAttributesUtil.rollOrUpgradeNbt(root);
            }
        }
    }
}
