package com.acikek.featurerequests.api.request.portal;

import com.acikek.featurerequests.api.request.event.FeatureRequestEvent;
import com.acikek.featurerequests.api.request.result.FeatureRequests;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractRequestPortal<T, R extends FeatureRequests<?>> implements FeatureRequestPortal<T, R> {

    private final String name;
    private final boolean defaultEnabled;
    protected final Map<T, R> requests = new HashMap<>();
    private final Map<Identifier, T> holders;

    private FeatureRequestEvent event = null;
    protected boolean all = false;

    public AbstractRequestPortal(String name, boolean defaultEnabled, Map<Identifier, T> holders) {
        this.name = name;
        this.defaultEnabled = defaultEnabled;
        this.holders = holders;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean isDefaultEnabled() {
        return defaultEnabled;
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
    public void addRequest(T holder, R requests) {
        this.requests.put(holder, requests);
    }

    @Override
    public void all() {
        this.all = true;
    }

    @Override
    public @Nullable Map<Identifier, T> holders() {
        return holders;
    }

    protected abstract R emptyWithAll(boolean all);

    @Override
    public R getRequests(T holder) {
        return all ? emptyWithAll(true) : requests.getOrDefault(holder, emptyWithAll(false));
    }

    @Override
    public String toString() {
        return requests().toString();
    }
}
