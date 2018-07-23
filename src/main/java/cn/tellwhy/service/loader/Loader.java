package cn.tellwhy.service.loader;

import java.util.ServiceLoader;

/**
 * Created by aaa.
 */
public class Loader {
    
    public static void main(String[] args) {
        try {
            load();
        } catch (Throwable throwable) {
            System.out.println(throwable.getMessage());
        }
    }
    
    private static void load() {
        ServiceLoader<Run> runs = ServiceLoader.load(Run.class);
        for (Run each : runs) {
            each.process();
        }
    }
}
