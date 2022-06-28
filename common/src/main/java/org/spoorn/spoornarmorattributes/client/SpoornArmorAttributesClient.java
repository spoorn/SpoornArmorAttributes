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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Log4j2
@Environment(EnvType.CLIENT)
public class SpoornArmorAttributesClient {

    private static final Style MAX_HEALTH_STYLE = Style.EMPTY.withColor(TextColor.fromRgb(16226554));
    private static final Style DMG_REDUCTION_STYLE = Style.EMPTY.withColor(TextColor.fromRgb(16568720));
    private static final Style MOVEMENT_SPEED_STYLE = Style.EMPTY.withColor(TextColor.fromRgb(16250004));
    private static final Style KNOCKBACK_RESISTANCE_STYLE = Style.EMPTY.withColor(TextColor.fromRgb(8976303));
    private static final Style THORNS_STYLE = Style.EMPTY.withColor(TextColor.fromRgb(7551762));
    private static final MutableText MAX_HEALTH_TOOLTIP = new TranslatableText("saa.tooltip.maxhealth");
    private static final MutableText DMG_REDUCTION_TOOLTIP = new TranslatableText("saa.tooltip.dmgReduc");
    private static final MutableText MOVEMENT_SPEED_TOOLTIP = new TranslatableText("saa.tooltip.moveSpeed");
    private static final MutableText KNOCKBACK_RESISTANCE_TOOLTIP = new TranslatableText("saa.tooltip.knockResist");
    private static final MutableText THORNS_TOOLTIP = new TranslatableText("saa.tooltip.thorns");
    private static final DecimalFormatSymbols SYMBOLS = new DecimalFormatSymbols(Locale.US);
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#", SYMBOLS);
    private static final DecimalFormat INTEGER_FORMAT = new DecimalFormat("#", SYMBOLS);
    
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
                            case Attribute.DMG_REDUCTION_NAME:
                                handleDmgReduction(adds, subNbt);
                                break;
                            case Attribute.MOVEMENT_SPEED_NAME:
                                handleMovementSpeed(adds, subNbt);
                                break;
                            case Attribute.KNOCKBACK_RESISTANCE_NAME:
                                handleKnockbackResistance(adds, subNbt);
                                break;
                            case Attribute.THORNS_NAME:
                                handleThorns(adds, subNbt);
                                break;
                            default:
                                // do nothing
                                log.error("Unsupported Spoorn Armor Attribute {}", name);
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
            float value = nbt.getFloat(BONUS_MAX_HEALTH);
            MutableText text = new LiteralText("+" + Math.round(value)).append(MAX_HEALTH_TOOLTIP).setStyle(MAX_HEALTH_STYLE);
            tooltips.add(text);
        }
    }

    private static void handleDmgReduction(List<Text> tooltips, NbtCompound nbt) {
        if (nbt.contains(DMG_REDUCTION)) {
            float value = nbt.getFloat(DMG_REDUCTION);
            MutableText text = new LiteralText("+" + INTEGER_FORMAT.format(value)).append(DMG_REDUCTION_TOOLTIP).setStyle(DMG_REDUCTION_STYLE);
            tooltips.add(text);
        }
    }

    private static void handleMovementSpeed(List<Text> tooltips, NbtCompound nbt) {
        if (nbt.contains(MOVEMENT_SPEED)) {
            float value = nbt.getFloat(MOVEMENT_SPEED);
            // Tries to show an integer value by multiplying by 10
            MutableText text = new LiteralText("+" + DECIMAL_FORMAT.format(value * 10)).append(MOVEMENT_SPEED_TOOLTIP).setStyle(MOVEMENT_SPEED_STYLE);
            tooltips.add(text);
        }
    }

    private static void handleKnockbackResistance(List<Text> tooltips, NbtCompound nbt) {
        if (nbt.contains(KNOCKBACK_RESISTANCE)) {
            float value = nbt.getFloat(KNOCKBACK_RESISTANCE);
            // Tries to show an integer value by multiplying by 10
            MutableText text = new LiteralText("+" + INTEGER_FORMAT.format(value * 100)).append(KNOCKBACK_RESISTANCE_TOOLTIP).setStyle(KNOCKBACK_RESISTANCE_STYLE);
            tooltips.add(text);
        }
    }

    private static void handleThorns(List<Text> tooltips, NbtCompound nbt) {
        if (nbt.contains(THORNS)) {
            float value = nbt.getFloat(THORNS);
            MutableText text = new LiteralText("+" + INTEGER_FORMAT.format(value)).append(THORNS_TOOLTIP).setStyle(THORNS_STYLE);
            tooltips.add(text);
        }
    }
}
