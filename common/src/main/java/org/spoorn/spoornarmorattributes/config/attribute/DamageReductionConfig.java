package org.spoorn.spoornarmorattributes.config.attribute;

import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

public class DamageReductionConfig {

    @Comment("Chance for an armor item to have damage reduction [0 = never, 1 = always] [default = 0.05]\n" +
            "This applies damage reduction to the FINAL damage taken, which means after all other effects such as enchantments have applied.\n" +
            "Note: damage reduction does not stack linearly, but rather exponential.  For example, if you\n" +
            "\thave one armor at 50% dmg reduction, and another at 50% dmg reduction, you don't have 100% dmg reduction.\n" +
            "\tInstead, it would reduce the final damage you take by 50% once, then 50% again effectively giving 75% dmg reduction.")
    public double attributeChance = 0.05;

    @Comment("Minimum damage percentage [default = 5]")
    public float minDmgReduction = 5;

    @Comment("Maximum damage percentage] [default = 90]")
    public float maxDmgReduction = 90;

    @Comment("True if damage reduction should be calculated using a Gaussian distribution, else it will be a linearly random\n" +
            "value between the minDmgReduction and maxDmgReduction [default = true]")
    public boolean useGaussian = true;

    // The default mean and sd makes it so  there's a ~15% chance of getting above 25 dmg reduction, 0.04% of above 50
    // Use https://onlinestatbook.com/2/calculators/normal_dist.html
    @Comment("Average damage reduction rate [default = 10]")
    public float mean = 10;

    @Comment("Standard deviation for the distribution [default = 12]")
    public double standardDeviation = 12;
}
