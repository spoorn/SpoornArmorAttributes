package org.spoorn.spoornarmorattributes.util;

import lombok.extern.log4j.Log4j2;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spoorn.spoornarmorattributes.att.Attribute;
import org.spoorn.spoornarmorattributes.att.Roller;
import org.spoorn.spoornarmorattributes.config.ModConfig;

import java.util.*;

@Log4j2
public class SpoornArmorAttributesUtil {

    public static final String NBT_KEY = "saa1";
    public static final String REROLL_NBT_KEY = "saa1_reroll";
    public static final String UPGRADE_NBT_KEY = "saa1_upgrade";
    public static final String BONUS_MAX_HEALTH = "bonusMaxHP";
    public static final String DMG_REDUCTION = "dmgReduc";
    public static final String MOVEMENT_SPEED = "moveSpeed";
    public static final String KNOCKBACK_RESISTANCE = "knockResist";
    public static final String THORNS = "thorns";
    public static final Random RANDOM = new Random();
    
    public static final Map<String, EntityAttribute> ATTRIBUTE_TO_ENTITY_ATTRIBUTE = Map.of(
            Attribute.MAX_HEALTH_NAME, EntityAttributes.GENERIC_MAX_HEALTH,
            Attribute.MOVEMENT_SPEED_NAME, EntityAttributes.GENERIC_MOVEMENT_SPEED,
            Attribute.KNOCKBACK_RESISTANCE_NAME, EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE
    );
    
    public static boolean shouldTryGenAttr(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem;
    }

    public static NbtCompound createAttributesSubNbt(NbtCompound root) {
        NbtCompound res = new NbtCompound();
        root.put(NBT_KEY, res);
        return res;
    }

    public static NbtCompound createAttributesSubNbtReturnRoot(NbtCompound root) {
        NbtCompound res = new NbtCompound();
        root.put(NBT_KEY, res);
        return root;
    }

    public static Optional<NbtCompound> getSAANbtIfPresent(ItemStack stack) {
        if (stack.hasNbt()) {
            NbtCompound root = stack.getNbt();

            if (root != null && root.contains(SpoornArmorAttributesUtil.NBT_KEY)) {
                return Optional.of(root.getCompound(SpoornArmorAttributesUtil.NBT_KEY));
            }
        }
        return Optional.empty();
    }

    public static boolean hasSAANbt(ItemStack stack) {
        return stack.hasNbt() && stack.getNbt().contains(NBT_KEY);
    }

    public static boolean isRerollItem(ItemStack stack) {
        String rerollItem = ModConfig.get().rerollItem;
        Optional<Item> item = Registry.ITEM.getOrEmpty(new Identifier(rerollItem));
        if (item.isEmpty()) {
            throw new RuntimeException("Reroll item " + rerollItem + " was not found in the registry!");
        }
        return stack.getItem().equals(item.get());
    }

    public static boolean isUpgradeItem(ItemStack stack) {
        String upgradeItem = ModConfig.get().upgradeItem;
        Optional<Item> item = Registry.ITEM.getOrEmpty(new Identifier(upgradeItem));
        if (item.isEmpty()) {
            throw new RuntimeException("Upgrade item " + upgradeItem + " was not found in the registry!");
        }
        return stack.getItem().equals(item.get());
    }

    /**
     * Assumes chance is between 0.0 and 1.0.
     */
    public static boolean shouldEnable(float chance) {
        return (chance > 0) && (RANDOM.nextFloat() < chance);
    }

    public static boolean shouldEnable(double chance) {
        return (chance > 0) && (RANDOM.nextDouble() < chance);
    }

    public static double getRandomInRange(double min, double max) {
        return RANDOM.nextFloat() * (max - min) + min;
    }

    public static int getRandomInRange(int min, int max) {
        return Math.round(RANDOM.nextFloat() * (max - min) + min);
    }

    public static float drawRandom(boolean useGaussian, double mean, double sd, double min, double max) {
        if (useGaussian) {
            return (float) getNextGaussian(mean, sd, min, max);
        } else {
            return (float) getRandomInRange(min, max);
        }
    }

    // Assumes parameters are correct
    public static double getNextGaussian(double mean, double sd, double min, double max) {
        double nextGaussian = RANDOM.nextGaussian() * sd + mean;
        if (nextGaussian < min) {
            nextGaussian = min;
        } else if (nextGaussian > max) {
            nextGaussian = max;
        }
        return nextGaussian;
    }

