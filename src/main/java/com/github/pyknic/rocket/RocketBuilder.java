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
package com.github.pyknic.rocket;

/**
 * Builder for the {@link Rocket} interface.
 *
 * @param <E>  the phase category enum
 *
 * @author Emil Forslund
 * @since  1.0.0
 */
public interface RocketBuilder<E extends Enum<E>> {

    /**
     * Add the specified instance to the launcher system. The instance will be
     * scanned for methods with the {@link Execute}-annotation.
     *
     * @param <T>       the type of the instance
     * @param instance  the instance to add
     * @return          a reference to this builder
     */
    <T> RocketBuilder<E> with(T instance);

    /**
     * Builds the {@link Rocket} instance, resolving all the dependencies.
     *
     * @return  the built {@link Rocket}
     */
    Rocket<E> build();

}