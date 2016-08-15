import org.junit.Test;

/**
 * Created by ldb on 2016/7/25.
 */
public class TestObjectInstance {

    @Test
    public void test(){
        B b = new B();
        A a = (A)b;
        if (a instanceof B){
            System.out.println("B," + a.getN());
        } else {
            System.out.println("A," + a.getN());
        }
    }
}

class A{
    protected String n;

    public A(){
        n = "a";
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }
}

class B extends A{
    public B(){
        n = "b";
    }
}