    public static void rollOrUpgradeNbt(NbtCompound root) {
        if (root.getBoolean(UPGRADE_NBT_KEY)) {
            SpoornArmorAttributesUtil.upgradeAttributes(root);
            root.remove(UPGRADE_NBT_KEY);
        } else if (root.getBoolean(REROLL_NBT_KEY)) {
            // Technically this is redundant as AnvilScreenHandlerMixin removes the NBT_KEY causing a reroll as soon as the item ticks in an inventory
            SpoornArmorAttributesUtil.rollAttributes(root);     
            root.remove(REROLL_NBT_KEY);
        }
    }

    // Apply attributes
    public static void rollAttributes(NbtCompound root) {
        if (!root.contains(SpoornArmorAttributesUtil.NBT_KEY)) {
            NbtCompound nbt = SpoornArmorAttributesUtil.createAttributesSubNbt(root);

            for (Map.Entry<String, Attribute> entry : Attribute.VALUES.entrySet()) {
                String name = entry.getKey();
                Attribute att = entry.getValue();

                if (SpoornArmorAttributesUtil.shouldEnable(att.chance)) {
                    NbtCompound newNbt = new NbtCompound();
                    switch (name) {
                        case Attribute.MAX_HEALTH_NAME:
                            newNbt.putFloat(BONUS_MAX_HEALTH, Roller.rollMaxHealth());
                            break;
                        case Attribute.DMG_REDUCTION_NAME:
                            newNbt.putFloat(DMG_REDUCTION, Roller.rollDmgReduction());
                            break;
                        case Attribute.MOVEMENT_SPEED_NAME:
                            newNbt.putFloat(MOVEMENT_SPEED, Roller.rollMovementSpeed());
                            break;
                        case Attribute.KNOCKBACK_RESISTANCE_NAME:
                            newNbt.putFloat(KNOCKBACK_RESISTANCE, Roller.rollKnockbackResistance());
                            break;
                        case Attribute.THORNS_NAME:
                            newNbt.putFloat(THORNS, Roller.rollThorns());
                            break;
                        default:
                            // do nothing
                            log.error("Unknown SpoornArmorAttribute: {}", name);
                    }
                    nbt.put(name, newNbt);
                }
            }
        }
    }

    // Upgrade stats if applicable
    public static void upgradeAttributes(NbtCompound root) {
        if (!root.contains(SpoornArmorAttributesUtil.NBT_KEY)) {
            SpoornArmorAttributesUtil.createAttributesSubNbtReturnRoot(root);
        }
        NbtCompound nbt = root.getCompound(SpoornArmorAttributesUtil.NBT_KEY);

        for (Map.Entry<String, Attribute> entry : Attribute.VALUES.entrySet()) {
            String name = entry.getKey();
            Attribute att = entry.getValue();

            if (SpoornArmorAttributesUtil.shouldEnable(att.chance)) {
                NbtCompound newNbt = nbt.contains(name) ? nbt.getCompound(name) : new NbtCompound();

                switch (name) {
                    case Attribute.MAX_HEALTH_NAME:
                        checkFloatUpgradeThenAdd(newNbt, BONUS_MAX_HEALTH, Roller.rollMaxHealth());
                        break;
                    case Attribute.DMG_REDUCTION_NAME:
                        checkFloatUpgradeThenAdd(newNbt, DMG_REDUCTION, Roller.rollDmgReduction());
                        break;
                    case Attribute.MOVEMENT_SPEED_NAME:
                        checkFloatUpgradeThenAdd(newNbt, MOVEMENT_SPEED, Roller.rollMovementSpeed());
                        break;
                    case Attribute.KNOCKBACK_RESISTANCE_NAME:
                        checkFloatUpgradeThenAdd(newNbt, KNOCKBACK_RESISTANCE, Roller.rollKnockbackResistance());
                        break;
                    case Attribute.THORNS_NAME:
                        checkFloatUpgradeThenAdd(newNbt, THORNS, Roller.rollThorns());
                        break;
                    default:
                        // do nothing
                        log.error("Unknown SpoornArmorAttribute: {}", name);
                }
                nbt.put(name, newNbt);
            }
        }
    }

    private static void checkFloatUpgradeThenAdd(NbtCompound nbt, String attribute, float newValue) {
        if (!nbt.contains(attribute) || nbt.getFloat(attribute) < newValue) {
            nbt.putFloat(attribute, newValue);
        }
    }
}
