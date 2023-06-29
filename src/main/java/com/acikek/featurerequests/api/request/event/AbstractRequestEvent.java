package com.acikek.featurerequests.api.request.event;

import com.acikek.featurerequests.api.request.plugin.FeatureRequestsPlugin;

public abstract class AbstractRequestEvent implements FeatureRequestEvent {

    private FeatureRequestsPlugin plugin;

    @Override
    public String toString() {
        return portalMap().toString();
    }

    @Override
    public FeatureRequestsPlugin plugin() {
        return plugin;
    }

    @Override
    public void setPlugin(FeatureRequestsPlugin plugin) {
        FeatureRequestEvent.super.setPlugin(plugin);
        this.plugin = plugin;
    }
}
