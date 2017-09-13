package report.btrace;

import java.util.Random;

/**
 * Created by aaa on 17-9-11.
 */
public class HelloWorld {
    public static void main(String[] args) throws Exception {
        //CaseObject object = new CaseObject();
        while (true) {
            Random random = new Random();
            execute(random.nextInt(4000));
            
            //object.execute(random.nextInt(4000));
        }
        
        
        
    }
    public static Integer execute(int sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (Exception e) {
        }
        System.out.println("sleep time is=>"+sleepTime);
        return 0;
    }
}
