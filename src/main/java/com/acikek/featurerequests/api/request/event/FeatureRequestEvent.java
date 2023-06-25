package com.acikek.featurerequests.api.request.event;

import com.acikek.featurerequests.api.request.portal.FeatureRequestPortal;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public interface FeatureRequestEvent {

    Identifier id();

    Collection<FeatureRequestPortal<?, ?>> portals();

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
    default JsonObject createDefaultObject(boolean enabled) {
        var obj = new JsonObject();
        for (var portal : portals()) {
            obj.add(portal.name(), new JsonPrimitive(enabled));
        }
        return obj;
    }
}
