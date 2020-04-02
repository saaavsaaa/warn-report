package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUtil {
    /*
     * 读取指定路径下的文件名和目录名
     */
    public static List<String> list(final String path) {
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

    public static String readContent(final String txtPath) {
        File file = new File(txtPath);
        if(file.isFile() && file.exists()){
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuffer sb = new StringBuffer();
                String text = null;
                while((text = bufferedReader.readLine()) != null){
                    sb.append(text);
                }
                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Map<String,String> readPhones(String txtPath) {
        Map<String,String> result = new HashMap<>();
        File file = new File(txtPath);
        if(file.isFile() && file.exists()){
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String text = null;
                while((text = bufferedReader.readLine()) != null){
                    int index = text.indexOf(' ');
                    result.put(text.substring(0, index), text.substring(index + 1));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}
