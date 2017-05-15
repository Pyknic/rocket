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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.pyknic.rocket.RocketTest.Phase.DESTROY;
import static com.github.pyknic.rocket.RocketTest.Phase.INIT;
import static com.github.pyknic.rocket.RocketTest.Phase.UPDATE;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Emil Forslund
 * @since  1.0.0
 */
class RocketTest {

    enum Phase {
        INIT,
        UPDATE,
        DESTROY
    }

    boolean firstInitiated;
    boolean secondInitiated;
    boolean thirdInitiated;

    boolean firstUpdated;
    boolean secondUpdated;
    boolean thirdUpdated;

    boolean firstDestroyed;
    boolean secondDestroyed;
    boolean thirdDestroyed;

    @BeforeEach
    void reset() {
        firstInitiated  = false;
        secondInitiated = false;
        thirdInitiated  = false;

        firstUpdated  = false;
        secondUpdated = false;
        thirdUpdated  = false;

        firstDestroyed  = false;
        secondDestroyed = false;
        thirdDestroyed  = false;
    }

    @Test
    void launch() {

        class First {
            @Execute("init") void init() {firstInitiated = true;}
            @Execute("update") void update() {firstUpdated = true;}
            @Execute("destroy") void destroy() {firstDestroyed = true;}
        }

        class Second {
            @Execute("init") void init() {secondInitiated = true;}
            @Execute("update") void update(First first) {secondUpdated = true;}
            @Execute("destroy") void destroy() {secondDestroyed = true;}
        }

        class Third {
            @Execute("init") void init(First first) {thirdInitiated = true;}
            @Execute("update") void update(First first, Second second) {thirdUpdated = true;}
            @Execute("destroy") void destroy(Second second) {thirdDestroyed = true;}
        }

        final Rocket<Phase> rocket = Rocket.builder(Phase.class)
            .with(new First())
            .with(new Second())
            .with(new Third())
            .build();

        assertFalse(firstInitiated);
        assertFalse(firstUpdated);
        assertFalse(firstDestroyed);
        assertFalse(secondInitiated);
        assertFalse(secondUpdated);
        assertFalse(secondDestroyed);
        assertFalse(thirdInitiated);
        assertFalse(thirdUpdated);
        assertFalse(thirdDestroyed);

        rocket.launch(INIT);

        assertTrue(firstInitiated);
        assertFalse(firstUpdated);
        assertFalse(firstDestroyed);
        assertTrue(secondInitiated);
        assertFalse(secondUpdated);
        assertFalse(secondDestroyed);
        assertTrue(thirdInitiated);
        assertFalse(thirdUpdated);
        assertFalse(thirdDestroyed);

        rocket.launch(UPDATE);

        assertTrue(firstInitiated);
        assertTrue(firstUpdated);
        assertFalse(firstDestroyed);
        assertTrue(secondInitiated);
        assertTrue(secondUpdated);
        assertFalse(secondDestroyed);
        assertTrue(thirdInitiated);
        assertTrue(thirdUpdated);
        assertFalse(thirdDestroyed);

        rocket.launch(DESTROY);

        assertTrue(firstInitiated);
        assertTrue(firstUpdated);
        assertTrue(firstDestroyed);
        assertTrue(secondInitiated);
        assertTrue(secondUpdated);
        assertTrue(secondDestroyed);
        assertTrue(thirdInitiated);
        assertTrue(thirdUpdated);
        assertTrue(thirdDestroyed);
    }

}