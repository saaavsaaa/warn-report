package object;

import org.junit.Test;

/**
 * Created by aaa on 18-1-17.
 */
public class ByteTest {
    @Test
    public void testBinaryComplement(){
        byte[] bs = new byte[10];
//        bs[0]= -127;
        bs[0] = -54;
        bs[1] = -2;
        bs[2] = -70;
        bs[3] = -66;
    
        int result = ((bs[0] & 0xff) << 24) | ((bs[1] & 0xff) << 16) | ((bs[2] & 0xff) << 8) | ((bs[3] & 0xff));
        System.out.println(Integer.toHexString(result));
        /*for (byte b : bs) {
            System.out.println("b" + (char)b);
            int c = b & 0xff;
            System.out.println("c" + (char)c);
        }*/
    }
}
