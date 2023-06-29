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
        int plugins = FeatureRequestsLoading.load();
        LOGGER.info("Feature Requests loaded with {} plugin(s) present", plugins);
    }
}
