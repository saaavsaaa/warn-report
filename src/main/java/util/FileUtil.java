package util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    /*
     * 读取指定路径下的文件名和目录名
     */
    public static List<String> list(String path) {
        File file = new File(path);
        File[] fileList = file.listFiles();
        List<String> result = new ArrayList<>();
        for (File each : fileList) {
            if (!each.isDirectory() && each.getName().endsWith(".wav") &&
                    !each.getName().equals("3gdb.wav") && !each.getName().equals("三个代表.wav")) {
                System.out.println(each.getName());
                result.add(each.getName());
            }
        }
        return result;
    }
}
