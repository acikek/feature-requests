package com.acikek.featurerequests;

import com.acikek.featurerequests.api.request.event.FeatureRequestEvent;
import com.acikek.featurerequests.api.request.plugin.FeatureRequestsPlugin;

import java.util.Collection;
import java.util.List;

public class TestRequestPlugin extends FeatureRequestsPlugin {

    public static TestRequestEvent event = new TestRequestEvent();

    @Override
    public String namespace() {
        return "test";
    }

    @Override
    public void afterLoad() {
    }

    @Override
    public Collection<FeatureRequestEvent> events() {
        return List.of(event);
    }
}
