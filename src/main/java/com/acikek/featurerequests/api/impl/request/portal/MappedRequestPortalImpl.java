package com.acikek.featurerequests.api.impl.request.portal;

import com.acikek.featurerequests.api.request.portal.AbstractMappedRequestPortal;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.function.Function;

public class MappedRequestPortalImpl<T, K> extends AbstractMappedRequestPortal<T, K> {

    private final Function<String, K> deserializer;

    public MappedRequestPortalImpl(String name, boolean defaultEnabled, Map<Identifier, T> holders, Function<String, K> deserializer) {
        super(name, defaultEnabled, holders);
        this.deserializer = deserializer;
    }

    @Override
    public K mappingFromJson(String mapping) {
        return deserializer.apply(mapping);
    }
}
