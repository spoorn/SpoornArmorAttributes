package org.spoorn.spoornarmorattributes.mixin;

import static org.spoorn.spoornarmorattributes.util.SpoornArmorAttributesUtil.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spoorn.spoornarmorattributes.config.ModConfig;
import org.spoorn.spoornarmorattributes.util.SpoornArmorAttributesUtil;

@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandlerMixin {

    @Shadow @Final private Property levelCost;

    @Shadow private int repairItemUsage;

    /**
     *
     * @param player
     * @param output ItemStack on the cursor.  Note: This will be "air" if user Shift+Clicks the output item!
     * @param ci
     */
    @Inject(method = "onTakeOutput", at = @At(value = "HEAD"))
    private void rerollSAA(PlayerEntity player, ItemStack output, CallbackInfo ci) {
        ForgingScreenHandlerAccessor accessor = (ForgingScreenHandlerAccessor) this;
        Inventory inputInventory = accessor.getInput();
        ItemStack input1 = inputInventory.getStack(0);
        ItemStack input2 = inputInventory.getStack(1);

        // Apply on output item
        if (player instanceof ServerPlayerEntity) {
            if (output.hasNbt()) {
                NbtCompound root = output.getNbt();
                SpoornArmorAttributesUtil.rollOrUpgradeNbt(root);
            }
        }

        // Put items in the correct order so vanilla code can subtract the stack count and remove item correctly
        // First slot should be the weapon, 2nd slot should be the upgrade item
        // This prevents the wrong order from deleting the entire stack of the upgrade item
        ItemStack weapon;
        if ((weapon = canUpgradeSAA(input1, input2)) != null || (weapon = canRerollSAA(input1, input2)) != null) {
            ItemStack temp = weapon == input1 ? input2 : input1;
            // Swap if in wrong order
            inputInventory.setStack(0, weapon);
            inputInventory.setStack(1, temp);
        }
    }

    @Inject(method = "updateResult", at = @At(value = "RETURN"))
    private void addRerollsSAA(CallbackInfo ci) {
        ForgingScreenHandlerAccessor accessor = (ForgingScreenHandlerAccessor) this;
        Inventory inputInventory = accessor.getInput();
        ItemStack input1 = inputInventory.getStack(0);
        ItemStack input2 = inputInventory.getStack(1);

        ItemStack swaStack = canRerollSAA(input1, input2);
        if (swaStack != null) {
            ItemStack existingOutputStack = accessor.getOutput().getStack(0);
            boolean useExistingOutput = existingOutputStack != null && !existingOutputStack.isEmpty()
                    && existingOutputStack.getItem() == swaStack.getItem();
            ItemStack output;

            // use existing output stack and modify NBT in case the reroll item is used for other purposes
            if (useExistingOutput) {
                output = existingOutputStack;
            } else {
                output = swaStack.copy();
            }
            
            NbtCompound root = output.getNbt();
            // This will cause a reroll no matter what.  We could do the same thing with Upgrading in the future if it's simpler than the mixin in ForgingScreenHandlerMixin
            if (root.contains(NBT_KEY)) {
                root.remove(NBT_KEY);
            }
            root.putBoolean(REROLL_NBT_KEY, true);

            if (!useExistingOutput) {
                this.levelCost.set(ModConfig.get().rerollLevelCost);
                this.repairItemUsage = 1;
                accessor.getOutput().setStack(0, output);
                ((ScreenHandlerAccessor) this).trySendContentUpdates();
            }
        } else {
            swaStack = canUpgradeSAA(input1, input2);
            if (swaStack != null) {
                ItemStack existingOutputStack = accessor.getOutput().getStack(0);
                boolean useExistingOutput = existingOutputStack != null && !existingOutputStack.isEmpty()
                        && existingOutputStack.getItem() == swaStack.getItem();
                ItemStack output;

                // use existing output stack and modify NBT in case the upgrade item is used for other purposes
                if (useExistingOutput) {
                    output = existingOutputStack;
                } else {
                    output = swaStack.copy();
                }
                
                NbtCompound root = output.getNbt();
                root.putBoolean(UPGRADE_NBT_KEY, true);

                if (!useExistingOutput) {
                    this.levelCost.set(ModConfig.get().upgradeLevelCost);
                    this.repairItemUsage = 1;
                    accessor.getOutput().setStack(0, output);
                    ((ScreenHandlerAccessor) this).trySendContentUpdates();
                }
            }
        }
    }
    
    @Inject(method = "canTakeOutput", at = @At(value = "HEAD"), cancellable = true)
    private void allowNoLevelCostSAA(PlayerEntity player, boolean present, CallbackInfoReturnable<Boolean> cir) {
        ForgingScreenHandlerAccessor accessor = (ForgingScreenHandlerAccessor) this;
        Inventory inputInventory = accessor.getInput();
        ItemStack input1 = inputInventory.getStack(0);
        ItemStack input2 = inputInventory.getStack(1);
        
        if ((canRerollSAA(input1, input2) != null && ModConfig.get().rerollLevelCost <= 0)
            || (canUpgradeSAA(input1, input2) != null && ModConfig.get().upgradeLevelCost <= 0)) {
            cir.setReturnValue(player.getAbilities().creativeMode || player.experienceLevel >= this.levelCost.get());
            cir.cancel();
        }
    }

    private ItemStack canRerollSAA(ItemStack stack1, ItemStack stack2) {
        if (SpoornArmorAttributesUtil.shouldTryGenAttr(stack1) && SpoornArmorAttributesUtil.isRerollItem(stack2)) {
            return stack1;
        } else if (SpoornArmorAttributesUtil.shouldTryGenAttr(stack2) && SpoornArmorAttributesUtil.isRerollItem(stack1)) {
            return stack2;
        }
        return null;
    }

    private ItemStack canUpgradeSAA(ItemStack stack1, ItemStack stack2) {
        if (SpoornArmorAttributesUtil.shouldTryGenAttr(stack1) && SpoornArmorAttributesUtil.isUpgradeItem(stack2)) {
            return stack1;
        } else if (SpoornArmorAttributesUtil.shouldTryGenAttr(stack2) && SpoornArmorAttributesUtil.isUpgradeItem(stack1)) {
            return stack2;
        }
        return null;
    }
}
