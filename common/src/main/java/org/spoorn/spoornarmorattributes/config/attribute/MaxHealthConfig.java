package org.spoorn.spoornarmorattributes.config.attribute;

import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

public class MaxHealthConfig {
    
    @Comment("Chance for an armor item to add bonus max health [0 = never, 1 = always] [default = 0.05]")
    public double attributeChance = 0.05;
    
    @Comment("Set to true to round bonus health to a whole number, so hearts are filled up nicely on the HUD [default = true]\n" +
            "This means bonus health of +2.3 would round to just +2, or bonus health of +2.7 would round to +3")
    public boolean roundBonusHealth = true;

    @Comment("Minimum bonus health [default = 0]")
    public float minHealth = 0;

    @Comment("Maximum bonus health [2 health is 1 heart] [default = 20.0]")
    public float maxHealth = 20;

    @Comment("True if damage should be calculated using a Gaussian distribution, else it will be a linearly random\n" +
            "value between the minHealth and maxHealth [default = true]")
    public boolean useGaussian = true;

    // The default mean and sd makes it so  there's a ~11% chance of getting above 5 health
    // Use https://onlinestatbook.com/2/calculators/normal_dist.html
    @Comment("Average bonus health [default = 2]")
    public int mean = 2;

    @Comment("Standard deviation for the distribution [default = 2.5]")
    public double standardDeviation = 2.5;
}
