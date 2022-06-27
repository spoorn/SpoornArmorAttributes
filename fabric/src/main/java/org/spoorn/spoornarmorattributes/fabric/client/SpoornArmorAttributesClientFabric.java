package org.spoorn.spoornarmorattributes.fabric.client;

import lombok.extern.log4j.Log4j2;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spoorn.spoornarmorattributes.client.SpoornArmorAttributesClient;

@Log4j2
@Environment(EnvType.CLIENT)
public class SpoornArmorAttributesClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        SpoornArmorAttributesClient.init();
    }
}
