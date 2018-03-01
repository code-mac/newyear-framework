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

package com.apehat.newyear.event.mock;

import com.apehat.newyear.event.EventDispatcher;
import com.apehat.newyear.event.EventDispatcherProvider;
import com.apehat.newyear.event.EventSubscriber;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class MockDomainEventDispatcherProvider implements EventDispatcherProvider {
    private static final EventDispatcher<? extends DomainEvent> PUBLISHER = new MockDomainEventEventDispatcher();

    private static final EventDispatcherProvider PROVIDER = new MockDomainEventDispatcherProvider();

    private MockDomainEventDispatcherProvider() {
    }

    public static EventDispatcherProvider getInstance() {
        return PROVIDER;
    }

    @Override
    public EventDispatcher<? extends DomainEvent> getService() {
        return PUBLISHER;
    }

    private static class MockDomainEventEventDispatcher implements EventDispatcher<DomainEvent> {

        @Override
        public void submit(DomainEvent event) {
        }

        @Override
        public <U extends DomainEvent> EventDispatcher<DomainEvent> subscribe(Class<U> eventType, EventSubscriber<? super U> subscriber) {
            System.out.println(String.format("[%s] subscribed [%s].", subscriber.getClass(), eventType));
            return this;
        }

        @Override
        public void reset() {
        }
    }
}
