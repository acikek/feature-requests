package com.acikek.featurerequests.api.impl.request.portal;

import com.acikek.featurerequests.api.request.portal.AbstractMappedRequestPortal;
import com.google.gson.JsonElement;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.function.Function;

public class MappedRequestPortalImpl<T, K> extends AbstractMappedRequestPortal<T, K> {

    private final Function<JsonElement, K> deserializer;

    public MappedRequestPortalImpl(String name, Map<Identifier, T> holders, Function<JsonElement, K> deserializer) {
        super(name, holders);
        this.deserializer = deserializer;
    }

    @Override
    public K mappingFromJson(JsonElement element) {
        return deserializer.apply(element);
    }
}
