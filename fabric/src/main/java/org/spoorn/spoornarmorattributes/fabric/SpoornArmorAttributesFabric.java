package org.spoorn.spoornarmorattributes.fabric;

import net.fabricmc.api.ModInitializer;
import org.spoorn.spoornarmorattributes.SpoornArmorAttributes;

public class SpoornArmorAttributesFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        SpoornArmorAttributes.init();
    }
}
