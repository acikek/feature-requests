package com.acikek.featurerequests.api.request.portal;

import com.acikek.featurerequests.api.request.result.SingleFeatureRequests;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A request portal wherein users can request features from content holders directly.
 * Therefore, there is a <b>single</b> layer of abstraction.
 */
public interface SingleRequestPortal<T> extends FeatureRequestPortal<T, SingleFeatureRequests> {

    /**
     * Submits a set of requests to the specified content holder.
     */
    void submit(T holder, Set<Identifier> requests);

    /**
     * @see SingleRequestPortal#submit(T, Set)
     */
    default void submit(T holder, Identifier... requests) {
        submit(holder, Arrays.stream(requests).collect(Collectors.toSet()));
    }

    /**
     * @see SingleRequestPortal#submit(T, Set)
     */
    default void submit(T holder, Identifier request) {
        submit(holder, Collections.singleton(request));
    }
}
