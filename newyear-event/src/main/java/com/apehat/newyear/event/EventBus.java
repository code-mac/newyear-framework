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
import com.apehat.newyear.util.ClassUtils;
import com.apehat.newyear.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author hanpengfei
 * @since 1.0
 */
public final class EventBus implements EventDispatcher<Event> {

    /**
     * The single instance of class {@code EventBus}
     */
    private static final EventBus INSTANCE = new EventBus();
    /**
     * The policy lock.
     */
    private static final Object POLICY_LOCK = new Object();
    /**
     * The dispatch policy be use to decision the Event.class and it's implementors
     * class. This is required, for method {@link #getDispatcher(Class)}.
     */
    private static EventBusDispatchPolicy dispatchPolicy;

    static {
        /*
         * Initialize the repository by configured providers.  These providers，
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

    /**
     * The repository be used to store {@code DispatcherProvider}
     */
    private final Repository repository = new Repository();

    private EventBus() {
        if (INSTANCE != null) {
            throw new AssertionError("Reflection unsupported.");
        }
    }

    public static EventBus getInstance() {
        return INSTANCE;
    }

    private static EventBusDispatchPolicy getDispatchPolicy() {
        return dispatchPolicy == null ? new DefaultPolicy() : dispatchPolicy;
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

    private Repository getRepository() {
        return repository;
    }

    public EventBus registerDefaultProvider(DispatcherProvider<EventDispatcher<Event>> provider) {
        registerProvider(Event.class, provider);
        return this;
    }

    public <T extends Event> EventBus registerProvider(
            Class<T> eventType, DispatcherProvider<? extends EventDispatcher<? super T>> provider) {
        getRepository().store(eventType, provider);
        return this;
    }

    public <T extends Event> EventDispatcher<? super T> getDispatcher(Class<T> eventType) {
        DispatcherProvider<? extends EventDispatcher<? super T>> provider = getRepository().find(eventType);
        if (provider == null) {
            throw new IllegalStateException("No provider of: " + eventType);
        }
        return provider.get();
    }

    @Override
    public void submit(Event event) {
        submitHelper(event);
    }

    @Override
    public <U extends Event> void subscribe(Class<U> type, EventSubscriber<? super U> subscriber) {
        EventDispatcher<? super U> dispatcher = getDispatcher(type);
        dispatcher.subscribe(type, subscriber);
    }

    @Override
    public Class<Event> type() {
        return Event.class;
    }

    private <T extends Event> void submitHelper(T event) {
        // Type safe, the class of event convert to Class<T>
        // because, <T extends Event>
        @SuppressWarnings("unchecked") Class<T> aClass = (Class<T>) event.getClass();
        EventDispatcher<? super T> dispatcher = getDispatcher(aClass);
        assert dispatcher != null;
        dispatcher.submit(event);
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

        private static final Logger logger = LoggerFactory.getLogger(Repository.class);

        private final Map<Class<? extends Event>, DispatcherProvider> map = new HashMap<>();

        private <T extends Event> void store(
                Class<? extends T> key, DispatcherProvider<? extends EventDispatcher<? super T>> value) {
            Objects.requireNonNull(key, "Must specified event type");
            Objects.requireNonNull(value, "Must specified provider");

            map.put(key, value);
        }

        private <T extends Event> DispatcherProvider<? extends EventDispatcher<? super T>> find(Class<T> eventType) {
            Objects.requireNonNull(eventType, "Cannot find provider by null");

            DispatcherProvider provider = map.get(eventType);

            if (provider == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("No provider of [{}].", eventType);
                }

                // find all superclasses and interfaces
                Set<Class<Event>> supers = ClassUtils.getClassesWithinBounds(eventType, Event.class);
                // supers positive sorting
                Collection<Class<Event>> sortedSupers = ClassUtils.sort(supers, getDispatchPolicy());

                for (Class<Event> superType : sortedSupers) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Try to find the provider by [{}]", superType);
                    }

                    provider = map.get(superType);
                    if (provider != null) {
                        break;
                    }
                }
            }

            // find default provider.
            // if hadn't register default provider - provider be set to null
            // can ensure by method put.
            @SuppressWarnings("unchecked") DispatcherProvider<? extends EventDispatcher<? super T>> providerToUse =
                    (provider == null) ? map.get(Event.class) : provider;
            return providerToUse;
        }

        /**
         * @param eventType the event type, that expected can submit by the
         *                  {@code Event.EventDispatcher}, what is provided by specified provider.
         * @param provider  the provider to be checkPermission.
         * @throws RegisterException the event publisher of specified provider
         *                           cannot submit the type of specified eventType.
         */
        private void checkDispatch(Class<? extends Event> eventType, DispatcherProvider provider) {
            EventDispatcher<?> eventDispatcher = provider.get();
            assert eventDispatcher != null;
            Class<? extends EventDispatcher> aClass = eventDispatcher.getClass();
            Class<?> parameter = ReflectionUtils.getGenericParameters(aClass, EventDispatcher.class)[0];
            if (!parameter.isAssignableFrom(eventType)) {
                throw new RegisterException(String.format(
                        "%s cannot submit to %s (provided by %s)", eventType, aClass, provider.getClass()));
            }
        }
    }
}
