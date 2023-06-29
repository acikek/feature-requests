package com.acikek.featurerequests.api.request.portal;

import com.acikek.featurerequests.api.request.event.FeatureRequestEvent;
import com.acikek.featurerequests.api.request.plugin.FeatureRequestsPlugin;
import com.acikek.featurerequests.api.request.result.FeatureRequests;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * A structure that a user can submit various feature requests to
 * which are then stored in {@link FeatureRequests} containers.<br>
 *
 * Users can submit individual requests for objects called 'content holders' with the {@code submit} method family.
 * They can additionally blanketly request all features for content holders
 * with the {@link FeatureRequestPortal#all()} method family.<br>
 *
 * All request portals are guaranteed to have <b>known</b> content {@link FeatureRequestPortal#holders()}.
 * Users can only submit requests to these content holders.<br>
 *
 * Requests are designed with deserialization in mind. Therefore, all request portals have a {@link FeatureRequestPortal#name()}
 * and all of their content holders are mapped to {@link Identifier}s. Implementations of the base request
 * portal may have additional serialization requirements.<br>
 *
 * The requests for a specific content holder can be queried with {@link FeatureRequestPortal#getRequests(T)}.
 *
 * @param <T> the content holder type
 * @param <R> the {@link FeatureRequests} implementation type
 */
public interface FeatureRequestPortal<T, R extends FeatureRequests<?>> {

    /**
     * @return the id-safe name for this request portal
     */
    String name();

    /**
     * @return whether this request portal's content should be enabled by default
     */
    boolean isDefaultEnabled();

    /**
     * @return the event contianing this request portal
     */
    @ApiStatus.OverrideOnly
    FeatureRequestEvent event();

    /**
     * Sets the request portal's {@link FeatureRequestPortal#event()}.
     * @throws IllegalStateException if the event is already set
     */
    @ApiStatus.OverrideOnly
    default void setEvent(FeatureRequestEvent event) {
        if (event() != null) {
            throw new IllegalStateException("event already set for request portal " + verbose());
        }
    }

    /**
     * @return a map of content holders to request lists
     */
    Map<T, R> requests();

    /**
     * Adds a request to the {@link FeatureRequestPortal#requests()}.<br>
     * You should never construct and add request containers manually.
     * Instead, use the idiomatic {@code submit} and {@link FeatureRequestPortal#all()} method families.
     */
    @ApiStatus.OverrideOnly
    void addRequest(T holder, R requests);

    /**
     * @return a map of known applicable content holders
     * @see FeatureRequestPortal#isHolderPresent(T)
     */
    Map<Identifier, T> holders();

    /**
     * @return whether the specified content holder is present in {@link FeatureRequestPortal#holders()}
     */
    default boolean isHolderPresent(T holder) {
        return holders().containsValue(holder);
    }

    /**
     * Validates that a content holder is referenced in the {@link FeatureRequestPortal#holders()} map.
     * @return the holder, if successful
     * @throws IllegalStateException if the holder is not present
     */
    default T checkHolder(T holder) {
        if (isHolderPresent(holder)) {
            return holder;
        }
        throw new IllegalStateException("content holder '" + holder + "' does not exist in request portal " + verbose());
    }

    /**
     * Submits requests for all features to the specified content holders.
     */
    void all(Collection<T> holders);

    /**
     * @see SingleRequestPortal#all(Collection)
     */
    @SuppressWarnings("unchecked")
    default void all(T... holders) {
        all(Arrays.stream(holders).toList());
    }

    /**
     * @see SingleRequestPortal#all(Collection)
     */
    default void all(T holder) {
        all(Collections.singletonList(holder));
    }

    /**
     * Submits requests for all features to all known content holders.
     */
    void all();

    /**
     * @return submitted requests for the specified content holder
     */
    R getRequests(T holder);

    /**
     * @return verbose identifier including the {@link FeatureRequestPortal#name()},
     * {@link FeatureRequestEvent#name()}, and {@link FeatureRequestsPlugin#namespace()}
     */
    default String verbose() {
        return "'" + name() + "' (event: " + event().name() + ", plugin: " + event().plugin().namespace() + ")";
    }
}
