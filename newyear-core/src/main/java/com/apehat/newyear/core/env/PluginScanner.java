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

package com.apehat.newyear.core.env;

/**
 * @author hanpengfei
 * @since 1.0
 */
public interface PluginScanner {

    /**
     * Set the type scan packages. If {@code AbstractPlugin Auto Scan} be close,
     * this config will be ignored. If this hadn't be set, type will scan
     * form {@code classpath}.
     *
     * @param packageNames the packages name will be used auto scan type
     * @return this
     * @see ConfigurableEnvironment#closePluginAutoScan()
     */
    ConfigurableEnvironment setPluginScanPackages(String... packageNames);

    /**
     * Set the ignored type scan packages
     *
     * @param ignoredPackageNames the package will not be scan.
     * @return this
     * @see ConfigurableEnvironment#closePluginAutoScan()
     */
    ConfigurableEnvironment setPluginScanIgnore(String... ignoredPackageNames);
}
