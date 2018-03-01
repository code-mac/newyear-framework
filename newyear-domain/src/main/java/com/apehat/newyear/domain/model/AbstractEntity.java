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

import com.apehat.newyear.validation.Validator;

/**
 * The class AbstractEntity extends form {@link Entity}, as entity default
 * implemention.
 *
 * @author hanpengfei
 * @since 1.0
 */
public abstract class AbstractEntity<T> implements Entity<T> {

    private final T id;

    /**
     * Construct an entity by specified id.
     *
     * @param id the id of the entity. As the unique identity. the entire
     *           life cycle that exists in the entity and stay the same.
     * @throws IllegalArgumentException if specified id is null.
     * @implNote The client must always ensure the id is immutable.
     */
    protected AbstractEntity(T id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must be specified");
        }
        this.id = id;
    }

    @Override
    public final T id() {
        return id;
    }

    /**
     * Validate current entity by specified validator.
     * <p>
     * In general, the method be invoke, when client want to validate
     * the integrity of the current entity, and the validity of the fields.
     *
     * @param validator what will be used to validate
     * @throws NullPointerException specified validator is null
     * @see Validator
     */
    @Override
    public void validate(Validator<Entity<T>> validator) {
        validator.validate(this);
    }

    /**
     * Determine whether this entity is same as other obj.
     *
     * @param obj the other object to be checkPermission
     * @return true, if obj is entity and the id of current entity is
     * equals obj's id; otherwise, false
     */
    @Override
    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Entity) {
            Entity otherEntity = (Entity) obj;
            return otherEntity.id().equals(id);
        }
        return false;
    }

    /**
     * Returns the hash code of current entity.
     *
     * @return current entity's hash code
     */
    @Override
    public final int hashCode() {
        /* 31 * 237 + id.hashCode(); */
        return 7347 + id.hashCode();
    }
}
