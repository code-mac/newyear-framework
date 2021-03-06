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

package com.apehat.newyear.context.annotation;

import java.lang.annotation.*;

/**
 * @author hanpengfei
 * @since 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
public @interface Inject {

    /**
     * The inject of the annotated. default is {@link InjectPolicy#BY_CLASS}.
     *
     * @return the inject of the annotated.
     */
    InjectPolicy policy() default InjectPolicy.BY_CLASS;

    boolean required() default true;
}
