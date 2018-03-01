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

import com.apehat.newyear.event.mock.DomainEvent;
import com.apehat.newyear.event.mock.MockDomainEventDispatcherProvider;
import com.apehat.newyear.event.mock.MockEvent;
import com.apehat.newyear.event.mock.MockEventDispatcherProvider;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author hanpengfei
 * @since 1.0
 */
@SuppressWarnings("unchecked")
public class EventDispatcherTest {

    private static EventBus eventBus;

    @BeforeClass
    public static void setUp() {
        eventBus = SimpleEventBus.getInstance();
        eventBus.registerDefaultProvider(() -> new EventDispatcher<Event>() {
            @Override
            public void submit(Event event) {

            }

            @Override
            public <U extends Event> EventDispatcher<Event> subscribe(Class<U> eventType,
                                                                      EventSubscriber<? super U> subscriber) {
                return null;
            }


            @Override
            public void reset() {

            }
        });
        eventBus.registerProvider(MockEvent.class, MockEventDispatcherProvider.getInstance());
        eventBus.registerProvider(DomainEvent.class, MockDomainEventDispatcherProvider.getInstance());
    }

    @Test
    public void testSubscribe() {
        EventDispatcher<? super Event> eventDispatcher = eventBus.getDispatcher(Event.class);
        eventDispatcher.subscribe(Event.class, new EventSubscriber<Event>() {
            @Override
            public void handle(Event content) {

            }

            @Override
            public String within() {
                return null;
            }
        });
    }

    @Test
    public void testSubscribeByDomainEvent() {
        EventDispatcher<? super Event> eventDispatcher = eventBus.getDispatcher(MockEvent.class);
        System.out.println(eventDispatcher);
        eventDispatcher.subscribe(DomainEvent.class, new EventSubscriber<DomainEvent>() {
            @Override
            public void handle(DomainEvent event) {

            }

            @Override
            public String within() {
                return null;
            }
        });
    }
}