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

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * todo 要求使用者提供一个过滤器，默认过是该类及其所有父类
 *
 * @author hanpengfei
 * @since 1.0
 */
public class SubscriberRepository<K, V extends Subscriber<?>> {

    private final Map<K, Set<V>> subscribers = new ConcurrentHashMap<>();

    public V store(K key, V value) {
        Set<V> registeredSubscribers = subscribers.get(key);
        if (registeredSubscribers == null) {
            registeredSubscribers = new LinkedHashSet<>();
        }
        registeredSubscribers.add(value);
        subscribers.put(key, registeredSubscribers);
        return value;
    }

    public Set<V> find(K key) {
        Set<V> registeredSubscribers = subscribers.get(key);
        if (registeredSubscribers == null) {
            registeredSubscribers = Collections.emptySet();
        }
        return Collections.unmodifiableSet(registeredSubscribers);
    }

    public void clear() {
        subscribers.clear();
    }
}
