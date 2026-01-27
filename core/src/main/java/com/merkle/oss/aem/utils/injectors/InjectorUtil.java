package com.merkle.oss.aem.utils.injectors;

import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.wcm.api.Page;
import com.merkle.oss.aem.utils.java.ClassUtil;
import com.merkle.oss.aem.utils.sling.SlingUtil;
import com.merkle.oss.aem.utils.wcm.PageManagerUtil;
import org.apache.commons.lang3.ClassUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.adapter.Adaptable;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Technical utility class providing helper methods for Sling Model Injectors.
 * <p>
 * This class contains complex reflection logic to handle type conversions between JCR properties
 * and Java fields, specifically focusing on:
 * <ul>
 * <li>Resolving {@link com.day.cq.wcm.api.Page} objects from various adaptable sources.</li>
 * <li>Mapping {@link org.apache.sling.api.resource.ValueMap} data to complex types (Collections, Arrays, Primitives).</li>
 * <li>Supporting {@link com.day.cq.commons.inherit.InheritanceValueMap} for hierarchical property lookups.</li>
 * </ul>
 */
public class InjectorUtil {

    private static final Logger LOG = LoggerFactory.getLogger(InjectorUtil.class);

    private InjectorUtil() {
        ClassUtil.assertNoInstance(this.getClass());
    }

    /**
     * Attempts to resolve an AEM {@link com.day.cq.wcm.api.Page} from an unknown adaptable object.
     * <p>
     * The resolution follows a prioritized fallback strategy:
     * <ol>
     * <li>Direct Adaptation: Tries {@code adaptable.adaptTo(Page.class)}.</li>
     * <li>Resource Context: If the adaptable is a {@link org.apache.sling.api.resource.Resource}, finds its containing page.</li>
     * <li>Request Context: If the adaptable is a {@link org.apache.sling.api.SlingHttpServletRequest}, finds the page containing the request's resource.</li>
     * </ol>
     *
     * @param adaptable The source object (typically the one being adapted into a Sling Model).
     * @return An {@link Optional} containing the resolved {@link com.day.cq.wcm.api.Page}, or empty if no page context is found.
     */
    public static @NonNull Optional<Page> getPageFromAdaptable(@NonNull final Object adaptable) {
        return Optional.ofNullable(evaluatePageFromAdaptable(adaptable, Adaptable.class, SlingUtil.to(Page.class))
                .orElse(
                        evaluatePageFromAdaptable(adaptable, Resource.class, PageManagerUtil::containingPage)
                                .orElse(
                                        evaluatePageFromAdaptable(adaptable, SlingHttpServletRequest.class, PageManagerUtil::containingPage)
                                                .orElse(null)
                                )
                )
        );
    }

    /**
     * Internal helper to execute a resolution function if the adaptable matches a specific type.
     */
    private static <T> @NonNull Optional<Page> evaluatePageFromAdaptable(@NonNull final Object adaptable, @NonNull final Class<T> withInstance, @NonNull final Function<T, Page> function) {
        if (withInstance.isInstance(adaptable)) {
            return Optional.ofNullable(function.apply(withInstance.cast(adaptable)));
        }
        return Optional.empty();
    }

    /**
     * Retrieves and converts a value from a {@link ValueMap} based on a reflected {@link Type}.
     * <p>
     * This method extends the standard ValueMap behavior by:
     * <ul>
     * <li>Automatically detecting and using {@link com.day.cq.commons.inherit.InheritanceValueMap#getInherited(String, Class)} if applicable.</li>
     * <li>Handling conversion between primitive arrays (e.g., {@code int[]}) and wrapper arrays (e.g., {@code Integer[]}).</li>
     * <li>Supporting {@link List} and {@link Collection} parameterized types.</li>
     * </ul>
     *
     * @param map  The source properties map.
     * @param name The property key.
     * @param type The reflected type of the target field or parameter.
     * @return The converted value, or {@code null} if the property is missing or incompatible.
     */
    @SuppressWarnings("CQRules:CQBP-44---WrongLogLevelInCatchBlock")
    public static @Nullable Object getValueFromValueMap(@Nullable final ValueMap map, @NonNull final String name, @NonNull final Type type) {
        if (map == null) {
            return null;
        } else if (type instanceof Class<?> clazz) {
            try {
                return getPossibleInherited(map, name, clazz);
            } catch (ClassCastException e) {
                LOG.debug("Handle case of primitive/wrapper arrays for property {}", name, e);
                return handlePrimitiveArrays(map, name, clazz);
            }
        } else if (type instanceof ParameterizedType) {
            return handleParameterizedType(map, name, (ParameterizedType) type);
        } else {
            LOG.debug("ValueMap-based injection does not support non-class types: {}", type);
            return null;
        }
    }

