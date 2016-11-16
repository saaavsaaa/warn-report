/**
 * Created by root on 16-11-16.
 */
public interface TestLongAndDouble {
    
}

class Test {
    
    protected long l = -1l;
    
    public static void main(String[] args) {
        System.out.println(toBinary(-1l));
        System.out.println(toBinary(1l));
        Test t = new Test();
        Worker w1 = new Worker(t);
        Worker2 w2 = new Worker2(t);
        w1.setDaemon(true);
        w2.setDaemon(true);
        w1.start();
        w2.start();
        while (true) {
            if (exec(t.l)){//(t.l != -1l && t.l != 1l) {// when t.l != -1l l = 1 && when t.l != 1l l could change to -1
                System.out.println(toBinary(t.l));
                System.out.println("l的写不是原子操作");
                break;
            }
        }
    }
    
    private static synchronized boolean exec(long l){
        if (l != -1l && l != 1l) {
            System.out.println(toBinary(l));
            System.out.println("l的写不是原子操作");
            return true;
        }
        return false;
    }
    
    private static String toBinary(long l) {
        StringBuilder sb = new StringBuilder(Long.toBinaryString(l));
        while (sb.length() < 64) {
            sb.insert(0, "0");
        }
        return sb.toString();
    }
}

class Worker extends Thread {
    
    public Worker(Test t) {
        this.t = t;
    }
    
    private Test t;
    
    public void run() {
        while (true) {
            t.l = -1l;
        }
    }
}

class Worker2 extends Thread {
    
    public Worker2(Test t) {
        this.t = t;
    }
    
    private Test t;
    
    public void run() {
        while (true) {
            t.l = 1l;
        }
    }
    
}