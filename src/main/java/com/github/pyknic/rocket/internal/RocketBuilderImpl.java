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

import com.github.pyknic.rocket.Execute;
import com.github.pyknic.rocket.Rocket;
import com.github.pyknic.rocket.RocketBuilder;
import com.github.pyknic.rocket.RocketException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.github.pyknic.rocket.internal.util.ReflectionUtil.traverseAncestors;
import static com.github.pyknic.rocket.internal.util.ReflectionUtil.traverseMethods;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

/**
 * @author Emil Forslund
 * @since  1.0.0
 */
public final class RocketBuilderImpl<E extends Enum<E>> implements RocketBuilder<E> {

    private final Class<E> phasesEnum;
    private final List<Object> instances;

    public RocketBuilderImpl(Class<E> phasesEnum) {
        this.phasesEnum = requireNonNull(phasesEnum);
        this.instances  = new LinkedList<>();
    }

    @Override
    public <T> RocketBuilder<E> with(T instance) {
        instances.add(instance);
        return this;
    }

    @Override
    public Rocket<E> build() {
        final Map<E, List<Runnable>> actions = new EnumMap<>(phasesEnum);
        final E[] phases = phasesEnum.getEnumConstants();

        for (final E phase : phases) {
            final List<Runnable> phaseActions = new LinkedList<>();

            final String phaseName = phase.name();
            final List<ActionMaker> actionMakers = new LinkedList<>();

            instances.forEach(inst ->
                traverseMethods(inst.getClass())
                    .filter(m -> !Modifier.isStatic(m.getModifiers()))
                    .filter(m -> {
                        final Execute execute = m.getAnnotation(Execute.class);
                        return execute != null
                            && phaseName.equalsIgnoreCase(execute.value());
                    }).forEachOrdered(m -> {
                        final Set<Class<?>> deps =
                            new HashSet<>(asList(m.getParameterTypes()));

                        final String name = format("%s#%s(%s)",
                            inst.getClass().getSimpleName(),
                            m.getName(),
                            Stream.of(m.getParameterTypes())
                                .map(Class::getSimpleName)
                                .collect(joining(", "))
                        );

                        // Create a node for this action.
                        actionMakers.add(new ActionMaker(inst.getClass(),
                            name, deps, () -> {
                                // Resolve every argument.
                                final Object[] args = Stream.of(m.getParameterTypes())
                                    .map(c -> instances.stream()
                                        .filter(c::isInstance).findFirst()
                                        .orElseThrow(() -> new RocketException(format(
                                            "Class '%s' has a method '%s' that has " +
                                            "the @Execute-annotation but one argument" +
                                            " '%s' can't be resolved. Make sure it " +
                                            "is installed in the RocketBuilder.",
                                            inst.getClass().getName(),
                                            m.getName(),
                                            c.getName()
                                        )))
                                    ).toArray();

                                m.setAccessible(true);
                                return () -> {
                                    try {
                                        m.invoke(inst, args);
                                    } catch (final IllegalAccessException
                                               | InvocationTargetException ex) {
                                        throw new RocketException(
                                            "Could not invoke annotated method " +
                                            name + ".", ex);
                                    }
                                };
                            }));
                    })
                );

            // If there was no actions in this phase, continue.
            if (!actionMakers.isEmpty()) {

                // Iterate over the action makers, using the makers as they are
                // possible to invoke.
                final Set<Class<?>> resolved = new HashSet<>();
                while (!actionMakers.isEmpty()) {
                    int counter = 0;

                    final Iterator<ActionMaker> it = actionMakers.iterator();
                    while (it.hasNext()) {
                        final ActionMaker am = it.next();
                        if (!am.dependencies.stream().allMatch(resolved::contains))
                            continue;

                        phaseActions.add(am.makeAction.get());
                        traverseAncestors(am.clazz).forEach(resolved::add);

                        it.remove();
                        counter++;
                    }

                    if (counter == 0) {
                        throw new RocketException(
                            "Error building " + phaseName + " phase. The " +
                                "following actions appear to be stuck in an " +
                                "infinite loop: [\n  " +
                                actionMakers.stream()
                                    .map(am -> am.name)
                                    .collect(joining("\n  ")) +
                                "\n]."
                        );
                    }
                }
            }

            switch (phaseActions.size()) {
                case 0  : actions.put(phase, emptyList()); break;
                case 1  : actions.put(phase, singletonList(phaseActions.get(0))); break;
                default : actions.put(phase, unmodifiableList(phaseActions)); break;
            }
        }

        return new RocketImpl<>(actions);
    }

    private static final class ActionMaker {

        private final Class<?> clazz;
        private final String name;
        private final Set<Class<?>> dependencies;
        private final Supplier<Runnable> makeAction;

        ActionMaker(Class<?> clazz,
                    String name,
                    Set<Class<?>> dependencies,
                    Supplier<Runnable> makeAction) {

            this.clazz        = requireNonNull(clazz);
            this.name         = requireNonNull(name);
            this.dependencies = requireNonNull(dependencies);
            this.makeAction   = requireNonNull(makeAction);
        }
    }
}