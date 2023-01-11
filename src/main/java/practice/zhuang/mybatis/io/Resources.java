package practice.zhuang.mybatis.io;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Objects;

/**
 * @author: ZhuangZG
 * @date 2023/1/10 14:17
 * @Description:
 */
public class Resources {

    public static InputStream readAsStream(String resource) throws IOException {
        ClassLoader[] classLoaders = getClassLoaders();
        for (ClassLoader classLoader : classLoaders) {
            InputStream inputStream = classLoader.getResourceAsStream(resource);
            if (null != inputStream) {
                return inputStream;
            }
        }
        throw new IOException("Could not find resource " + resource);
    }

    public static Reader readAsReader(String path) throws IOException {
        return new InputStreamReader(readAsStream(path));
    }

    public static ClassLoader[] getClassLoaders() {
        return new ClassLoader[]{
                Thread.currentThread().getContextClassLoader(),
                ClassLoader.getSystemClassLoader()
        };
    }

    public static Class<?> classForName(String className) throws Exception {
        return Class.forName(className);
    }
}
