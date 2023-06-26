package com.acikek.featurerequests.api.request.portal;

import com.acikek.featurerequests.api.impl.request.portal.MappedRequestPortalImpl;
import com.acikek.featurerequests.api.impl.request.portal.SingleRequestPortalImpl;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class RequestPortals {

    private RequestPortals() {}

    public static <T> Map<Identifier, T> createMap(Collection<T> holders, Function<T, Identifier> idFn) {
        return holders.stream()
                .map(holder -> new Pair<>(idFn.apply(holder), holder))
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }

    public static <T> SingleRequestPortal<T> single(String name, boolean defaultEnabled, Map<Identifier, T> holders) {
        return new SingleRequestPortalImpl<>(name, defaultEnabled, holders);
    }

    public static <T> SingleRequestPortal<T> single(String name, Map<Identifier, T> holders) {
        return single(name, false, holders);
    }

    public static <T, K> MappedRequestPortal<T, K> mapped(String name, boolean defaultEnabled, Map<Identifier, T> holders, Function<String, K> deserializer) {
        return new MappedRequestPortalImpl<>(name, defaultEnabled, holders, deserializer);
    }

    public static <T, K> MappedRequestPortal<T, K> mapped(String name, Map<Identifier, T> holders, Function<String, K> deserializer) {
        return mapped(name, false, holders, deserializer);
    }

    public static <T> MappedRequestPortal<T, Identifier> idMapped(String name, boolean defaultEnabled, Map<Identifier, T> holders) {
        return new MappedRequestPortalImpl<>(name, defaultEnabled, holders, Identifier::new);
    }

    public static <T> MappedRequestPortal<T, Identifier> idMapped(String name, Map<Identifier, T> holders) {
        return idMapped(name, false, holders);
    }
}
