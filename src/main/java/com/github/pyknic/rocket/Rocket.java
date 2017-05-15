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

import com.github.pyknic.rocket.internal.RocketBuilderImpl;

/**
 * Rocker Launcher system.
 *
 * @param <E>  the phase category enum
 *
 * @author Emil Forslund
 * @since  1.0.0
 */
public interface Rocket<E extends Enum<E>> {

    /**
     * Creates a new {@link RocketBuilder}.
     *
     * @param <E>         the phases enum type
     * @param phasesEnum  enum representing the phases available
     * @return            the new builder
     */
    static <E extends Enum<E>> RocketBuilder<E> builder(Class<E> phasesEnum) {
        return new RocketBuilderImpl<>(phasesEnum);
    }

    /**
     * Invoke all the methods as part of the specified phase.
     *
     * @param phase  the phase to invoke
     */
    void launch(E phase);

}
