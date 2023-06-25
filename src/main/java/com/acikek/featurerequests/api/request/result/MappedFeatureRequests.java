package com.acikek.featurerequests.api.request.result;

import com.acikek.featurerequests.api.request.portal.MappedRequestPortal;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * A structure containing the results of a {@link MappedRequestPortal}.<br>
 * Retrieve a single mapping container with the {@link MappedFeatureRequests#getMapped(K)} method.
 * @param <K> the mapping key type used by the request portal
 * @see SingleFeatureRequests
 */
public final class MappedFeatureRequests<K> extends FeatureRequests<Map<K, SingleFeatureRequests>> {

    @ApiStatus.Internal
    public MappedFeatureRequests(boolean all, Map<K, SingleFeatureRequests> requests) {
        super(all, requests);
    }

    @ApiStatus.Internal
    public static <K> MappedFeatureRequests<K> emptyWithAll(boolean all) {
        return new MappedFeatureRequests<>(all, new HashMap<>());
    }

    @ApiStatus.Internal
    public static <K> MappedFeatureRequests<K> empty() {
        return emptyWithAll(false);
    }

    @ApiStatus.Internal
    public static <K> MappedFeatureRequests<K> useAll() {
        return emptyWithAll(true);
    }

    /**
     * @return a single request container based on the specified mapping key
     */
    public SingleFeatureRequests getMapped(K key) {
        var mapped = requests().getOrDefault(key, SingleFeatureRequests.empty());
        return new SingleFeatureRequests(all() || mapped.all(), mapped.requests());
    }

    @Override
    public String toString() {
        return "mapped" + getString();
    }
}
