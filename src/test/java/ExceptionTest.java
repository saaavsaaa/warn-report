import org.junit.Test;

import java.sql.SQLException;

/**
 * Created by aaa on 17-3-15.
 */
public class ExceptionTest<T extends RuntimeException> {
    @Test
    public void run(){
        try {
            this.castT(new SQLException());
        }catch (final Exception e){
//        }catch (final SQLException e){
            e.printStackTrace();
        }
    }

    private void castT(Exception e){
        throw (T)e;
    }
}
