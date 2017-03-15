import org.junit.Test;

import java.sql.SQLException;

/**
 * Created by aaa on 17-3-15.
 */
public class ExceptionTest<T extends Exception> {
    @Test
    public void run(){
        try {
            new ExceptionTest<RuntimeException>().castT(new SQLException());
        }catch (final RuntimeException/*or Exception*/ e){
//        }catch (final SQLException e){
            e.printStackTrace();
        }
    }

    private void castT(final Exception e) throws T {
        throw (T)e;
    }
}
