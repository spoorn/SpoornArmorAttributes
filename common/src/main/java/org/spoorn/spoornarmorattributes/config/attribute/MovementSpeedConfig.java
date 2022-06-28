package org.spoorn.spoornarmorattributes.config.attribute;

import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

public class MovementSpeedConfig {

    @Comment("Chance for an armor item to have movement speed boost [0 = never, 1 = always] [default = 0.05]\n" +
            "Bonus movement speed is a percentage applied linearly, meaning each armor item adds a flat % movement speed\n" +
            "from the original movement speed.  So if you have 4 armor items each 25%, you ultimately get 100% bonus movement speed.\n" +
            "Note: The tooltip will show movementSpeed * 10, so it looks like a nicer integer value.")
    public double attributeChance = 0.05;

    @Comment("Minimum movement speed boost percentage [default = 1]")
    public double minSpeed = 1;

    @Comment("Maximum movement speed boost percentage [default = 100]")
    public double maxSpeed = 100;

    @Comment("True if movement speed boost should be calculated using a Gaussian distribution, else it will be a linearly random\n" +
            "value between the minSpeed and maxSpeed [default = true]")
    public boolean useGaussian = true;

    // The default mean and sd makes it so  there's a ~0.1% chance of getting above 50% movement speed
    // Use https://onlinestatbook.com/2/calculators/normal_dist.html
    @Comment("Average movement speed boost [default = 5]")
    public double mean = 5;

    @Comment("Standard deviation for the distribution [default = 15]")
    public double standardDeviation = 15;
}
