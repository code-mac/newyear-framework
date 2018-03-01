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

/**
 * @author hanpengfei
 * @since 1.0
 */

import com.apehat.newyear.core.SubscriberRepository;
import com.apehat.newyear.event.EventDispatcher;
import com.apehat.newyear.event.EventDispatcherProvider;
import com.apehat.newyear.event.EventSubscriber;
import com.apehat.newyear.util.Validation;

import java.util.Set;

/**
 * Default domain event dispatcher provider implemention
 *
 * @author hanpengfei
 */
public class DomainEventDispatcherProvider implements EventDispatcherProvider {

    private static final DomainEventDispatcherProvider PROVIDER = new DomainEventDispatcherProvider();
    private static final ThreadLocal<DomainEventDispatcher> DISPATCHERS =
            ThreadLocal.withInitial(DomainEventDispatcher::new);

    private DomainEventDispatcherProvider() {
        if (PROVIDER != null) {
            throw new AssertionError();
        }
    }

    public static EventDispatcherProvider getInstance() {
        return PROVIDER;
    }

    @Override
    public EventDispatcher<?> getService() {
        return DISPATCHERS.get();
    }

    private static class DomainEventDispatcher implements EventDispatcher<DomainEvent> {

        private final SubscriberRepository<Class<? extends DomainEvent>,
                EventSubscriber<? super DomainEvent>> subscriberRepository = new SubscriberRepository<>();
        private boolean lock;

        @Override
        public void submit(DomainEvent event) {
            Validation.requireNonNull(event, "No event be submit, because specified a null.");
            if (locked()) {
                submit(event);
            }
            try {
                lock();

                Class<? extends DomainEvent> eventType = event.getClass();
                Set<EventSubscriber<? super DomainEvent>> subscribers = subscriberRepository.find(eventType);
                if (!subscribers.isEmpty()) {
                    for (EventSubscriber<? super DomainEvent> subscriber : subscribers) {
                        subscriber.handle(event);
                    }
                }
            } finally {
                unlock();
            }
        }

        @Override
        public <U extends DomainEvent> EventDispatcher<DomainEvent> subscribe(Class<U> eventType,
                                                                              EventSubscriber<? super U> subscriber) {
            if (locked()) {
                subscribe(eventType, subscriber);
            }
            try {
                lock();
                // todo check safety
                //noinspection unchecked
                subscriberRepository.store(eventType, (EventSubscriber<? super DomainEvent>) subscriber);
                return this;
            } finally {
                unlock();
            }
        }

        @Override
        public void reset() {
            if (locked()) {
                reset();
            }
            try {
                lock();
                subscriberRepository.clear();
            } finally {
                unlock();
            }
        }

        boolean locked() {
            return lock;
        }

        void lock() {
            lock = true;
        }

        void unlock() {
            lock = false;
        }
    }
}
