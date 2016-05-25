package rocket;

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

