package cn.tellwhy.run;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Created by aaa on 18-4-16.
 * http://chenzehe.iteye.com/blog/1967070
 */
public class RunJar {
    public static void main(String[] args) throws Throwable {
        String usage = "RunJar jarFile [mainClass] args...";
        
        if (args.length < 1) {
            System.err.println(usage);
            System.exit(-1);
        }
        int firstArg = 0;
        String fileName = args[firstArg++];
        File file = new File(fileName);
        
        // 从jar文件MANIFEST.MF中读取Main-Class
        String mainClassName = null;
        JarFile jarFile;
        try {
            jarFile = new JarFile(fileName);
        } catch (IOException io) {
            throw new IOException("Error opening job jar: " + fileName)
                    .initCause(io);
        }
        
        Manifest manifest = jarFile.getManifest();
        if (manifest != null) {
            mainClassName = manifest.getMainAttributes().getValue("Main-Class");
        }
        jarFile.close();
        
        if (mainClassName == null) {
            if (args.length < 2) {
                System.err.println(usage);
                System.exit(-1);
            }
            // 如果MANIFEST.MF未设置Main-Class,则用户输入
            mainClassName = args[firstArg++];
        }
        mainClassName = mainClassName.replaceAll("/", ".");
        final File workDir = File.createTempFile("unjar", "",
                new File(System.getProperty("user.dir")));
        workDir.delete();
        workDir.mkdirs();
        if (!workDir.isDirectory()) {
            System.err.println("Mkdirs failed to create " + workDir);
            System.exit(-1);
        }
        
        // 解压jar文件
        unJar(file, workDir);
        
        // 设置classPath
        ArrayList<URL> classPath = new ArrayList<URL>();
        classPath.add(new File(workDir + "/").toURL());
        classPath.add(file.toURL());
        ClassLoader loader = new URLClassLoader(classPath.toArray(new URL[0]));
        Thread.currentThread().setContextClassLoader(loader);
        
        // 利用反射取得class和method
        Class<?> mainClass = Class.forName(mainClassName, true, loader);
        Method main = mainClass.getMethod("main", new Class[] { Array
                .newInstance(String.class, 0).getClass() });
        String[] newArgs = Arrays.asList(args).subList(firstArg, args.length)
                .toArray(new String[0]);
        try {
            // 运行方法
            main.invoke(null, new Object[] { newArgs });
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }
    //解压jar包中的文件到toDir目录
    public static void unJar(File jarFile, File toDir) throws IOException {
        JarFile jar = new JarFile(jarFile);
        try {
            Enumeration entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = (JarEntry) entries.nextElement();
                if (!entry.isDirectory()) {
                    InputStream in = jar.getInputStream(entry);
                    try {
                        File file = new File(toDir, entry.getName());
                        if (!file.getParentFile().mkdirs()) {
                            if (!file.getParentFile().isDirectory()) {
                                throw new IOException(
                                        "Mkdirs failed to create "
                                                + file.getParentFile()
                                                .toString());
                            }
                        }
                        OutputStream out = new FileOutputStream(file);
                        try {
                            byte[] buffer = new byte[8192];
                            int i;
                            while ((i = in.read(buffer)) != -1) {
                                out.write(buffer, 0, i);
                            }
                        } finally {
                            out.close();
                        }
                    } finally {
                        in.close();
                    }
                }
            }
        } finally {
            jar.close();
        }
    }
}
