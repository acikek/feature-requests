package com.acikek.featurerequests.api.request.result;

import com.acikek.featurerequests.api.request.portal.SingleRequestPortal;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashSet;
import java.util.Set;

/**
 * A structure containing the results of a {@link SingleRequestPortal}.<br>
 * To check if a feature is requested, use {@link SingleFeatureRequests#contains(Identifier)}.<br>
 * It is not necessary to manually check {@link FeatureRequests#all()}, as the {@code contains} method does so on its own.
 */
public final class SingleFeatureRequests extends FeatureRequests<Set<Identifier>> {

    @ApiStatus.Internal
    public SingleFeatureRequests(boolean all, Set<Identifier> requests) {
        super(all, requests);
    }

    @ApiStatus.Internal
    public static SingleFeatureRequests emptyWithAll(boolean all) {
        return new SingleFeatureRequests(all, new HashSet<>());
    }

    @ApiStatus.Internal
    public static SingleFeatureRequests empty() {
        return emptyWithAll(false);
    }

    @ApiStatus.Internal
    public static SingleFeatureRequests useAll() {
        return emptyWithAll(true);
    }

    /**
     * @return whether the specified feature was requested
     */
    public boolean contains(Identifier feature) {
        return all() || requests().contains(feature);
    }

    @Override
    public String toString() {
        return "single" + getString();
    }
}
