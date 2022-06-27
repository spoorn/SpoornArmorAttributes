package org.spoorn.spoornarmorattributes.client;

import lombok.extern.log4j.Log4j2;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Log4j2
@Environment(EnvType.CLIENT)
public class SpoornArmorAttributesClient {
    
    public static void init() {
        log.info("Hello client from SpoornArmorAttributes!");
    }
}
