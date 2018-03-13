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

package com.apehat.newyear.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class IOUtils {

    private static final int DEFAULT_BUFFER_SIZE = 8192;

    private static final int MAX_BUFFER_SIZE = Integer.MAX_VALUE - 8;

    private IOUtils() {
    }

    public static byte[] readAllBytes(InputStream is) throws IOException {
        byte[] buf = new byte[DEFAULT_BUFFER_SIZE];
        int capacity = buf.length;
        int nread = 0;
        int n;
        for (; ; ) {
            // read to EOF which may read more or less than initial buffer size
            while ((n = is.read(buf, nread, capacity - nread)) > 0) {
                nread += n;
            }

            // if the last call to read returned -1, then we're done
            if (n < 0) {
                break;
            }

            // need to allocate a larger buffer
            if (capacity <= MAX_BUFFER_SIZE - capacity) {
                capacity = capacity << 1;
            } else {
                if (capacity == MAX_BUFFER_SIZE)
                    throw new OutOfMemoryError("Required array size too large");
                capacity = MAX_BUFFER_SIZE;
            }
            buf = Arrays.copyOf(buf, capacity);
        }
        return (capacity == nread) ? buf : Arrays.copyOf(buf, nread);
    }

    public static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        byte[] data = new byte[DEFAULT_BUFFER_SIZE];
        int len;

        while ((len = in.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, len);
        }
        buffer.flush();
        return buffer.toByteArray();
    }
}
