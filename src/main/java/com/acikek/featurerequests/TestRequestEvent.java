package com.acikek.featurerequests;

import com.acikek.featurerequests.api.request.event.AbstractRequestEvent;
import com.acikek.featurerequests.api.request.portal.MappedRequestPortal;
import com.acikek.featurerequests.api.request.portal.FeatureRequestPortal;
import com.acikek.featurerequests.api.request.portal.RequestPortals;
import com.acikek.featurerequests.api.request.portal.SingleRequestPortal;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class TestRequestEvent extends AbstractRequestEvent {

    @Override
    public Identifier id() {
        return new Identifier("test:numbers");
    }

    private final SingleRequestPortal<Integer> digits = RequestPortals.single("digits", Map.of(
            new Identifier("test:ten"), 10,
            new Identifier("test:one_thousand"), 100
    ));

    private final MappedRequestPortal<Float, Identifier> floats = RequestPortals.idMapped("floats", Map.of(
            new Identifier("test:one_point_two"), 1.2f,
            new Identifier("test:half"), 0.5f
    ));

    public SingleRequestPortal<Integer> digits() {
        return digits;
    }

    public MappedRequestPortal<Float, Identifier> floats() {
        return floats;
    }

    @Override
    public Collection<FeatureRequestPortal<?, ?>> portals() {
        return List.of(digits, floats);
    }
}
