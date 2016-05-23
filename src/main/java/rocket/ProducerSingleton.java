package rocket;

/**
 * Created by ldb on 2016/5/17.
 */
public class ProducerSingleton {
    private static class SingletonHolder {
        private static final ProducerSingleton INSTANCE = new ProducerSingleton();
    }

    private ProducerSingleton() {
    }

    public static final ProducerSingleton getInstance() {
        return SingletonHolder.INSTANCE;
    }
}

