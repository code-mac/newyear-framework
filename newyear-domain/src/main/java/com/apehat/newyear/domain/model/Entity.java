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

package com.apehat.newyear.domain.model;

import com.apehat.newyear.validation.Validatable;

/**
 * The class Entity as superclass of the classes, when the class had be
 * definition as a have unique identification object.
 * <p>
 * When a class implemented Entity interface, the implementer will have an
 * unique identification. The identification will exists in
 * the implementer's whole life cycle.
 * <p>
 * If want to determine whether the two entities are the same. only need
 * to invoke like: {@code a.id().equals(b.id())}, or more simple
 * like: {@code a.equals(b)}.  They have same effect.
 *
 * @param <T> the type of id, for this entity (must be an immutable class)
 * @author hanpengfei
 * @implSpec The client must always ensure that the type {@code T} of id
 * must is absolutely immutable. If {@code T} can be changed, we can't
 * guarantee unique of id. And it also creates security holes.
 * @implSpec implementor must override {@link Object#equals(Object)}
 * and {@link Object#hashCode()}, and use id field as equals compare value.
 * And to comply with the java language equals and hashCode convention. i.e.
 * we must ensure, once the two entities have the same id, even though they
 * have different fields, they must be equals (In a general way, we think one
 * entity is another manifestation of other entities).
 * <p>
 * Why do you have to do this? Just imagine, in the different system, like
 * authentication system and others business system, when others system
 * depends on the authentication system, and they may through some technical
 * means pass user information, the user information may be different
 * (because they are from different system). At this moment, if not according
 * to the above agreement, it may cause the program to fail.
 */
public interface Entity<T extends ValueObject> extends Validatable<Entity<T>> {

    /**
     * Returns the unique identity of current entity.
     * <p>
     * As the identity of entity, every id must be immutable.
     * i.e. the value of id shouldn't be changed, at the entity's total life
     * cycle.
     *
     * @return the identity of current entity.
     */
    T id();
}
