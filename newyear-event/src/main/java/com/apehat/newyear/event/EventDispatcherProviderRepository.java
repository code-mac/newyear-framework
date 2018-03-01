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

import com.apehat.newyear.core.NullArgumentException;
import com.apehat.newyear.core.ProviderRepository;
import com.apehat.newyear.core.RegisterException;

import java.util.Comparator;

/**
 * @author hanpengfei
 * @since 1.0
 */
public interface EventDispatcherProviderRepository<T extends Event>
        extends ProviderRepository<Class<? extends T>, EventDispatcherProvider<? super T>> {
    /**
     * Store provided value by key, to repository.
     *
     * @param eventType the event type as key, to be register
     * @param provider  the provider as value, to be register
     * @throws NullArgumentException specified eventType of provider is null
     * @throws RegisterException     the event publisher of specified provider
     *                               cannot submit the type of specified eventType.
     */
    @Override
    void store(Class<? extends T> eventType, EventDispatcherProvider<? super T> provider);

    /**
     * Get the provider by specified eventType
     *
     * @param eventType the event type be used to find provider
     * @return null, provider cannot be found
     */
    @Override
    EventDispatcherProvider<? super T> find(Class<? extends T> eventType);


    void setComparator(Comparator<? super Class<? extends Event>> comparator);
}
