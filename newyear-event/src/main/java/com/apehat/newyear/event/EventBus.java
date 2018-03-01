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

import java.util.Comparator;

/**
 * @author hanpengfei
 * @since 1.0
 */
public interface EventBus<T extends Event> extends EventDispatcher<T> {

    /**
     * Set event classes comparator. The comparator will be used to match
     * provider when call {@link #getDispatcher(Class)}.
     *
     * @param comparator the comparator to be used.
     * @return this
     */
    EventBus<T> setComparator(Comparator<Class<? extends Event>> comparator);

    /**
     * The default event type of current {@code EventBus}.
     * This type be used to register default provider
     *
     * @return the default
     * @see #registerDefaultProvider(EventDispatcherProvider)
     */
    Class<T> defaultEventType();

    /**
     * Register specified provider as default provider. The provider be used
     * at {@link #getDispatcher(Class)}, when cannot find appropriate
     * provider by specified {@code eventType}.
     * If default provider already exits, will replace old provider by this
     * provider.
     *
     * @param provider the provider will be registered
     * @return this
     * @throws NullPointerException specified is null
     * @throws RegisterException    the event publisher of specified provider
     *                              cannot submit the type of specified eventType.
     * @see #registerProvider(Class, EventDispatcherProvider)
     * @see #getDispatcher(Class)
     * @see #defaultEventType()
     */
    EventBus<T> registerDefaultProvider(EventDispatcherProvider<? super T> provider);

    /**
     * Register specified provider to by specified specified event type.
     *
     * @param eventType the key to register
     * @param provider  the value to register
     * @return this
     * @throws NullPointerException specified event type of provider is null
     * @throws RegisterException    the event publisher of specified provider
     *                              cannot submit the type of specified eventType.
     */
    EventBus<T> registerProvider(Class<? extends T> eventType, EventDispatcherProvider<? super T> provider);

    /**
     * Get the {@code Event.EventDispatcher} by specified event type.
     * <p>
     * This implemention depend of {@link EventDispatcherProvider#getService()}
     * So, in fact, this method is find provider. If cannot find provider by
     * the event type, will try to find the provider by the superclass (or
     * interfaces) of the event type.
     * If still cannot find, will filling back to find the provider by default
     * event type. If default provider is not exists, will throw
     * {@link IllegalStateException}.
     * <p>
     * Note: in general, this method will not return null value. But this
     * agreement depends on you provided {@code EventDispatcherProvider} will
     * not return null.
     *
     * @param eventType the event type, will be used to find publisher
     * @return a event publisher
     * @throws NullPointerException  specified eventType is null
     * @throws IllegalStateException cannot find dispatcher by specified event type
     * @see EventDispatcherProvider#getService()
     */
    EventDispatcher<? super T> getDispatcher(Class<? extends T> eventType);

    /**
     * Submit an event to event bus. The event bus only is proxy. i.e. event bus will
     * find a appropriate {@code EventDispatcher} by the type of specified event, then
     * use this dispatcher to do submit.
     *
     * @param event the event will be submit
     * @throws NullPointerException  specified event is null
     * @throws IllegalStateException cannot find dispatcher by specified event
     * @see #getDispatcher(Class)
     */
    @Override
    default void submit(T event) {
        // the class of event, always same as itself class
        @SuppressWarnings("unchecked") EventDispatcher<? super T> dispatcher =
                getDispatcher((Class<? extends T>) event.getClass());
        assert dispatcher != null;
        dispatcher.submit(event);
    }

    /**
     * Subscribe specified {@code eventType} by specified subscriber to a appropriate
     * {@code EventDispatcher} form registered {@code EventDispatcherProvider}.
     *
     * @param eventType  the eventType will be subscriber
     * @param subscriber the subscriber, to subscribe specified {@code eventType}
     * @return this
     */
    @Override
    default <U extends T> EventDispatcher<T> subscribe(Class<U> eventType, EventSubscriber<? super U> subscriber) {
        EventDispatcher dispatcher = getDispatcher(eventType);
        assert dispatcher != null;
        // noinspection unchecked
        dispatcher.subscribe(eventType, subscriber);
        return this;
    }

    /**
     * Reset all {@code EventDispatcher} form registered {@code EventDispatcherProvider}.
     *
     * @throws UnsupportedOperationException if implemention un supported to reset
     */
    @Override
    default void reset() {
        throw new UnsupportedOperationException("The event bus unsupported reset");
    }


}
