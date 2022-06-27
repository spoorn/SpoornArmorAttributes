package org.spoorn.spoornarmorattributes;

import lombok.extern.log4j.Log4j2;
import org.spoorn.spoornarmorattributes.att.Attribute;
import org.spoorn.spoornarmorattributes.config.ModConfig;

@Log4j2
public class SpoornArmorAttributes {

    public static final String MODID = "spoornarmorattributes";
    
    public static void init() {
        log.info("Hello from SpoornArmorAttributes!");

        // Config
        ModConfig.init();

        // Attribute registry
        Attribute.init();
    }
}
