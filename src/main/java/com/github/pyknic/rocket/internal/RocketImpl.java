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
package com.github.pyknic.rocket.internal;

import com.github.pyknic.rocket.Rocket;

import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * @author Emil Forslund
 * @since  1.0.0
 */
final class RocketImpl<E extends Enum<E>> implements Rocket<E> {

    private final Map<E, List<Runnable>> actions;

    RocketImpl(Map<E, List<Runnable>> actions) {
        this.actions = requireNonNull(actions);
    }

    @Override
    public void launch(E phase) {
        actions.get(phase).forEach(Runnable::run);
    }
}