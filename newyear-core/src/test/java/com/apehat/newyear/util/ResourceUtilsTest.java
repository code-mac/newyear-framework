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

import org.testng.annotations.Test;

import java.net.URL;

/**
 * @author hanpengfei
 * @since 1.0
 */
public class ResourceUtilsTest {

    @Test
    public void testGetURLByPackageName() throws Exception {
        String location = "com.apehat.newyear.util";
        URL url = ResourceUtils.getURL(location);

        assert ResourceUtils.isClassPathURL(url);
    }

    @Test
    public void testGetURLByPackagePath() throws Exception {
        String location = "com/apehat/newyear/util";
        URL url = ResourceUtils.getURL(location);

        assert ResourceUtils.isClassPathURL(url);
    }

    @Test
    public void testGetURLByJarFile() throws Exception {
        String location = "test.jar";
        URL url = ResourceUtils.getURL(location);

        assert ResourceUtils.isJarURL(url);
    }

    @Test
    public void testGetURLByAbsolutePath() throws Exception {
        String rt = "/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar";
        URL url = ResourceUtils.getURL(rt);

        assert ResourceUtils.isJarURL(url);
        assert ResourceUtils.isClassPathURL(url);
    }

    @Test
    public void testToJarURL() throws Exception {
    }

    @Test
    public void testIsFileURL() throws Exception {
    }

    @Test
    public void testIsJarFile() throws Exception {
    }

    @Test
    public void testIsJarURL() throws Exception {
    }

    @Test
    public void testIsJarFileURL() throws Exception {
    }

    @Test
    public void testExpandJarURL() throws Exception {
    }

    @Test
    public void testIsClassPathURL() throws Exception {
    }

    @Test
    public void testGetRealPath() throws Exception {
        String rt = "/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar";
        URL url = ResourceUtils.getURL(rt);
        String realPath = ResourceUtils.getRealPath(url);

        assert rt.equals(realPath);
    }

    @Test
    public void testEncode() throws Exception {
    }

    @Test
    public void testEncodeURL() throws Exception {
    }

    @Test
    public void testDecode() throws Exception {
    }

    @Test
    public void testDecodeURL() throws Exception {
    }

    @Test
    public void testGetFile() throws Exception {
    }

    @Test
    public void testUseCachesIfNecessary() throws Exception {
    }
}