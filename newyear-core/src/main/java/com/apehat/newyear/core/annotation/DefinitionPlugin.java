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

import java.util.Arrays;
import java.util.List;

/**
 * @author hanpengfei
 * @since 1.0
 */
public interface DefinitionPlugin extends Comparable<DefinitionPlugin> {

    /**
     * 被注解了插件的类，该类将被框架进行管理与启动
     *
     * @return 被注解了插件的类
     */
    Class<?> type();

    /**
     * 该插件的依赖，如果没有，则为一个空的数组
     *
     * @return 该插件的依赖
     */
    Class<?>[] dependencies();

    /**
     * 启动点所需的参数类型
     *
     * @return 启动点所需的参数类型，如果没有，则为一个空的数组
     */
    Class<?>[] starterArgsType();

    /**
     * 进行插件之间的比较，如果两个被定义的插件实力相同，则返回0；如果当前的插件依赖于
     * 传入的插件，则返回 1; 否则返回 -1
     *
     * @param o 被用来于当前插件进行比较的插件
     * @return 0, 1, -1
     */
    @Override
    default int compareTo(DefinitionPlugin o) {
        assert o != null;
        if (o == this) {
            return 0;
        }
        List<Class<?>> depends = Arrays.asList(dependencies());
        if (depends.contains(o.getClass())) {
            return 1;
        }
        return -1;
    }
}
