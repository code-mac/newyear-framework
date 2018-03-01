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

package com.apehat.newyear.domain.model;

import com.apehat.newyear.event.EventDispatcher;
import com.apehat.newyear.event.EventDispatcherProvider;
import com.apehat.newyear.event.EventPublisher;
import com.apehat.newyear.event.SimpleEventBus;

/**
 * @author hanpengfei
 * @since 1.0
 */
public interface Aggregation<T> extends Entity<T>, EventPublisher<DomainEvent> {

    /**
     * Publish an event.
     * <p>
     * Note: before call this method, must ensure already registered
     * appropriate {@link EventDispatcherProvider} of specified event, or
     * already registered default provider.
     *
     * @param event the event will be published, must be instance of
     *              {@code basisEventType}
     * @throws NullPointerException  specified event is null
     * @throws IllegalStateException cannot find publisher by specified event
     * @see SimpleEventBus#registerProvider(Class, EventDispatcherProvider)
     * @see SimpleEventBus#registerDefaultProvider(EventDispatcherProvider)
     */
    @Override
    default void publish(DomainEvent event) {
        EventDispatcher publisher = SimpleEventBus.getInstance().getDispatcher(event.getClass());
        //noinspection unchecked
        publisher.submit(event);
    }
}
