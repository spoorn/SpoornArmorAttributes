package org.spoorn.spoornarmorattributes.client;

import static org.spoorn.spoornarmorattributes.util.SpoornArmorAttributesUtil.*;
import lombok.extern.log4j.Log4j2;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.spoorn.spoornarmorattributes.att.Attribute;
import org.spoorn.spoornarmorattributes.util.SpoornArmorAttributesUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@Environment(EnvType.CLIENT)
public class SpoornArmorAttributesClient {

    private static final Style MAX_HEALTH_STYLE = Style.EMPTY.withColor(TextColor.fromRgb(16226554));
    private static final MutableText MAX_HEALTH_TOOLTIP = new TranslatableText("saa.tooltip.maxhealth");
    
    public static void init() {
        log.info("Hello client from SpoornArmorAttributes!");
    }

    // Mimic Fabric API's ItemTooltipCallback with our own Mixin to support both Fabric and Forge without introducing
    // Fabric API as a dependency, yet still be easily converted back
    @FunctionalInterface
    public interface ItemTooltipCallback {
        void getTooltip(ItemStack stack, TooltipContext context, List<Text> lines);
    }

    public static ItemTooltipCallback registerTooltipCallback() {
//        ItemTooltipCallback.EVENT.register(
        return (ItemStack stack, TooltipContext context, List<Text> lines) -> {
            Optional<NbtCompound> optNbt = SpoornArmorAttributesUtil.getSAANbtIfPresent(stack);

            List<Text> adds = null;

            // Rerolling
            if (stack.hasNbt() && optNbt.isEmpty()) {
                NbtCompound root = stack.getNbt();
                if (root.getBoolean(REROLL_NBT_KEY)) {
                    adds = new ArrayList<>();
                    adds.add(new LiteralText(""));
                    adds.add(new LiteralText("???").formatted(Formatting.AQUA));
                }
            } else if (optNbt.isPresent()) {
                NbtCompound nbt = optNbt.get();
                adds = new ArrayList<>();
                adds.add(new LiteralText(""));

                for (String name : Attribute.TOOLTIPS) {
                    if (nbt.contains(name)) {
                        NbtCompound subNbt = nbt.getCompound(name);
                        switch (name) {
                            case Attribute.MAX_HEALTH_NAME:
                                handleMaxHealth(adds, subNbt);
                                break;
                            default:
                                // do nothing
                        }
                    }
                }
            }

            // Upgrades
            if (stack.hasNbt()) {
                NbtCompound root = stack.getNbt();
                if (root.getBoolean(UPGRADE_NBT_KEY)) {
                    if (adds == null) {
                        adds = new ArrayList<>();
                    }
                    adds.add(new LiteralText(""));
                    adds.add(new LiteralText("+++").formatted(Formatting.RED));
                }
            }

            if (adds != null && adds.size() > 1) {
                // Add after the item name
                lines.addAll(1, adds);
            }
        };
    }

    private static void handleMaxHealth(List<Text> tooltips, NbtCompound nbt) {
        if (nbt.contains(BONUS_MAX_HEALTH)) {
            float bonusMaxHealth = nbt.getFloat(BONUS_MAX_HEALTH);
            MutableText text = new LiteralText("+" + Math.round(bonusMaxHealth)).append(MAX_HEALTH_TOOLTIP).setStyle(MAX_HEALTH_STYLE);
            tooltips.add(text);
        }
    }
}
