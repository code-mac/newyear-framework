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

package com.apehat.newyear.core.io;

import com.apehat.newyear.util.IOUtils;

import java.io.IOException;

/**
 * @author hanpengfei
 * @since 1.0
 */
public abstract class AbstractResource implements Resource {

    private byte[] cis;

    private synchronized byte[] cachedByteArray() throws IOException {
        if (cis == null) {
            cis = IOUtils.toByteArray(getInputStream());
        }
        return cis;
    }

    @Override
    public byte[] getByteArray() throws IOException {
        if (cis == null) {
            cis = cachedByteArray();
        }
        return cis;
    }
}
