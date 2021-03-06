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

/**
 * @author hanpengfei
 * @since 1.0
 */
public interface EventDispatcher<T extends Event> {

    void submit(T event);

    void subscribe(EventSubscriber<? super T> subscriber);

    <U extends T> void subscribe(Class<U> eventType, EventSubscriber<? super U> subscriber);

    void reset();
}
