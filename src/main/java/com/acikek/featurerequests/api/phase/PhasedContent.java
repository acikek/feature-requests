package com.acikek.featurerequests.api.phase;

import com.acikek.featurerequests.api.impl.phase.PhasedContentImpls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * <p>
 *     A container for content that may be created at some point in time but of which there is no guarantee.
 * </p>
 *
 * <p>
 *     This is useful for {@link ContentBase} implementations, wherein due to the request system, content may not be
 *     registered; however, all created content must be registered, so this content must not be created either.
 * </p>
 *
 * <pre>
 * {@code
 * PhasedContent<Block> block = PhasedContent.of(() -> new Block(...));
 * if (someCondition) {
 *     registerBlock(block.create());
 * }
 * Block result = block.get(); // the Block instance if created, null otherwise
 * Block required = block.require(); // not null, but errors if not created
 * }
 * </pre>
 */
public abstract class PhasedContent<T> {

    protected Supplier<T> supplier;
    protected Optional<T> value = Optional.empty();

    protected PhasedContent(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    /**
     * Creates a phased content instance.<br>
     * The specified supplier can return {@code null}, but the supplier itself must not be {@code null}.
     */
    public static <T> PhasedContent<T> of(@NotNull Supplier<T> supplier) {
        Objects.requireNonNull(supplier);
        return new PhasedContentImpls.Internal<>(supplier);
    }

    /**
     * @return a phased content implementation wherein creation calls do nothing
     */
    public static <T> PhasedContent<T> none() {
        return new PhasedContentImpls.Null<>();
    }

    /**
     * Creates a nullable phased content instance.<br>
     * If the specified supplier is {@code null}, returns {@link PhasedContent#none()}
     */
    public static <T> PhasedContent<T> ofNullable(@Nullable Supplier<T> supplier) {
        return supplier != null
                ? of(supplier)
                : none();
    }

    /**
     * Creates a phased content instance where the value already exists.<br>
     * The existing value cannot be {@code null}.
     */
    public static <T> PhasedContent<T> ofExternal(@NotNull T existing) {
        Objects.requireNonNull(existing);
        return new PhasedContentImpls.External<>(existing);
    }

    /**
     * Creates a phased content instance from a generic {@link Object}.
     *
     * <ul>
     *     <li>Uses {@link PhasedContent#ofExternal(Object)} if the object is of type {@code T}</li>
     *     <li>Uses {@link PhasedContent#of(Supplier)} if the object is a {@link Supplier}</li>
     * </ul>
     *
     * @throws IllegalStateException if no suitable constructor can be found
     */
    @SuppressWarnings("unchecked")
    public static <T> PhasedContent<T> from(Object content, Class<T> clazz) {
        if (clazz.isInstance(content)) {
            return ofExternal(clazz.cast(content));
        }
        if (content instanceof Supplier<?> supplier) {
            return of((Supplier<T>) supplier);
        }
        throw new IllegalStateException("object must be either a content value or a supplier");
    }

    /**
     * @return the current phase of the content
     */
    public abstract ContentPhase phase();

    /**
     * @return whether this content existed before it was created
     */
    public boolean isExternal() {
        return phase() == ContentPhase.EXTERNAL;
    }

    /**
     * @return the opposite of {@link PhasedContent#isExternal()}
     */
    public boolean isInternal() {
        return !isExternal() && canExist();
    }

    /**
     * @return whether the value is able to be created or already exists
     */
    public boolean canExist() {
        return phase() != ContentPhase.NULL;
    }

    /**
     * Creates the content and returns it for a {@link PhasedContent#create()} call.
     */
    protected abstract T createContent();

    /**
     * Retrieves, caches, and returns the value from the original supplier.
     * <b>This can only be done once!</b>
     * @return the created value
     * @throws IllegalStateException if the value has already been created
     * @see PhasedContent#isCreated()
     */
    public T create() {
        if (isCreated()) {
            throw new IllegalStateException("phased content has already been created");
        }
        return createContent();
        /*;*/
    }

    /**
     * @return the created value
     * @throws IllegalStateException if the value has not been created
     * @see PhasedContent#create()
     * @see PhasedContent#get()
     */
    public T require() {
        if (!isCreated()) {
            throw new IllegalStateException("phased content was never created");
        }
        return get();
    }

    /**
     * @return the value if created, otherwise {@code null}
     * @see PhasedContent#create()
     * @see PhasedContent#require()
     */
    public @Nullable T get() {
        return value.orElse(null);
    }

    /**
     * @return whether the value has been created
     * @see PhasedContent#create()
     */
    public boolean isCreated() {
        return value.isPresent();
    }

    /**
     * Executes a runnable if {@link PhasedContent#isExternal()} is not {@code true};
     * that is, if this content was created and did not exist beforehand
     */
    public void ifInternal(Runnable fn) {
        if (isInternal()) {
            fn.run();
        }
    }

    /**
     * Creates the content and then, <b>if this content is internal</b>, executes the specified callback.
     * @see PhasedContent#create()
     * @see PhasedContent#ifInternal(Runnable)
     */
    public void create(Consumer<T> ifInternal) {
        var value = create();
        ifInternal(() -> ifInternal.accept(value));
    }

    /**
     * @see PhasedContent#create(Consumer)
     */
    public void create(BiConsumer<T, PhasedContent<T>> ifInternal) {
        create(content -> ifInternal.accept(content, this));
    }

    /**
     * Requires and executes the specified callback <b>if this content is internal</b>.
     * @see PhasedContent#require()
     * @see PhasedContent#ifInternal(Runnable)
     */
    public void require(Consumer<T> ifInternal) {
        ifInternal(() -> ifInternal.accept(require()));
    }

    /**
     * @see PhasedContent#require(Consumer)
     */
    public void require(BiConsumer<T, PhasedContent<T>> ifInternal) {
        require(content -> ifInternal.accept(content, this));
    }

    /**
     * Executes the callback if the content has been created,
     * regardless of whether the value is internal.
     */
    public void ifCreated(Consumer<T> callback) {
        if (isCreated()) {
            callback.accept(require());
        }
    }

    /**
     * @see PhasedContent#ifCreated(Consumer)
     */
    public void ifCreated(BiConsumer<T, PhasedContent<T>> callback) {
        ifCreated(content -> callback.accept(content, this));
    }

    /**
     * @return the members of the specified collection that have been created
     */
    public static <T> List<PhasedContent<T>> filterByCreation(Collection<PhasedContent<T>> containers) {
        return containers.stream()
                .filter(PhasedContent::isCreated)
                .toList();
    }

    /**
     * @return the <b>values</b> of the specified content collection that have been created
     * @see PhasedContent#filterByCreation(Collection)
     */
    public static <T> List<T> getByCreation(Collection<PhasedContent<T>> containers) {
        return filterByCreation(containers).stream()
                .map(PhasedContent::get)
                .toList();
    }

    /**
     * @see PhasedContent#filterByCreation(Collection)
     */
    @SafeVarargs
    public static <T> List<PhasedContent<T>> filterByCreation(PhasedContent<T>... containers) {
        return filterByCreation(Arrays.stream(containers).toList());
    }

    /**
     * @see PhasedContent#getByCreation(Collection)
     */
    @SafeVarargs
    public static <T> List<T> getByCreation(PhasedContent<T>... containers) {
        return getByCreation(Arrays.stream(containers).toList());
    }
}
