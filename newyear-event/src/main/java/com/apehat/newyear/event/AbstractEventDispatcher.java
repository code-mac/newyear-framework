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

import com.apehat.newyear.util.ReflectionUtils;
import com.apehat.newyear.util.Validation;

/**
 * @author hanpengfei
 * @since 1.0
 */
public abstract class AbstractEventDispatcher<T extends Event> implements EventDispatcher<T> {

    /**
     * 验证指定的事件类型是否能订阅到该发布器
     * 验证发布器是否能处理所有指定的事件类型
     */
    protected final void checkPermission(Class<? extends Event> eventType, EventSubscriber<?> subscriber) {
        Validation.requireNonNull(eventType);
        Validation.requireNonNull(subscriber);

        assertAllowSubscribe(eventType);
        assertCanHandle(eventType, subscriber);
    }

    private void assertCanHandle(Class<? extends Event> eventType, EventSubscriber<?> subscriber) {
        // ensure subscribe can handle event with eventType
        Class<? extends EventSubscriber> aClass = subscriber.getClass();
        Class<?> parameter = ReflectionUtils.getGenericParameters(aClass, EventSubscriber.class)[0];
        if (!parameter.isAssignableFrom(eventType)) {
            // the subscribe can handle all event what is sub of event type
            throw new IllegalArgumentException(String.format(
                    "[%s] cannot handle the event with type [%s]", aClass, eventType));
        }
    }

    private void assertAllowSubscribe(Class<? extends Event> eventType) {
        // if support root type, need to checkPermission Event.class
        if (rootTypeSupported() && Event.class == eventType) {
            return;
        }

        final Class<?> subParam = rootEventType();
        assert subParam != null;
        // If event type isn't sub of statemented type - this
        // publisher cannot submit the event type
        if (!subParam.isAssignableFrom(eventType)) {
            throw new IllegalArgumentException(String.format(
                    "[%s] cannot submit by [%s]", eventType, getClass()));
        }
    }

    protected abstract Class<T> rootEventType();

    protected abstract boolean rootTypeSupported();
}
