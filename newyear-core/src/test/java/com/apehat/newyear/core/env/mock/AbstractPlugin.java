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

package com.apehat.newyear.core.env.mock;

import com.apehat.newyear.core.env.CommandLineArgs;
import com.apehat.newyear.core.env.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hanpengfei
 * @since 1.0
 */
public abstract class AbstractPlugin implements Plugin {

    private static final Logger logger = LoggerFactory.getLogger(MockPlugin.class);

    @Override
    public void launch(Class<?> entrance, CommandLineArgs args) {
        logger.info("AbstractPlugin [{}] be started.", getClass());
    }
}
