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

package com.apehat.newyear.event.annotation;

import com.apehat.newyear.event.Event;
import com.apehat.newyear.event.EventSubscriber;
import com.apehat.newyear.validation.annotation.NonNull;

import java.lang.annotation.*;

/**
 * Subscribe events by defined {@code EventSubscriber}s. The subscriber must
 * have an no-parameter constructor.
 *
 * @author hanpengfei
 * @since 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Subscribe {

    /**
     * Subscribe a specified event.
     *
     * @return be subscribed event.
     */
    @NonNull
    Class<? extends Event> evenType();

    /**
     * The subscribers to subscribe specified event.
     *
     * @return the subscribes to subscriber
     */
    @NonNull
    Class<? extends EventSubscriber>[] by();
}
