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
import com.apehat.newyear.util.GenericUtils;
import com.apehat.newyear.validation.Validation;
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
    private static final Object LOCK = new Object();
    /**
     * The REPOSITORY be used to storeCustom {@code DispatcherProvider}
     */
    private static final Repository REPOSITORY = new Repository();
    /**
     * The dispatch policy be use to decision the Event.class and it's implementors
     * class. This is required, for method {@link #getDispatcher(Class)}.
     */
    private static EventBusDispatchPolicy dispatchPolicy;

    static {
        /*
         * Initialize the REPOSITORY by configured custom.  These custom，
         * as default implemention， support the even module running.
         * The configuration file at /META-INF/service/com.apehat.newyear.event.DispatcherProvider
         */
        ServiceLoader<DispatcherProvider> providers = ServiceLoader.load(DispatcherProvider.class);
        for (DispatcherProvider provider : providers) {
            getInstance().getRepository().storeBuildIn(provider);
        }
    }

    /**
     * Construct a {@code EventBus} instance.
     *
     * @throws AssertionError already have a instance.
     */
    private EventBus() {
        if (INSTANCE != null) {
            throw new AssertionError("Reflection unsupported.");
        }
    }

    /**
     * Returns the {@code EventBus} instance.
     *
     * @return the instance.
     */
    public static EventBus getInstance() {
        return INSTANCE;
    }

    /**
     * Sets an application's {@code EventBusDispatchPolicy}.
     * This method can be called at most once in a given Java Virtual Machine.
     * <p>
     * The {@code EventBusDispatchComparator} instance is used to
     * find a event dispatcher provider from a type token.
     *
     * @param policy a desired policy
     * @throws Error event bus dispatch policy
     */
    public static void setDispatchPolicy(EventBusDispatchPolicy policy) {
        Objects.requireNonNull(dispatchPolicy, "Must specific a dispatch policy.");
        synchronized (LOCK) {
            if (EventBus.dispatchPolicy != null) {
                throw new Error("Policy already defined.");
            }
            EventBus.dispatchPolicy = policy;
        }
    }

    /**
     * The method {@link EventDispatcher#submit(Event)} proxy.
     * <p>
     * This implemention by invoke {@link #getDispatcher(Class)} get a event dispatcher,
     * then use it to submit.
     *
     * @param event the event
     * @see EventDispatcher#submit(Event)
     * @see #getDispatcher(Class)
     */
    @Override
    public void submit(Event event) {
        submitHelper(event);
    }

    /**
     * The method {@link EventDispatcher#submit(Event)} proxy.
     * <p>
     * This implemention by invoke {@link #getDispatcher(Class)} get a event dispatcher,
     * then use it to submit.
     *
     * @param eventType  the event type
     * @param subscriber the subscriber
     * @param <U>        the type token
     * @see EventDispatcher#subscribe(Class, EventSubscriber)
     * @see #getDispatcher(Class)
     */
    @Override
    public <U extends Event> void subscribe(Class<U> eventType, EventSubscriber<? super U> subscriber) {
        getDispatcher(eventType).subscribe(eventType, subscriber);
    }

    /**
     * @throws UnsupportedOperationException the event bus does not support without type token
     */
    @Override
    public void subscribe(EventSubscriber<? super Event> subscriber) {
        throw new UnsupportedOperationException(getClass() + " does not support without type token.");
    }

    /**
     * @throws UnsupportedOperationException the event bus does not support reset.
     */
    @Override
    public void reset() {
        throw new UnsupportedOperationException(getClass() + " does not support to reset.");
    }

    /**
     * Register provider by {@code Event.class} as default provider. The provider must can provide
     * a {@code EventDispatcher}, that can dispatch all {@code Event}.
     * <p>
     * The default {@code DispatcherProvider} will be used to provide a {@code EventDispatcher}, when
     * cannot {@code getDispatcher}, will get the dispatcher by this provider.
     *
     * @param provider the provider to registered
     * @return this
     * @throws NullPointerException     specified provider is null
     * @throws IllegalArgumentException default {@code EventDispatcher} already be registered
     * @see #registerProvider(Class, DispatcherProvider)
     */
    public EventBus registerDefaultProvider(DispatcherProvider<EventDispatcher<Event>> provider) {
        registerProvider(Event.class, provider);
        return this;
    }

    /**
     * Register the {@code provider} by {@code eventType}.
     *
     * @param eventType the type token, as key.
     * @param provider  the provider as value.
     * @param <T>       the type of type token.
     * @return this
     * @throws NullPointerException     specified event type or provider is null
     * @throws IllegalArgumentException the provider of specified {@code eventType} already exists.
     * @see #replcaeProvider(Class, DispatcherProvider)
     */
    public <T extends Event> EventBus registerProvider(
            Class<T> eventType, DispatcherProvider<? extends EventDispatcher<? super T>> provider) {
        Objects.requireNonNull(eventType, "Must specific type token - eventType");
        Objects.requireNonNull(provider, "Must specific provider.");

        synchronized (LOCK) {
            DispatcherProvider<? extends EventDispatcher<? super T>> rp = getRepository().find(eventType);
            Validation.requireFalse(rp == null || getRepository().isBuildIn(rp),
                    "Already registered a provider [%s], by [%s]", rp, eventType);
        }
        getRepository().storeCustom(eventType, provider);
        return this;
    }

    /**
     * Mandatory register by specified {@code eventType} and {@code provider}. If the provider of event type
     * already exists, this will replace old provider. If non old provider, this will to register.
     * <p>
     * This method is not recommended, unless you must register the {@code provider}, and cannot rewrite old
     * code source.
     *
     * @param eventType the type token, as key.
     * @param provider  the provider as value.
     * @param <T>       the type of type token.
     * @return this
     * @throws NullPointerException specified event type or provider is null
     */
    public <T extends Event> EventBus replcaeProvider(
            Class<T> eventType, DispatcherProvider<? extends EventDispatcher<? super T>> provider) {
        Objects.requireNonNull(eventType, "Must specific type token - eventType");
        Objects.requireNonNull(provider, "Must specific provider.");

        getRepository().storeCustom(eventType, provider);
        return this;
    }

    /**
     * Returns a appropriate {@code EventDispatcher} by specified {@code eventType}.
     * If hadn't register a provider of the event type, or it's superclass, will try to
     * returns a default dispatcher. If don;t have default dispatcher to use, will throw
     * a {@link IllegalStateException}.
     *
     * @param eventType the type toke, to get dispatcher
     * @param <T>       the type of type token.
     * @return a dispatcher.
     * @throws NullPointerException  specified event type of policy is null
     * @throws IllegalStateException cannot found appropriate dispatcher.
     * @see #registerDefaultProvider(DispatcherProvider)
     */
    public <T extends Event> EventDispatcher<? super T> getDispatcher(Class<T> eventType) {
        return getDispatcher(eventType, new DefaultPolicy());
    }

    /**
     * Returns a appropriate {@code EventDispatcher} by specified {@code eventType} and
     * policy.
     * <p>
     * If hadn't register a provider of the event type, or it's superclass, will try to
     * returns a default dispatcher. If don;t have default dispatcher to use, will throw
     * a {@link IllegalStateException}.
     *
     * @param eventType the type toke, to get dispatcher
     * @param policy    the dispatch policy, to find appropriate dispatcher
     * @param <T>       the type of type token.
     * @return a dispatcher.
     * @throws NullPointerException  specified event type of policy is null
     * @throws IllegalStateException cannot found appropriate dispatcher.
     * @see EventBusDispatchPolicy
     */
    public <T extends Event> EventDispatcher<? super T> getDispatcher(Class<T> eventType,
                                                                      EventBusDispatchPolicy policy) {
        Objects.requireNonNull(eventType, "Cannot find provider by null");
        Objects.requireNonNull(policy, "Must specified a policy.");

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
            provider = getRepository().find(Event.class);
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

    /**
     * The providers repository, to store build in providers and custom providers
     */
    private static class Repository {

        private final Map<Class<? extends Event>, DispatcherProvider> builtIn = new ConcurrentHashMap<>(16);
        private final Map<Class<? extends Event>, DispatcherProvider> custom = new ConcurrentHashMap<>();

        /**
         * Store a build in provider. This method only should be called at initialization.
         *
         * @param provider a build in provider
         */
        private void storeBuildIn(DispatcherProvider<?> provider) {
            assert provider != null;

            EventDispatcher<?> dispatcher = provider.get();
            assert dispatcher != null;
            Class<? extends EventDispatcher> aClass = dispatcher.getClass();

            // type safe of EventDispatcher generic statement
            // because already statement EventDispatcher<T extends Event>
            @SuppressWarnings("unchecked") Class<? extends Event> parameterType =
                    (Class<? extends Event>) GenericUtils.getGenericParameters(aClass, EventDispatcher.class)[0];

            assert parameterType != null;

            builtIn.put(parameterType, provider);
        }

        /**
         * Store a custom provider, use the specified {@code eventType} as key.
         *
         * @param eventType a type token, as key. It's instance had extended of {@code Event}
         * @param provider  the provider, as value. Can provide a {@code EventDispatcher} to
         *                  handle {@code eventType} instance
         * @param <T>       the type token
         */
        private <T extends Event> void storeCustom(Class<? extends T> eventType,
                                                   DispatcherProvider<? extends EventDispatcher<? super T>> provider) {
            assert eventType != null;
            assert provider != null;

            DispatcherProvider absent = custom.put(eventType, provider);

            assert absent == null;
        }

        private <T extends Event> DispatcherProvider<? extends EventDispatcher<? super T>> find(Class<T> eventType) {
            assert eventType != null;
            DispatcherProvider provider = custom.get(eventType);
            if (provider == null) {
                provider = builtIn.get(eventType);
            }
            // type safe, can ensure by method put
            @SuppressWarnings("unchecked") DispatcherProvider<? extends EventDispatcher<? super T>> providerToUse = provider;
            return providerToUse;
        }

        /**
         * Determine whether the provider is belong to build in providers.
         *
         * @param provider the provider to check
         * @return true, if the provider is build in provider, otherwise, false.
         */
        private boolean isBuildIn(DispatcherProvider<?> provider) {
            return builtIn.containsValue(provider);
        }
    }
}
