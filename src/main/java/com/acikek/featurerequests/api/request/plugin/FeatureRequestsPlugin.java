package com.acikek.featurerequests.api.request.plugin;

import com.acikek.featurerequests.api.request.event.FeatureRequestEvent;
import com.google.common.collect.Iterables;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface FeatureRequestsPlugin {

    Collection<FeatureRequestEvent> events();

    @ApiStatus.OverrideOnly
    default void onLoad() {
        // Empty
    }

    @ApiStatus.OverrideOnly
    default void afterLoad() {
        // Empty
    }

    default @Nullable FeatureRequestEvent mainEvent() {
        return events().size() == 1
                ? Iterables.get(events(), 0)
                : null;
    }

    @ApiStatus.Internal
    default void init() {
        events().forEach(FeatureRequestEvent::init);
    }
}
