package com.acikek.featurerequests.api.impl.loading;

import com.acikek.featurerequests.FeatureRequestsMod;
import com.acikek.featurerequests.api.request.event.FeatureRequestEvent;
import com.acikek.featurerequests.api.request.plugin.FeatureRequestsPlugin;
import com.acikek.featurerequests.api.request.portal.FeatureRequestPortal;
import com.acikek.featurerequests.api.request.portal.MappedRequestPortal;
import com.acikek.featurerequests.api.request.portal.SingleRequestPortal;
import com.acikek.featurerequests.api.request.result.FeatureRequests;
import com.acikek.featurerequests.api.request.result.MappedFeatureRequests;
import com.acikek.featurerequests.api.request.result.SingleFeatureRequests;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

@ApiStatus.Internal
public class FeatureRequestsLoading {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Supplier<ImmutableList<FeatureRequestsPlugin>> PLUGINS = Suppliers.memoize(
            () -> ImmutableList.copyOf(
                    FabricLoader.getInstance().getEntrypoints(FeatureRequestsMod.ID, FeatureRequestsPlugin.class)
            )
    );

    public static void load() {
        var plugins = PLUGINS.get();
        plugins.forEach(FeatureRequestsPlugin::onLoad);
        plugins.forEach(FeatureRequestsLoading::loadPlugin);
        plugins.forEach(FeatureRequestsPlugin::afterLoad);
    }

    public static Path getEventPath(FeatureRequestsPlugin plugin, FeatureRequestEvent event) {
        String base = FeatureRequestsMod.ID + "/" + event.id().getNamespace();
        if (event != plugin.mainEvent()) {
            base += "/" + event.id().getPath();
        }
        return FabricLoader.getInstance().getConfigDir().resolve(base + ".json");
    }

    public static JsonObject readEvent(FeatureRequestsPlugin plugin, FeatureRequestEvent event) {
        var path = getEventPath(plugin, event);
        try {
            String content = Files.readString(path);
            return FeatureRequestsLoading.GSON.fromJson(content, JsonObject.class);
        }
        catch (IOException e) {
            var obj = event.createDefaultObject(plugin.isDefaultEnabled());
            String content = FeatureRequestsLoading.GSON.toJson(obj);
            try {
                Files.createDirectories(path.getParent());
                Files.writeString(path, content);
            }
            catch (IOException ex) {
                FeatureRequestsMod.LOGGER.error("Failed to write config file for request event '" + event.id() + "'", e);
            }
            return obj;
        }
    }

    public static <T extends FeatureRequests<?>> T emptyWithAll(JsonElement element, Function<Boolean, T> fn) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean()) {
            return fn.apply(element.getAsBoolean());
        }
        return null;
    }

    public static SingleFeatureRequests singleFromJson(JsonElement element) {
        var all = emptyWithAll(element, SingleFeatureRequests::emptyWithAll);
        if (all != null) {
            return all;
        }
        if (!element.isJsonArray()) {
            throw new JsonSyntaxException("single request container must be a boolean or an array");
        }
        Set<Identifier> requests = new HashSet<>();
        for (var elem : element.getAsJsonArray()) {
            var id = JsonHelper.asString(elem, "request");
            requests.add(new Identifier(id));
        }
        return new SingleFeatureRequests(false, requests);
    }

    // Fuck all these methods

    public static <T, K> void addRequestToMapped(MappedRequestPortal<T, K> portal, T holder, JsonElement element) {
        MappedFeatureRequests<K> all = emptyWithAll(element, MappedFeatureRequests::emptyWithAll);
        if (all != null) {
            portal.all();
            return;
        }
        if (!element.isJsonObject()) {
            throw new JsonSyntaxException("mapped request container must be a boolean or an object");
        }
        Map<K, SingleFeatureRequests> requests = new HashMap<>();
        for (var entry : element.getAsJsonObject().entrySet()) {
            var mapping = portal.mappingFromJson(entry.getKey());
            requests.put(mapping, singleFromJson(entry.getValue()));
        }
        var container = new MappedFeatureRequests<>(false, requests);
        portal.addRequest(holder, container);
    }

    @SuppressWarnings("unchecked") // it's fine
    public static <T> void addRequest(FeatureRequestPortal<T, ?> portal, Identifier id, JsonElement value) {
        var holder = portal.holders().get(id);
        if (holder == null) {
            throw new JsonSyntaxException("holder '" + id + "' does not exist");
        }
        if (portal instanceof SingleRequestPortal<?>) {
            var single = (SingleRequestPortal<T>) portal;
            single.addRequest(holder, singleFromJson(value));
        }
        if (portal instanceof MappedRequestPortal<?, ?>) {
            var mapped = (MappedRequestPortal<T, ?>) portal;
            addRequestToMapped(mapped, holder, value);
        }
    }

    public static void loadPortal(Map<String, FeatureRequestPortal<?, ?>> portalMap, String key, JsonElement element) {
        if (!portalMap.containsKey(key)) {
            throw new JsonSyntaxException("request portal '" + key + "' does not exist");
        }
        var portal = portalMap.get(key);
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean()) {
            portal.all();
            return;
        }
        if (!element.isJsonObject()) {
            throw new JsonSyntaxException("portal request set must be a boolean or an object");
        }
        for (var entry : element.getAsJsonObject().entrySet()) {
            var holderId = new Identifier(entry.getKey());
            try {
                addRequest(portal, holderId, entry.getValue());
            }
            catch (Exception e) {
                throw new JsonSyntaxException("error while submitting request to portal '" + key + "' for holder '" + holderId + "'", e);
            }
        }
    }

    public static void loadEvent(FeatureRequestEvent event, JsonObject file) {
        var map = event.portalMap();
        for (var entry : file.entrySet()) {
            String key = entry.getKey();
            loadPortal(map, key, entry.getValue());
        }
    }

    public static void loadPlugin(FeatureRequestsPlugin plugin) {
        for (var event : plugin.events()) {
            try {
                var obj = readEvent(plugin, event);
                loadEvent(event, obj);
            }
            catch (Exception e) {
                throw new JsonSyntaxException("error while submitting request to event '" + event.id() + "' (" + getEventPath(plugin, event) + ")", e);
            }
        }
    }
}