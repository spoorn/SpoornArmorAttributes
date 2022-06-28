package org.spoorn.spoornarmorattributes.config.attribute;

import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

public class KnockbackResistanceConfig {

    @Comment("Chance for an armor item to have knockback resistance [0 = never, 1 = always] [default = 0.05]\n" +
            "Knockback Resistance is a rate from 0 to 1, where 1 means 100% knockback resistance, 0.5 means 50%.\n" +
            "Note: The tooltip will show knockbackResistance * 100, so it looks like a nicer integer value.")
    public double attributeChance = 0.05;

    @Comment("Minimum knockback resistance (this is a rate from 0 to 1) [default = 0.01]")
    public double minKnockbackResistance = 0.01;

    @Comment("Maximum knockback resistance (this is a rate from 0 to 1) [default = 0.25]")
    public double maxKnockbackResistance = 0.25;

    @Comment("True if knockback resistance should be calculated using a Gaussian distribution, else it will be a linearly random\n" +
            "value between the minKnockbackResistance and maxKnockbackResistance [default = true]")
    public boolean useGaussian = true;

    // The default mean and sd makes it so  there's a ~16% chance of getting above 0.15 knockback resistance
    // Use https://onlinestatbook.com/2/calculators/normal_dist.html
    @Comment("Average knockback resistance [default = 0.05]")
    public double mean = 0.05;

    @Comment("Standard deviation for the distribution [default = 0.1]")
    public double standardDeviation = 0.1;
}
