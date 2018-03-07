/*
 * Copyright ApeHat.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.apehat.newyear.event;

/**
 * Abstract {@link Event} implemention.
 * The version default is {@link #INITIAL_VERSION}.
 *
 * @author hanpengfei
 * @since 1.0
 */
public abstract class AbstractEvent implements Event {
    private static final long serialVersionUID = 399018640362809954L;

    private final long occurredTimeMillis;
    private final int version;

    protected AbstractEvent() {
        this.occurredTimeMillis = System.currentTimeMillis();
        version = INITIAL_VERSION;
    }

    /**
     * When current is extension form other event, this constructor should be
     * caller.
     * At this time, the version should increase form parent version.
     *
     * @param occurredTimeMillis the occurred time millis of parent
     * @throws IllegalArgumentException the parent version less than 1
     * @see #version()
     */
    protected AbstractEvent(long occurredTimeMillis, int parentVersion) {
        if (parentVersion < INITIAL_VERSION) {
            throw new IllegalArgumentException(
                    "The version of parent mustn't less than 1.");
        }
        this.occurredTimeMillis = occurredTimeMillis;
        version = parentVersion + 1;
    }

    @Override
    public long occurredOn() {
        return occurredTimeMillis;
    }

    @Override
    public int version() {
        return version;
    }
}
