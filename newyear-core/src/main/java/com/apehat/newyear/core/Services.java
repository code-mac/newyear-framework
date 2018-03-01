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

package com.apehat.newyear.core;

import com.apehat.newyear.lang.NonNull;

/**
 * @param <T> the type of service
 * @param <K> provider key
 * @param <V> the type of provider, the provider must can provide service
 * @author hanpengfei
 * @since 1.0
 */
public interface Services<T, K, V extends Provider<?>> {

    /**
     * Register a service provider by specified key. If the provider is
     * uniqueness, and specified already be registered, will throw
     * {@link IllegalConflictException}.
     *
     * @param key      the key to find provider
     * @param provider the provider to register
     * @return this
     * @throws RegisterException other registration constraints are not met.
     */
    Services registerProvider(K key, V provider);

    /**
     * Returns a new service by specified key.
     *
     * @param key the key be used find service.
     * @return null, if service cannot be found. otherwise, a service instance.
     */
    @NonNull
    T newService(K key);
}
