package org.spoorn.spoornarmorattributes.forge.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.spoorn.spoornarmorattributes.client.SpoornArmorAttributesClient;

public class SpoornArmorAttributesClientForge {

    public static void init() {
        if (FMLEnvironment.dist != Dist.CLIENT) {
            return;
        }

        SpoornArmorAttributesClient.init();
    }
}
