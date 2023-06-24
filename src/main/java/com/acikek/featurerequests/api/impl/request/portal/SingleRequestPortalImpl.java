package com.acikek.featurerequests.api.impl.request.portal;

import com.acikek.featurerequests.api.request.portal.AbstractRequestPortal;
import com.acikek.featurerequests.api.request.portal.SingleRequestPortal;
import com.acikek.featurerequests.api.request.result.SingleFeatureRequests;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class SingleRequestPortalImpl<T>
        extends AbstractRequestPortal<T, SingleFeatureRequests>
        implements SingleRequestPortal<T> {

    public SingleRequestPortalImpl(String name, Map<Identifier, T> holders) {
        super(name, holders);
    }

    @Override
    public void submit(T holder, Set<Identifier> requests) {
        var features = this.requests.computeIfAbsent(checkHolder(holder), k -> SingleFeatureRequests.empty());
        features.requests().addAll(requests);
    }

    @Override
    public void all(Collection<T> holders) {
        for (var holder : holders) {
            requests.put(checkHolder(holder), SingleFeatureRequests.useAll());
        }
    }
}
