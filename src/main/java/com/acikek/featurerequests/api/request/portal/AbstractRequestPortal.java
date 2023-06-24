package com.acikek.featurerequests.api.request.portal;

import com.acikek.featurerequests.api.request.event.FeatureRequestEvent;
import com.acikek.featurerequests.api.request.result.FeatureRequests;
import com.google.common.base.Joiner;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public abstract class AbstractRequestPortal<T, R extends FeatureRequests<?>> implements FeatureRequestPortal<T, R> {

    private final String name;
    protected final Map<T, R> requests = new HashMap<>();
    private final Map<Identifier, T> holders;

    private FeatureRequestEvent event = null;

    public AbstractRequestPortal(String name, Map<Identifier, T> holders) {
        Stream.of(name, holders).forEach(Objects::requireNonNull);
        this.name = name;
        this.holders = holders;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public FeatureRequestEvent event() {
        return event;
    }

    @Override
    public void setEvent(FeatureRequestEvent event) {
        FeatureRequestPortal.super.setEvent(event);
        this.event = event;
    }

    @Override
    public Map<T, R> requests() {
        return requests;
    }

    @Override
    public @Nullable Map<Identifier, T> holders() {
        return holders;
    }

    @Override
    public String toString() {
        return requests().toString();
    }
}
