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
import com.apehat.newyear.util.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hanpengfei
 * @since 1.0
 */
public final class SimpleEventDispatcherProviderRepository implements EventDispatcherProviderRepository<Event> {
    private static final Logger logger = LoggerFactory.getLogger(SimpleEventDispatcherProviderRepository.class);

    private final Map<Class<? extends Event>, EventDispatcherProvider<? super Event>> providers = new ConcurrentHashMap<>();

    private Comparator<? super Class<? extends Event>> comparator;

    @Override
    public void setComparator(Comparator<? super Class<? extends Event>> comparator) {
        this.comparator = Validation.requireNonNull(comparator, "Must specified a comparator");
    }

    @Override
    public void store(Class<? extends Event> eventType, EventDispatcherProvider<? super Event> provider) {
        Validation.requireNonNull(eventType, "Must specified event type");
        Validation.requireNonNull(provider, "Must specified provider");

        checkDispatch(eventType, provider);

        EventDispatcherProvider pre = providers.put(eventType, provider);

        if (logger.isInfoEnabled() && provider.equals(pre)) {
            logger.info("Registered provider [{}] by [{}]", provider, eventType);
        }
    }

    @Override
    public EventDispatcherProvider<? super Event> find(Class<? extends Event> eventType) {
        Validation.requireNonNull(eventType, "Cannot find provider by null");

        EventDispatcherProvider<? super Event> provider = providers.get(eventType);

        if (provider == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("No provider of [{}].", eventType);
            }

            // find all superclasses and interfaces
            Set<Class<Event>> supers = ClassUtils.getClassesWithinBounds(eventType, Event.class);
            // supers positive sorting
            Comparator<? super Class<Event>> comparator = (this.comparator != null)
                    ? this.comparator : new DefaultEventClassesComparator();
            Collection<Class<Event>> sortedSupers = ClassUtils.sort(supers, comparator);

            for (Class<Event> superType : sortedSupers) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Try to find the provider by [{}]", superType);
                }

                provider = providers.get(superType);
                if (provider != null) {
                    break;
                }
            }
        }

        // find default provider.
        // if hadn't register default provider - provider be set to null
        return provider == null ? providers.get(Event.class) : provider;
    }

    /**
     * @param eventType the event type, that expected can submit by the
     *                  {@code Event.EventDispatcher}, what is provided by specified provider.
     * @param provider  the provider to be checkPermission.
     * @throws RegisterException the event publisher of specified provider
     *                           cannot submit the type of specified eventType.
     */
    private void checkDispatch(Class<? extends Event> eventType, EventDispatcherProvider provider) {
        EventDispatcher<?> eventDispatcher = provider.getService();
        assert eventDispatcher != null;
        Class<? extends EventDispatcher> aClass = eventDispatcher.getClass();
        Class<?> parameter = ReflectionUtils.getGenericParameters(aClass, EventDispatcher.class)[0];
        if (!parameter.isAssignableFrom(eventType)) {
            throw new RegisterException(String.format(
                    "%s cannot submit to %s (provided by %s)", eventType, aClass, provider.getClass()));
        }
    }

    private class DefaultEventClassesComparator implements Comparator<Class<? extends Event>> {
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
}
