package com.acikek.featurerequests.api.request.event;

import com.acikek.featurerequests.api.request.plugin.FeatureRequestsPlugin;
import com.acikek.featurerequests.api.request.portal.FeatureRequestPortal;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A container linking to {@link FeatureRequestPortal}s to be ferried over to end users. Basically a data model
 * for request portals.<br>
 *
 * To include an event in Feature Requests' config loading stage, create a {@link FeatureRequestsPlugin} and add
 * the event to its {@link FeatureRequestsPlugin#events()}.<br>
 *
 * There should only ever be one instance of an event implementation.
 * Implementors should pass that instance around in code, preferably in the {@link FeatureRequestsPlugin#afterLoad()}
 * stage where all portals have been modified by their config files.
 */
public interface FeatureRequestEvent {

    /**
     * @return the ID of the event. This determines its file path within the Feature Requests config folder
     * ({@code config/feature_requests}. If the parent {@link FeatureRequestsPlugin} contains more than one event,
     *
     */
    default String name() {
        return null;
    }

    Collection<FeatureRequestPortal<?, ?>> portals();

    /**
     * @return the plugin containing this request event
     */
    @ApiStatus.OverrideOnly
    FeatureRequestsPlugin plugin();

    /**
     * Sets the request event's {@link FeatureRequestEvent#plugin()}.
     * @throws IllegalStateException if the plugin is already set
     */
    @ApiStatus.OverrideOnly
    default void setPlugin(FeatureRequestsPlugin plugin) {
        if (plugin() != null) {
            throw new IllegalStateException("plugin already set for request event " + verbose());
        }
    }

    default void all() {
        for (var portal : portals()) {
            portal.all();
        }
    }

    default Map<String, FeatureRequestPortal<?, ?>> portalMap() {
        return portals().stream()
                .map(portal -> new Pair<>(portal.name(), portal))
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }

    @ApiStatus.Internal
    default void init() {
        for (var portal : portals()) {
            portal.setEvent(this);
        }
    }

    @ApiStatus.Internal
    default JsonObject createDefaultObject() {
        var obj = new JsonObject();
        for (var portal : portals()) {
            obj.add(portal.name(), new JsonPrimitive(portal.isDefaultEnabled()));
        }
        return obj;
    }

    default String verbose() {
        return "'" + name() + "' (plugin: " + plugin().namespace() + ")";
    }
}
