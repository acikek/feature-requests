package com.acikek.featurerequests.api.request.portal;

import com.acikek.featurerequests.api.impl.request.portal.MappedRequestPortalImpl;
import com.acikek.featurerequests.api.impl.request.portal.SingleRequestPortalImpl;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility methods for creating {@link FeatureRequestPortal}s.
 */
public final class RequestPortals {

    private RequestPortals() {}

    /**
     * Creates a map suitable for {@link FeatureRequestPortal#holders()} based on a collection of holder
     * instances and a conversion function.
     * @param holders the holder instances to add to a portal
     * @param idFunction the function to convert the holders to {@link Identifier}s
     * @return the resulting map
     */
    public static <T> Map<Identifier, T> createMap(Collection<T> holders, Function<T, Identifier> idFunction) {
        return holders.stream()
                .map(holder -> new Pair<>(idFunction.apply(holder), holder))
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }

    /**
     * Creates a {@link SingleRequestPortal}.
     * @param name see {@link FeatureRequestPortal#name()}
     * @param defaultEnabled see {@link FeatureRequestPortal#isDefaultEnabled()}
     * @param holders see {@link FeatureRequestPortal#holders()}
     * @return the resulting portal
     */
    public static <T> SingleRequestPortal<T> single(String name, boolean defaultEnabled, Map<Identifier, T> holders) {
        return new SingleRequestPortalImpl<>(name, defaultEnabled, holders);
    }

    /**
     * @see RequestPortals#single(String, boolean, Map)
     */
    public static <T> SingleRequestPortal<T> single(String name, Map<Identifier, T> holders) {
        return single(name, false, holders);
    }

    /**
     * Creates a {@link MappedRequestPortal}.
     * @param deserializer a deserialization function for {@link MappedRequestPortal#mappingFromJson(String)}
     * @return the resulting portal
     * @see RequestPortals#single(String, boolean, Map)
     */
    public static <T, K> MappedRequestPortal<T, K> mapped(String name, boolean defaultEnabled, Map<Identifier, T> holders, Function<String, K> deserializer) {
        return new MappedRequestPortalImpl<>(name, defaultEnabled, holders, deserializer);
    }

    /**
     * @see RequestPortals#mapped(String, boolean, Map, Function)
     * @see RequestPortals#single(String, boolean, Map)
     */
    public static <T, K> MappedRequestPortal<T, K> mapped(String name, Map<Identifier, T> holders, Function<String, K> deserializer) {
        return mapped(name, false, holders, deserializer);
    }

    /**
     * @return a {@link MappedRequestPortal} with the mapping keys being {@link Identifier} instances
     * @see RequestPortals#mapped(String, boolean, Map, Function)
     * @see RequestPortals#single(String, boolean, Map)
     */
    public static <T> MappedRequestPortal<T, Identifier> idMapped(String name, boolean defaultEnabled, Map<Identifier, T> holders) {
        return new MappedRequestPortalImpl<>(name, defaultEnabled, holders, Identifier::new);
    }

    /**
     * @see RequestPortals#idMapped(String, boolean, Map)
     * @see RequestPortals#mapped(String, boolean, Map, Function)
     * @see RequestPortals#single(String, boolean, Map)
     */
    public static <T> MappedRequestPortal<T, Identifier> idMapped(String name, Map<Identifier, T> holders) {
        return idMapped(name, false, holders);
    }
}
