package com.acikek.featurerequests.api.request.portal;

import com.acikek.featurerequests.api.request.result.MappedFeatureRequests;
import com.acikek.featurerequests.api.request.result.SingleFeatureRequests;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Map;

public abstract class AbstractMappedRequestPortal<T, K>
        extends AbstractRequestPortal<T, MappedFeatureRequests<K>>
        implements MappedRequestPortal<T, K> {

    public AbstractMappedRequestPortal(String name, Map<Identifier, T> holders) {
        super(name, holders);
    }

    private MappedFeatureRequests<K> getMapped(T holder) {
        return requests.computeIfAbsent(holder, k -> MappedFeatureRequests.empty());
    }

    @Override
    public void submit(T holder, K key, Collection<Identifier> requests) {
        var features = getMapped(checkHolder(holder))
                .requests()
                .computeIfAbsent(key, k -> SingleFeatureRequests.empty());
        features.requests().addAll(requests);
    }

    @Override
    public void all(Collection<T> holders, K key) {
        for (var holder : holders) {
            getMapped(checkHolder(holder)).requests().put(key, SingleFeatureRequests.useAll());
        }
    }

    @Override
    public void all(Collection<T> holders) {
        for (var holder : holders) {
            requests.put(checkHolder(holder), MappedFeatureRequests.useAll());
        }
    }
}
