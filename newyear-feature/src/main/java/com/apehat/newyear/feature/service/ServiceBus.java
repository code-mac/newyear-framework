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

package com.apehat.newyear.feature.service;

import java.util.*;

/**
 * @author hanpengfei
 * @since 1.0
 */
public final class ServiceBus {

    private static final ServiceBus SERVICE_BUS = new ServiceBus();

    private ServiceBus() {
    }

    public static ServiceBus getInstance() {
        return SERVICE_BUS;
    }

    public Collection<Service> getServices() {
        return Starter.getBuildInServices();
    }

    private static class Starter {

        private static final Set<Service> SERVICES;

        static {
            Set<Service> loadedService = new HashSet<>();
            ServiceLoader<Service> services = ServiceLoader.load(Service.class);
            for (Service next : services) {
                loadedService.add(next);
            }
            SERVICES = Collections.unmodifiableSet(loadedService);
        }

        static Collection<Service> getBuildInServices() {
            return SERVICES;
        }
    }
}
