package com.acikek.featurerequests.api.request.portal;

import com.acikek.featurerequests.api.impl.request.portal.MappedRequestPortalImpl;
import com.acikek.featurerequests.api.impl.request.portal.SingleRequestPortalImpl;
import com.google.gson.JsonElement;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class RequestPortals {

    private RequestPortals() {}

    public static <T> Map<Identifier, T> createMap(Collection<T> holders, Function<T, Identifier> idMapper) {
        return holders.stream()
                .map(holder -> new Pair<>(idMapper.apply(holder), holder))
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }

    public static <T> SingleRequestPortal<T> single(String name, Map<Identifier, T> holders) {
        return new SingleRequestPortalImpl<>(name, holders);
    }

    public static <T, K> MappedRequestPortal<T, K> mapped(String name, Map<Identifier, T> holders, Function<JsonElement, K> deserializer) {
        return new MappedRequestPortalImpl<>(name, holders, deserializer);
    }

    public static <T> MappedRequestPortal<T, Identifier> idMapped(String name, Map<Identifier, T> holders) {
        return new MappedRequestPortalImpl<>(name, holders, element -> Identifier.tryParse(element.getAsString()));
    }
}
