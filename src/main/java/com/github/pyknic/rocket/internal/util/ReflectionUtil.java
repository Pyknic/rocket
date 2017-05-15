/**
 *
 * Copyright (c) 2017, Emil Forslund. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.pyknic.rocket.internal.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.stream.Stream;

/**
 * Some common utility methods for analyzing classes with reflection.
 *
 * @author  Emil Forslund
 * @since   1.0.0
 */
public final class ReflectionUtil {

    /**
     * Returns a stream of all the member fields for the specified class,
     * including inherited fields from any ancestors. This includes public,
     * private, protected and package private fields.
     *
     * @param clazz  the class to traverse
     * @return       stream of fields
     */
    public static Stream<Field> traverseFields(Class<?> clazz) {
        final Class<?> parent = clazz.getSuperclass();
        final Stream<Field> inherited;

        if (parent != null) {
            inherited = traverseFields(parent);
        } else {
            inherited = Stream.empty();
        }

        return Stream.concat(inherited, Stream.of(clazz.getDeclaredFields()));
    }

    /**
     * Returns a stream of all methods in the specified class, including
     * inherited ones.
     *
     * @param clazz  the class to traverse
     * @return       stream of methods
     */
    public static Stream<Method> traverseMethods(Class<?> clazz) {
        return traverseAncestors(clazz)
            .map(Class::getDeclaredMethods)
            .flatMap(Stream::of);
    }

    /**
     * Returns a stream of all the classes upwards in the inheritance tree of
     * the specified class, including the class specified as the first element
     * and {@code java.lang.Object} as the last one.
     *
     * @param clazz  the first class in the tree
     * @return       stream of ancestors (including {@code clazz})
     */
    public static Stream<Class<?>> traverseAncestors(Class<?> clazz) {
        final Class<?>[] interfaces = clazz.getInterfaces();
        if (clazz.getSuperclass() == null) {
            if (interfaces.length == 0) {
                return Stream.of(clazz);
            } else {
                return Stream.concat(
                    Stream.of(clazz),
                    Stream.of(clazz.getInterfaces())
                        .flatMap(ReflectionUtil::traverseAncestors)
                ).distinct();
            }
        } else {
            return Stream.concat(
                Stream.of(clazz),
                Stream.concat(
                    Stream.of(clazz.getSuperclass()),
                    Stream.of(clazz.getInterfaces())
                ).flatMap(ReflectionUtil::traverseAncestors)
            ).distinct();
        }
    }

    /**
     * Should never be invoked.
     */
    private ReflectionUtil() {}
}