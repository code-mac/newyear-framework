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
 * 这个注解只被用于类注解了 Plugin 后，有多个构造器的情况
 * <p>
 * 插件的启动点, 当被注解到一个构造器上时，系统会根据构造器所需要的参数，
 * 从其声明的依赖中进行查找，并进行注入。
 * 如果被注解了 {@code Plugin} 的类没有构造器注解该注解，则系统默认该类只有一个构造器。
 * 即，系统会进行反射查找该类的所有构造器，如果找到的构造器数量大于 1，则系统会抛出异常
 * 如果系统只找到一个构造器，则会调用该构造器，进行插件的实例化。如果该构造器需要参数，则
 * 系统会在该插件声明的依赖中查找相应的实例，如果查找不到，则会抛出异常
 * <p>
 * 例如：
 * <pre>
 *     {@code @Plugin}
 *     type class SimplePlugin {
 *     }
 * </pre>
 * 这种情况是合理的。虽然该类没有声明任何的启动点，但实例化该插件也不需要任何的其他插件实例。
 * 也就是说，该插件对它所声明的依赖插件，只是启动顺序的依赖，而不是配置上的显式依赖
 *
 * @author hanpengfei
 * @see Plugin
 * @since 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CONSTRUCTOR)
public @interface Starter {
}
