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

import com.apehat.newyear.core.Dispatcher;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

/**
 * @author hanpengfei
 * @since 1.0
 */
public final class DispatcherServlet extends HttpServlet
        implements Dispatcher<HttpServletRequest, String, HttpServletRequestSubscriber> {
    private static final long serialVersionUID = -1144190263321641053L;

    @Override
    public void submit(HttpServletRequest request) {
        // when subscriber cannot be found. should publish {@code
        // ResponderNotFound} event
    }

    @Override
    public Dispatcher subscribe(String contentType, HttpServletRequestSubscriber subscriber) {
        return null;
    }

    @Override
    public void reset() {

    }
}

