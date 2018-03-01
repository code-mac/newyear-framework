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

import com.apehat.newyear.util.Validation;

import java.util.Comparator;

/**
 * Event publisher service facade.
 *
 * @author hanpengfei
 * @since 1.0
 */
public final class SimpleEventBus implements EventBus<Event> {

    private static final EventBus<Event> INSTANCE = new SimpleEventBus();

    private final SimpleEventDispatcherProviderRepository repository;

    private SimpleEventBus() {
        if (INSTANCE != null) {
            throw new AssertionError();
        }
        repository = new SimpleEventDispatcherProviderRepository();
    }

    /**
     * Returns the global unique INSTANCE of {@code SimpleEventBus}.
     *
     * @return the INSTANCE.
     */
    public static EventBus<Event> getInstance() {
        return SimpleEventBus.INSTANCE;
    }

    @Override
    public EventBus<Event> setComparator(Comparator<Class<? extends Event>> comparator) {
        repository.setComparator(comparator);
        return this;
    }

    @Override
    public Class<Event> defaultEventType() {
        return Event.class;
    }

    @Override
    public EventBus<Event> registerDefaultProvider(EventDispatcherProvider<? super Event> provider) {
        registerProvider(defaultEventType(), provider);
        return this;
    }

    @Override
    public EventBus<Event> registerProvider(Class<? extends Event> eventType, EventDispatcherProvider<? super Event> provider) {
        repository.store(eventType, provider);
        return this;
    }

    @Override
    public EventDispatcher<? super Event> getDispatcher(Class<? extends Event> eventType) {
        Validation.requireNonNull(eventType, "Must specified eventType");

        EventDispatcherProvider<? super Event> provider = repository.find(eventType);
        if (provider == null) {
            throw new IllegalStateException("No provider of " + eventType);
        }
        return provider.getService();
    }
}
