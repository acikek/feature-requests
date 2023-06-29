package com.acikek.featurerequests.api.request.portal;

import com.acikek.featurerequests.api.request.result.MappedFeatureRequests;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * A request portal wherein users can request features from a content holder's keys.
 * Therefore, the requests for a content holder in this portal are <b>mapped</b> by a specific key.<br>
 *
 * Content holders are represented in JSON by their {@link Identifier} in the {@link FeatureRequestPortal#holders()} map
 * and deserialized by referencing this map. The key type {@code K} is flexible, but a mapped request portal must be
 * provided a deserialization function (from a {@link String}) called in {@link MappedRequestPortal#mappingFromJson(String)}.
 *
 * @param <K> the deserializable key type
 */
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
     * @return a mapping key converted from a JSON string
     * @throws JsonSyntaxException if the conversion fails
     */
    K mappingFromJson(String mapping);
}
