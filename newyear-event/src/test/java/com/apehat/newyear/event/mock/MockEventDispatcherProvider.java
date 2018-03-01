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

import com.apehat.newyear.event.Event;
import com.apehat.newyear.event.EventDispatcher;
import com.apehat.newyear.event.EventDispatcherProvider;
import com.apehat.newyear.event.EventSubscriber;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class MockEventDispatcherProvider implements EventDispatcherProvider<MockEvent> {

    private static final EventDispatcher<? super MockEvent> PUBLISHER = new MockEventEventDispatcher();
    private static final EventDispatcherProvider<? super MockEvent> PROVIDER = new MockEventDispatcherProvider();

    private MockEventDispatcherProvider() {
    }

    public static EventDispatcherProvider<? super MockEvent> getInstance() {
        return PROVIDER;
    }

    @Override
    public EventDispatcher<? super MockEvent> getService() {
        return PUBLISHER;
    }

    private static class MockEventEventDispatcher implements EventDispatcher<Event> {

        @Override
        public void submit(Event event) {

        }

        @Override
        public <U extends Event> EventDispatcher<Event> subscribe(Class<U> eventType, EventSubscriber<? super U> subscriber) {
            System.out.println(String.format("[%s] subscribed event.", subscriber.getClass()));
            return this;
        }

        @Override
        public void reset() {

        }
    }
}
