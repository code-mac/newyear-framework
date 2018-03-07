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
 * @author hanpengfei
 * @since 1.0
 */
public interface EventPublisher<T extends Event> {

    /**
     * Publish an event.
     * <p>
     * Note: before call this method, must ensure already registered
     * appropriate {@link DispatcherProvider} of specified event, or
     * already registered default provider.
     *
     * @param event the event will be published, must be instance of
     *              {@code basisEventType}
     * @throws NullPointerException  specified event is null
     * @throws IllegalStateException cannot find publisher by specified event
     * @see EventBus#registerProvider(Class, DispatcherProvider)
     * @see EventBus#registerDefaultProvider(DispatcherProvider)
     * @see EventBus#submit(Event)
     */
    default <E extends T> void publish(E event) {
        EventBus.getInstance().submit(event);
    }
}
