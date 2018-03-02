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

import com.apehat.newyear.validation.annotation.NonNull;

import java.io.Serializable;

/**
 * @author hanpengfei
 * @since 1.0
 */
public interface Event extends Serializable {

    int INITIAL_VERSION = 1;

    /**
     * Returns the occurred time millis of event.
     * If current is extended form others. This value should same as it's
     * parent.
     *
     * @return the occurred time millis of event.
     */
    long occurredOn();

    /**
     * Returns the event occur scope.
     *
     * @return current event occur scope
     */
    @NonNull
    String scope();

    /**
     * Returns the version of event. Default is {@link #INITIAL_VERSION}.
     * If current is extended form others. This value should increasing form
     * it's parent.
     *
     * @return the version of event.
     */
    default int version() {
        return INITIAL_VERSION;
    }
}
