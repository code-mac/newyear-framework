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

package com.apehat.newyear.bean;

import com.apehat.newyear.validation.exception.IllegalConflictException;

/**
 * @author hanpengfei
 * @since 1.0
 */
public interface BeanFactory {

    /**
     * Get bean by class. if specified class is interface of abstract class,
     * will return its implementor. If the implementor of specified class only
     * have one, be managed by container, will return the instance of this
     * implementor; else if the container managed implementor more than one of
     * this class, will return the primary implementor, if have and only one.
     * else, will throw {@link IllegalConflictException}.
     *
     * @param aClass the class to get bean
     * @param <T>    the type of class
     * @return a instance of class.
     * @throws BeanNotFoundException    the bean of specified class not found.
     * @throws IllegalConflictException the bean of specified class more than one,
     *                                  and no any bean is primary, or primary bean
     *                                  of specified class more than one.
     */
    <T> T getBean(Class<T> aClass);

    /**
     * Get bean by specified id. The id must be globally unique. If specified
     * and id of interface or abstract class, this won't return any object,
     * and will throw {@link BeanNotFoundException}. If the bean of specified
     * id have more than one, even if one of them is primary, will throw the
     * {@link IllegalConflictException}, too.
     *
     * @param id the id to get bean
     * @return a instance of id
     * @throws BeanNotFoundException    the bean of specified id not found
     * @throws IllegalConflictException the bean of specified id more than one,
     *                                  and no any bean is primary, or primary
     *                                  bean of specified id more than one.
     */
    Object getBean(String id);
}
