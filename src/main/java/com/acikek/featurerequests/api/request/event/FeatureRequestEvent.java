package com.acikek.featurerequests.api.request.event;

import com.acikek.featurerequests.api.request.portal.FeatureRequestPortal;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

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
}
