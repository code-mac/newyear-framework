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

import com.apehat.newyear.core.RegisterException;
import com.apehat.newyear.event.mock.DomainEvent;
import com.apehat.newyear.event.mock.MockDomainEventDispatcherProvider;
import com.apehat.newyear.event.mock.MockEventDispatcherProvider;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author hanpengfei
 * @since 1.0
 */
@SuppressWarnings("unchecked")
public class SimpleEventBusTest {

    private static final EventBus PROVIDERS = SimpleEventBus.getInstance();

    @BeforeClass
    public static void setUp() {
        PROVIDERS.registerDefaultProvider(MockEventDispatcherProvider.getInstance());
        PROVIDERS.registerProvider(DomainEvent.class, MockDomainEventDispatcherProvider.getInstance());
    }

    @Test
    public void registerDefaultProvider() {
        PROVIDERS.registerDefaultProvider(MockEventDispatcherProvider.getInstance());
    }

    @Test
    public void registerProvider() {
        PROVIDERS.registerProvider(DomainEvent.class, MockDomainEventDispatcherProvider.getInstance());
    }

    @Test(expectedExceptions = RegisterException.class)
    public void registerIllegalProvider() {
        PROVIDERS.registerProvider(Event.class, MockDomainEventDispatcherProvider.getInstance());
    }

    @Test
    public void newService() {
        EventDispatcher<?> eventDispatcher = PROVIDERS.getDispatcher(DomainEvent.class);
        EventDispatcher<?> eventDispatcher1 = PROVIDERS.getDispatcher(DomainEvent.class);
        EventDispatcher<?> eventDispatcher2 = PROVIDERS.getDispatcher(DomainEvent.class);
//        assert MockDomainEventDispatcherProvider.getInstance().getService().equals(eventDispatcher);
        assert eventDispatcher != null;
        assert eventDispatcher.equals(eventDispatcher1);
        assert eventDispatcher.equals(eventDispatcher2);
    }
}