    /**
     * Handles the conversion of JCR array properties into Java {@link List} or {@link Collection} types.
     */
    private static @Nullable Object handleParameterizedType(@NonNull final ValueMap map, @NonNull final String name, @NonNull final ParameterizedType type) {
        if (type.getActualTypeArguments().length != 1) {
            return null;
        }

        final Class<?> collectionType = (Class<?>) type.getRawType();
        if (!(collectionType.equals(Collection.class) || collectionType.equals(List.class))) {
            return null;
        }

        final Class<?> itemType = (Class<?>) type.getActualTypeArguments()[0];
        final Object array = getPossibleInherited(map, name, Array.newInstance(itemType, 0).getClass());
        if (array == null) {
            return null;
        }

        return Arrays.asList((Object[]) array);
    }

    /**
     * Bridges the gap between primitive and wrapper arrays.
     * <p>
     * Because the JCR often stores values as primitive arrays, but Java reflection may expect
     * wrapper objects (or vice versa), this method performs manual array transformation.
     */
    private static @Nullable Object handlePrimitiveArrays(@NonNull final ValueMap map, @NonNull final String name, @NonNull final Class<?> clazz) {
        if (clazz.isArray()) {
            final Class<?> componentType = clazz.getComponentType();
            if (componentType.isPrimitive()) {
                final Class<?> wrapper = ClassUtils.primitiveToWrapper(componentType);
                if (wrapper != componentType) {
                    final Object wrapperArray = getPossibleInherited(map, name, Array.newInstance(wrapper, 0)
                            .getClass());
                    if (wrapperArray != null) {
                        return unwrapArray(wrapperArray, componentType);
                    }
                }
            } else {
                final Class<?> primitiveType = ClassUtils.wrapperToPrimitive(componentType);
                if (primitiveType != componentType) {
                    final Object primitiveArray = getPossibleInherited(map, name, Array.newInstance(primitiveType, 0).getClass());
                    if (primitiveArray != null) {
                        return wrapArray(primitiveArray, componentType);
                    }
                }
            }
        }

        return null;
    }

    /**
     * Determines the correct ValueMap retrieval method based on whether inheritance is supported.
     */
    private static @Nullable Object getPossibleInherited(@NonNull final ValueMap map, @NonNull final String name, @NonNull final Class<?> type) {
        if (map instanceof InheritanceValueMap) {
            return ((InheritanceValueMap) map).getInherited(name, type);
        } else {
            return map.get(name, type);
        }
    }

    private static @NonNull Object unwrapArray(@NonNull final Object wrapperArray, @NonNull final Class<?> primitiveType) {
        return transformArray(wrapperArray, primitiveType);
    }

    private static @NonNull Object wrapArray(@NonNull final Object primitiveArray, @NonNull final Class<?> wrapperType) {
        return transformArray(primitiveArray, wrapperType);
    }

    /**
     * Performs an element-by-element copy between a wrapper array and a primitive array (or vice-versa).
     */
    private static @NonNull Object transformArray(@NonNull final Object sourceArray, @NonNull final Class<?> targetComponentType) {
        final int length = Array.getLength(sourceArray);
        final Object targetArray = Array.newInstance(targetComponentType, length);
        for (int i = 0; i < length; i++) {
            Array.set(targetArray, i, Array.get(sourceArray, i));
        }
        return targetArray;
    }

}
