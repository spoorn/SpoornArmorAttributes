package org.spoorn.spoornarmorattributes.forge;

import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkConstants;
import org.spoorn.spoornarmorattributes.SpoornArmorAttributes;
import org.spoorn.spoornarmorattributes.forge.client.SpoornArmorAttributesClientForge;

@Mod(SpoornArmorAttributes.MODID)
public class SpoornArmorAttributesForge {

    public SpoornArmorAttributesForge() {
        // Submit our event bus to let architectury register our content on the right time
        //EventBuses.registerModEventBus(ClimbLaddersFast.MODID, FMLJavaModLoadingContext.get().getModEventBus());
        //Make sure the mod being absent on the other network side does not cause the client to display the server as incompatible
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
        SpoornArmorAttributes.init();

        // Client
        SpoornArmorAttributesClientForge.init();
    }
}
