package com.acikek.featurerequests.api.request.portal;

import com.acikek.featurerequests.api.request.result.MappedFeatureRequests;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public interface MappedRequestPortal<T, K> extends FeatureRequestPortal<T, MappedFeatureRequests<K>> {

    /**
     * Submits a set of requests to a specific mapping key in the specified content holder.
     */
    void submit(T holder, K key, Collection<Identifier> requests);

    /**
     * @see MappedRequestPortal#submit(Object, Object, Collection)
     */
    default void submit(T holder, K key, Identifier... requests) {
        submit(holder, key, Arrays.stream(requests).collect(Collectors.toSet()));
    }

    /**
     * @see MappedRequestPortal#submit(Object, Object, Collection)
     */
    default void submit(T holder, K key, Identifier request) {
        submit(holder, key, Collections.singleton(request));
    }

    /**
     * Submits requests for all features to a specific mapping key in the specified content holders.
     */
    void all(Collection<T> holders, K key);

    /**
     * @see MappedRequestPortal#all(Collection, Object)
     */
    @SuppressWarnings("unchecked")
    default void all(K key, T... holders) {
        all(Arrays.stream(holders).toList(), key);
    }

    /**
     * @see MappedRequestPortal#all(Collection, Object)
     */
    default void all(T holder, K key) {
        all(Collections.singletonList(holder), key);
    }

    /**
     * Submits requests for all features to a specific mapping key in all known content holders.
     */
    default void allForKey(K key) {
        all(holders().values(), key);
    }

    /**
     * @return a mapping key converted from a JSON element
     * @throws JsonSyntaxException if the conversion fails
     */
    K mappingFromJson(JsonElement element);

    @Override
    default MappedFeatureRequests<K> getRequests(T holder) {
        return requests().getOrDefault(holder, MappedFeatureRequests.empty());
    }
}
