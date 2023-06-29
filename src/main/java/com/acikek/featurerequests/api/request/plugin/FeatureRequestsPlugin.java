package com.acikek.featurerequests.api.request.plugin;

import com.acikek.featurerequests.api.request.event.FeatureRequestEvent;
import com.google.common.collect.Iterables;
import net.fabricmc.loader.api.ModContainer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * A plugin entrypoint for the <em>Feature Requests</em> library.<br>
 * Plugins can be registered under the {@code feature_requests} entrypoint key in a mod manifest.
 */
public abstract class FeatureRequestsPlugin {

    private String namespace = null;

    public String namespace() {
        return namespace;
    }

    public abstract Collection<FeatureRequestEvent> events();

    @ApiStatus.OverrideOnly
    public void onLoad() {
        // Empty
    }

    @ApiStatus.OverrideOnly
    public void afterLoad() {
        // Empty
    }

    public @Nullable FeatureRequestEvent mainEvent() {
        return events().size() == 1
                ? Iterables.get(events(), 0)
                : null;
    }

    @ApiStatus.Internal
    public void init(ModContainer mod) {
        events().forEach(FeatureRequestEvent::init);
        for (var event : events()) {
            event.setPlugin(this);
        }
        if (namespace() == null) {
            namespace = mod.getMetadata().getId();
        }
    }
}
