package util;

import java.io.*;

/**
 * Created by aaa on 18-1-30.
 */
public class ResourceUtil {
    
    //rm -rf ClassCode.class
    public static void write(String path, byte[] data) {
        try {
            File file = new File(path);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
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
    
    public static InputStream getInput(String path) throws IOException {
        File f = new File(path);
        return new FileInputStream(f.getAbsoluteFile());
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
