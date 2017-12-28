package object;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.junit.Test;

/**
 * Created by aaa on 17-11-15.
 */
public class ThreadLocalTest {
    @Test
    public void test(){
        ThreadLocal<Header[]> headerHolder = new ThreadLocal<>();
        Header[] headers = headerHolder.get();
        if (headers == null) {
            System.out.println("111111111111111111111");
            return;
        }
        System.out.println("22222222222222222222");
    }
}
