import org.junit.Test;
import rocketDoubleWrite.ProducerClient;

/**
 * Created by ldb on 2016/5/23.
 */
public class TestDoubleClient {
    @Test
    public void send(){
        int a = 2;
        for (int i = 0; i < a; i++) {
            ProducerClient.send("a1", "{'a12':'121'}");
        }
    }
}
