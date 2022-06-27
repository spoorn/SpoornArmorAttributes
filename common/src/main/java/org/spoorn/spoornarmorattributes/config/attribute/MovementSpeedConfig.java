package org.spoorn.spoornarmorattributes.config.attribute;

import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

public class MovementSpeedConfig {

    @Comment("Chance for an armor item to have movement speed boost [0 = never, 1 = always] [default = 0.05]")
    public double attributeChance = 0.05;

    @Comment("Minimum movement speed boost (for reference, vanilla default is 0.7) [default = 0.01]\n" +
            "Note: The tooltip will show movementSpeed * 10, so it looks like a nicer integer value.")
    public double minSpeed = 0.01;

    @Comment("Maximum movement speed boost (for reference, vanilla default is 0.7) [default = 0.1]")
    public double maxSpeed = 0.1;

    @Comment("True if movement speed boost should be calculated using a Gaussian distribution, else it will be a linearly random\n" +
            "value between the minSpeed and maxSpeed [default = true]")
    public boolean useGaussian = true;

    // The default mean and sd makes it so  there's a ~0.4% chance of getting above 0.1 movement speed
    // Use https://onlinestatbook.com/2/calculators/normal_dist.html
    @Comment("Average movement speed boost [default = 0.02]")
    public double mean = 0.02;

    @Comment("Standard deviation for the distribution [default = 0.02]")
    public double standardDeviation = 0.02;
}
