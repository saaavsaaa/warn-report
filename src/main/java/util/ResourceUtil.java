package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by aaa on 18-1-30.
 */
public class ResourceUtil {
    
    public static byte[] loadResource(String path) throws IOException {
        InputStream is = ClassLoader.getSystemResourceAsStream(path);
        try {
            return loadFile(is);
        } finally {
            if (is == null) {
                System.err.println("Unable to load resource: " + path);
            } else {
                is.close();
            }
        }
    }
    
    public static byte[] loadFile(String path) throws IOException {
        File f = new File(path);
        try (InputStream is = new FileInputStream(f.getAbsoluteFile())) {
            byte[] data = loadFile(is);
            return data;
        }
    }
    
    private static byte[] loadFile(InputStream is) throws IOException {
        byte[] result = new byte[0];
        
        byte[] buffer = new byte[1024];
        
        int read = -1;
        while ((read = is.read(buffer)) > 0) {
            byte[] newResult = new byte[result.length + read];
            System.arraycopy(result, 0, newResult, 0, result.length);
            System.arraycopy(buffer, 0, newResult, result.length, read);
            result = newResult;
        }
        return result;
    }
}
