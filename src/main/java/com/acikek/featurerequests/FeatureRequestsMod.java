package com.acikek.featurerequests;

import com.acikek.featurerequests.api.impl.loading.FeatureRequestsLoading;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FeatureRequestsMod implements ModInitializer {

    public static final String ID = "feature_requests";

    public static final Logger LOGGER = LogManager.getLogger(ID);

    @Override
    public void onInitialize() {
        /*var event = new TestRequestEvent();
        event.digits().submit(10, new Identifier("test:multiply"));
        event.digits().all(100);
        event.floats().submit(0.5f, new Identifier("test:trim"), new Identifier("test:yes"));
        System.out.println(event.digits());
        var requests = event.floats().getRequests(0.5f).getMapped(new Identifier("test:trim"));
        System.out.println(requests);*/
        FeatureRequestsLoading.load();
    }
}
