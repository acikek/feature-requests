package com.acikek.featurerequests.api.request.event;

public abstract class AbstractRequestEvent implements FeatureRequestEvent {

    @Override
    public String toString() {
        return this.getClass().getName() + portalMap().toString();
    }
}
