package org.spoorn.spoornarmorattributes.config.attribute;

import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

public class ThornsConfig {

    @Comment("Chance for an armor item to have thorns attribute. [0 = never, 1 = always] [default = 0.05]\n" +
            "Thorns returns damage back to the attacker.\n")
    public double attributeChance = 0.05;

    @Comment("Minimum thorns return damage percentage [default = 1]")
    public double minReturnDmg = 1;

    @Comment("Maximum thorns return damage percentage [default = 1000]")
    public double maxReturnDmg = 1000;

    @Comment("True if thorns return damage percentage should be calculated using a Gaussian distribution, else it will be a linearly random\n" +
            "value between the minReturnDmg and maxReturnDmg [default = true]")
    public boolean useGaussian = true;

    // The default mean and sd makes it so  there's a ~15% chance of getting above 25 return damage percentage, 1% for above 50
    // Use https://onlinestatbook.com/2/calculators/normal_dist.html
    @Comment("Average thorns return damage percentage [default = 5]")
    public double mean = 5;

    @Comment("Standard deviation for the distribution [default = 20]")
    public double standardDeviation = 25;
}
