import org.junit.Test;

import java.sql.SQLException;

/**
 * Created by aaa on 17-3-15.
 */
public class ExceptionTest<T extends Exception> {
//public class ExceptionTest<T extends RuntimeException> {
    @Test
    public void run(){
        try {
            new ExceptionTest<RuntimeException>().castT(new SQLException());
        }catch (final RuntimeException/*or Exception*/ e){
//        }catch (final Exception e){
//        }catch (final SQLException e){
//          Can be reached by Exception
            e.printStackTrace();
        }
    }

    private void castT(final Exception e) throws T {
        throw (T)e;
    }
}
