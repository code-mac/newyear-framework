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

package com.apehat.newyear.lang;

import java.lang.annotation.*;

/**
 * The annotation {@code NonNull} be annotated to method and parameter, to
 * validate the return value of method (or the incoming parameter) isn't null.
 * <p>
 * If this be annotated to method, and method return null value, will throw
 * {@link AssertionError}. If the return type statement of method is
 * void, this annotation will be ignore.
 * <p>
 * Or, if this be annotated to parameter, after method (or constructor) be
 * invoked, will validate be annotated parameters, at first. If there are
 * null, will throw {@link IllegalArgumentException}
 *
 * @author hanpengfei
 * @since 1.0
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
public @interface NonNull {
}
