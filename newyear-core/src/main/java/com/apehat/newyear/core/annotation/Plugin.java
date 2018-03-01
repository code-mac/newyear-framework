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

package com.apehat.newyear.core.annotation;

import java.lang.annotation.*;

/**
 * 被注解的插件声明其依赖的插插件
 * 每一个插件中必须有一个一个方法被注解了 {@link Starter}.
 * 系统根据该方法所需要的参数类型，从其声明的依赖中进行查找
 *
 * @author hanpengfei
 * @see Starter
 * @since 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Plugin {

    Class<?>[] dependencies() default {};
}
