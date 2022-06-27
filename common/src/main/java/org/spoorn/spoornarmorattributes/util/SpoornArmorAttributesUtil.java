package org.spoorn.spoornarmorattributes.util;

import lombok.extern.log4j.Log4j2;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spoorn.spoornarmorattributes.att.Attribute;
import org.spoorn.spoornarmorattributes.config.ModConfig;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Log4j2
public class SpoornArmorAttributesUtil {

    public static final String NBT_KEY = "saa3";
    public static final String REROLL_NBT_KEY = "saa3_reroll";
    public static final String UPGRADE_NBT_KEY = "saa3_upgrade";
    public static final Random RANDOM = new Random();

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

    public static float getRandomInRange(float min, float max) {
        return RANDOM.nextFloat() * (max - min) + min;
    }

    public static int getRandomInRange(int min, int max) {
        return Math.round(RANDOM.nextFloat() * (max - min) + min);
    }

    public static float drawRandom(boolean useGaussian, float mean, double sd, float min, float max) {
        if (useGaussian) {
            return (float) getNextGaussian(mean, sd, min, max);
        } else {
            return getRandomInRange(min, max);
        }
    }

    // Assumes parameters are correct
    public static double getNextGaussian(float mean, double sd, float min, float max) {
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
