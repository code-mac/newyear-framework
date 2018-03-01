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

package com.apehat.newyear.web;

import java.util.HashSet;
import java.util.Set;

/**
 * @author hanpengfei
 * @since 1.0
 */
public final class HttpRequestMethods {

    private final Set<HttpRequestMethod> methods;

    private HttpRequestMethods() {
        methods = new HashSet<>();
    }

    public static HttpRequestMethods append(HttpRequestMethod method) {
        HttpRequestMethods httpRequestMethods = new HttpRequestMethods();
        httpRequestMethods.methods.add(method);
        return httpRequestMethods;
    }

    public boolean contains(HttpRequestMethod method) {
        if (methods.isEmpty()) {
            return HttpRequestMethod.GET.equals(method);
        }
        return methods.contains(method);
    }
}
