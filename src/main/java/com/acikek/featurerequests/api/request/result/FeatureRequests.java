package com.acikek.featurerequests.api.request.result;

import com.acikek.featurerequests.api.request.portal.FeatureRequestPortal;
import org.jetbrains.annotations.ApiStatus;

/**
 * A structure containing the results of a {@link FeatureRequestPortal}.
 * @param <T> the {@link FeatureRequests#requests()} collection type
 * @see SingleFeatureRequests
 * @see MappedFeatureRequests
 */
public sealed class FeatureRequests<T> permits SingleFeatureRequests, MappedFeatureRequests {

    private final boolean all;
    private final T requests;

    @ApiStatus.Internal
    public FeatureRequests(boolean all, T requests) {
        this.all = all;
        this.requests = requests;
    }

    /**
     * @return whether <b>all</b> features were requested from the portal
     */
    public boolean all() {
        return all;
    }

    /**
     * @return a collection of requests from the portal
     */
    public T requests() {
        return requests;
    }

    protected String getString() {
        return all ? "(all)" : requests.toString();
    }
}
