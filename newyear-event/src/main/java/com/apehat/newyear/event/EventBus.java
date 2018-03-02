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

import com.apehat.newyear.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hanpengfei
 * @since 1.0
 */
public final class EventBus implements EventDispatcher<Event> {

    private static final Logger logger = LoggerFactory.getLogger(EventBus.class);

    /**
     * The single instance of class {@code EventBus}
     */
    private static final EventBus INSTANCE = new EventBus();
    /**
     * The policy lock.
     */
    private static final Object POLICY_LOCK = new Object();
    /**
     * The REPOSITORY be used to store {@code DispatcherProvider}
     */
    private static final Repository REPOSITORY = new Repository();
    /**
     * The dispatch policy be use to decision the Event.class and it's implementors
     * class. This is required, for method {@link #getDispatcher(Class)}.
     */
    private static EventBusDispatchPolicy dispatchPolicy;

    static {
        /*
         * Initialize the REPOSITORY by configured providers.  These providers，
         * as default implemention， support the even module running.
         * The configuration file at /META-INF/service/com.apehat.newyear.event.DispatcherProvider
         */
        ServiceLoader<DispatcherProvider> providers = ServiceLoader.load(DispatcherProvider.class);
        for (DispatcherProvider provider : providers) {
            DispatcherProvider<? extends EventDispatcher<? super Event>> genericProvider =
                    (DispatcherProvider<? extends EventDispatcher<? super Event>>) provider;
            EventDispatcher<? extends Event> dispatcher = genericProvider.get();
            Class<? extends Event> type = dispatcher.type();
            getInstance().registerProvider(type, genericProvider);
        }
    }

    private EventBus() {
        if (INSTANCE != null) {
            throw new AssertionError("Reflection unsupported.");
        }
    }

    public static EventBus getInstance() {
        return INSTANCE;
    }

    /**
     * Sets an application's {@code EventBusDispatchPolicy}.
     * This method can be called at most once in a given Java Virtual
     * Machine.
     * <p>
     * The {@code EventBusDispatchComparator} instance is used to
     * find a event dispatcher provider from a type token.
     *
     * @param policy a desired policy
     * @throws Error event bus dispatch policy
     */
    public static void setDispatchPolicy(EventBusDispatchPolicy policy) {
        Objects.requireNonNull(dispatchPolicy, "Must specific a dispatchPolicy.");
        synchronized (POLICY_LOCK) {
            if (EventBus.dispatchPolicy != null) {
                throw new Error("Policy already defined.");
            }
            EventBus.dispatchPolicy = policy;
        }
    }

    @Override
    public void submit(Event event) {
        submitHelper(event);
    }

    @Override
    public <U extends Event> void subscribe(Class<U> type, EventSubscriber<? super U> subscriber) {
        getDispatcher(type).subscribe(type, subscriber);
    }

    @Override
    public Class<Event> type() {
        return Event.class;
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException(getClass() + " does not support to reset.");
    }

    public EventBus registerDefaultProvider(DispatcherProvider<EventDispatcher<Event>> provider) {
        registerProvider(Event.class, provider);
        return this;
    }

    public <T extends Event> EventBus registerProvider(
            Class<T> eventType, DispatcherProvider<? extends EventDispatcher<? super T>> provider) {
        Objects.requireNonNull(eventType, "Must specific type token - eventType");
        Objects.requireNonNull(provider, "Must specific provider.");

        getRepository().store(eventType, provider);
        return this;
    }

    public <T extends Event> EventDispatcher<? super T> getDispatcher(Class<T> eventType) {
        return getDispatcher(eventType, new DefaultPolicy());
    }

    public <T extends Event> EventDispatcher<? super T> getDispatcher(Class<T> eventType, EventBusDispatchPolicy policy) {
        Objects.requireNonNull(eventType, "Cannot find provider by null");

        DispatcherProvider<? extends EventDispatcher<? super T>> provider = getRepository().find(eventType);

        if (provider == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("No provider of [{}].", eventType);
            }

            // find all superclasses and interfaces
            Set<Class<Event>> supers = ClassUtils.getClassesWithinBounds(eventType, Event.class);
            // supers positive sorting
            Collection<Class<Event>> sortedSupers = ClassUtils.sort(supers, policy);

            for (Class<Event> superType : sortedSupers) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Try to find the provider by [{}]", superType);
                }

                provider = getRepository().find(superType);
                if (provider != null) {
                    break;
                }
            }
        }

        // cannot found provider - try to find default provider
        if (provider == null) {
            provider = getRepository().find(type());
        }

        // don't have default provider - throw exception.
        if (provider == null) {
            throw new IllegalStateException("No provider of: " + eventType);
        }

        return provider.get();
    }


    private <T extends Event> void submitHelper(T event) {
        // Type safe, method limit is <T extends Event>
        @SuppressWarnings("unchecked") Class<T> aClass = (Class<T>) event.getClass();
        EventDispatcher<? super T> dispatcher = getDispatcher(aClass);
        assert dispatcher != null;
        dispatcher.submit(event);
    }

    private Repository getRepository() {
        return REPOSITORY;
    }

    private static class DefaultPolicy implements EventBusDispatchPolicy {
        @Override
        public int compare(Class<? extends Event> o1, Class<? extends Event> o2) {
            if (o1 == o2) {
                return 0;
            }
            if (o1 == Event.class) {
                return 1;
            }
            if (o1 == AbstractEvent.class) {
                return 1;
            }
            if (o1.isAssignableFrom(o2)) {
                return 1;
            }
            return -1;
        }
    }

    private static class Repository {

        private final Map<Class<? extends Event>, DispatcherProvider> providers = new ConcurrentHashMap<>();

        private <T extends Event> void store(
                Class<? extends T> eventType, DispatcherProvider<? extends EventDispatcher<? super T>> provider) {
            assert eventType != null;
            assert provider != null;

            providers.put(eventType, provider);
        }

        private <T extends Event> DispatcherProvider<? extends EventDispatcher<? super T>> find(Class<T> eventType) {
            assert eventType != null;
            // type safe, can ensure by method put
            @SuppressWarnings("unchecked") DispatcherProvider<? extends EventDispatcher<? super T>> provider =
                    providers.get(eventType);
            return provider;
        }
    }
}
