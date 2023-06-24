package com.acikek.featurerequests;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class FeatureRequestsMod implements ModInitializer {

    @Override
    public void onInitialize() {
        var event = new TestRequestEvent();
        event.digits().submit(10, new Identifier("test:multiply"));
        event.digits().all(100);
        event.floats().submit(0.5f, new Identifier("test:trim"), new Identifier("test:yes"));
        System.out.println(event.digits());
        var requests = event.floats().getRequests(0.5f).getMapped(new Identifier("test:trim"));
        System.out.println(requests);
    }
}
