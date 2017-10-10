
import org.junit.Test;
import rocketDoubleWrite.ProducerClient;
import util.ConcurrentRun;

public class TestDoubleClient {
    @Test
    public void send(){
        int a = 1;
        for (int i = 0; i < a; i++) {
            ProducerClient.send("a1", "{'a12':'121'}");
        }
    }

    @Test
    public void test() throws InterruptedException {
        Runnable runnable = new Runnable() {
            public void run() {
                boolean isSuccess = ProducerClient.send("201606021155", "{'a12':'121'}");
                System.out.print(isSuccess + "\n");
            }
        };
        ConcurrentRun.executeTasks(100, runnable);
    }
}
