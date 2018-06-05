package server;

import org.junit.Test;
import util.ConcurrentRun;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aaa on 2016/10/9.
 */
public class DBTest {
    
    @Test
    public void updateRCConcurrent(){
        Runnable update1 = () -> {
            Connection conn = null;
            try {
                conn = DBUtils.getConnection();
                conn.setAutoCommit(false);
                conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                execSelect(conn);
                execUpdate(conn);
                Thread.sleep(1000);
                execSelect(conn);
                conn.commit();
                System.out.println("TreadId : " + Thread.currentThread().getId() + " commit");
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };
    
        Runnable update2 = () -> {
            Connection conn = null;
            try {
                conn = DBUtils.getConnection();
                conn.setAutoCommit(false);
//                conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                Thread.sleep(1000);
                retryUpdate(0, conn);
                execSelect(conn);
                conn.commit();
                System.out.println("TreadId : " + Thread.currentThread().getId() + " commit");
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };
        
        
        Thread threadu1 = createTask(update1);
        Thread threadu2 = createTask(update2);
        threadu1.start();
        threadu2.start();
        try {
            threadu1.join();
            threadu2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private boolean retryUpdate(final int count, Connection conn) throws SQLException {
        int currentCount = count + 1;
        conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        int a = execSelect(conn);
        conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
        int r = execUpdate(a, conn);
        if (r == 1){
            System.out.println("TreadId : " + Thread.currentThread().getId() + ", true version a : " + a);
            return true;
        }
        if (r == 0 && currentCount < 3){
            System.out.println("TreadId : " + Thread.currentThread().getId() + ", retry version a : " + a);
            return retryUpdate(currentCount, conn);
        }
        System.out.println("false");
        return false;
    }

    @Test
    public void testConcurrentUpdate() throws InterruptedException {
        Runnable update = () -> {
            Connection conn = null;
            try {
                conn = DBUtils.getConnection();
                conn.setAutoCommit(false);
                conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                int a = execSelect(conn);
                int r = execUpdate(a, conn);
                if (r == 0){
                    repeatUpdate(0, conn);
                }
                execSelect(conn);
                conn.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };
        ConcurrentRun.executeTasks(1100, update);
    }

    private boolean repeatUpdate(final int count, Connection conn) throws SQLException {
        int currentCount = count + 1;
        int a = execSelect(conn);
        int r = execUpdate(a, conn);
        if (r == 1){
            System.out.println("TreadId : " + Thread.currentThread().getId() + ", true version a : " + a);
            return true;
        }
        if (r == 0 && currentCount < 3){
            return repeatUpdate(currentCount, conn);
        }
        System.out.println("false");
        return false;
    }

    @Test
    public void testUpdate() throws SQLException {
        Connection conn = null;
        try {
            conn = DBUtils.getConnection();
            conn.setAutoCommit(false);//设置为手动提交事务
            execSelect(conn);
            execUpdate(conn);
            execSelect(conn);
            conn.commit(); //如果所有sql语句成功，则提交事务
        } catch (SQLException e) {
            e.printStackTrace();
            conn.rollback();//只要有一个sql语句出现错误，则将事务回滚
        }finally {
            conn.close();
        }
    }

    @Test
    public void testLowLevelUpdateInThread() throws InterruptedException {
        Runnable updateAndSelect = () -> {
            Connection conn = null;
            try {
                conn = DBUtils.getConnection();
                conn.setAutoCommit(false);
                conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                execSelect(conn);
                Thread.sleep(100);
                int a = execSelect(conn);
                execUpdate(a, conn);
                execSelect(conn);
                throw new SQLException();
                //conn.commit();
            } catch (SQLException e) {
                e.printStackTrace();
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Runnable update = () -> {
            Connection conn = null;
            try {
                conn = DBUtils.getConnection();
                conn.setAutoCommit(false);
                conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                execSelect(conn);
                Thread.sleep(10);
                execUpdate(conn);
                execSelect(conn);
                conn.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        Thread threadus = createTask(updateAndSelect);
        Thread threadu = createTask(update);
        threadus.start();
        threadu.start();
        threadu.join();
        threadus.join();
        System.out.print("111");
    }

    @Test
    public void testUpdateInThread() throws InterruptedException {
        Runnable updateAndSelect = () -> {
            Connection conn = null;
            try {
                conn = DBUtils.getConnection();
                conn.setAutoCommit(false);
                execSelect(conn);
                Thread.sleep(100);
                int a = execSelect(conn);
                execUpdate(a, conn);
                execSelect(conn);
                conn.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Runnable update = () -> {
            Connection conn = null;
            try {
                conn = DBUtils.getConnection();
                conn.setAutoCommit(false);
                //conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                execSelect(conn);
                Thread.sleep(10);
                execUpdate(conn);
                execSelect(conn);
                conn.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        Thread threadus = createTask(updateAndSelect);
        Thread threadu = createTask(update);
        threadus.start();
        threadu.start();
        threadu.join();
        threadus.join();
        System.out.print("111");
    }
    
    /*
    TreadId : 12, version a : 0, tag:21
    TreadId : 11, version a : 0, tag:11
    TreadId : 12 update : 1
    TreadId : 12, version a : 1, tag:22
    TreadId : 11, version a : 0, tag:12
    TreadId : 12, version a : 1, tag:23
    2C
    TreadId : 11 update : 1
    TreadId : 12, version a : 1, tag:221
    TreadId : 11, version a : 2, tag:13
    1C
    TreadId : 11, version a : 2, tag:111
    TreadId : 12, version a : 1, tag:222
    TreadId : 12 update : 1
    TreadId : 12, version a : 3, tag:223
    2C1
    END
    */
    @Test
    public void testUpdateCrossInThread() throws InterruptedException {
        Runnable updateAndSelect = () -> {
            Connection conn = null;
            try {
                conn = DBUtils.getConnection();
                conn.setAutoCommit(false);
//                conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                execSelect(conn, "11");
                Thread.sleep(100);
                execSelect(conn, "12");
                execUpdate(conn);
                Thread.sleep(10000);
                execSelect(conn, "13");
                conn.commit();
                System.out.println("1C");
                Thread.sleep(10000);
                execSelect(conn, "111");
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        
        Runnable update = () -> {
            Connection conn = null;
            try {
                conn = DBUtils.getConnection();
                conn.setAutoCommit(false);
//                conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                execSelect(conn, "21");
                Thread.sleep(10);
                execUpdate(conn);
                execSelect(conn, "22");
                Thread.sleep(5000);
                execSelect(conn, "23");
                conn.commit();
                System.out.println("2C");
                execSelect(conn, "221");
                Thread.sleep(21000);
                execSelect(conn, "222");
                execUpdate(conn);
                execSelect(conn, "223");
                conn.commit();
                System.out.println("2C1");
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
    
        Runnable update3 = () -> {
            Connection conn = null;
            try {
                conn = DBUtils.getConnection();
                conn.setAutoCommit(false);
//                conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                execSelect(conn, "31");
                Thread.sleep(150);
                execUpdate(conn);
                execSelect(conn, "32");
                Thread.sleep(5000);
                execSelect(conn, "33");
                conn.commit();
                System.out.println("3C");
                execSelect(conn, "3");
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        Thread threadus = createTask(updateAndSelect);
        Thread threadu = createTask(update);
        Thread thread3 = createTask(update3);
        threadus.start();
        threadu.start();
        thread3.start();
        threadu.join();
        threadus.join();
        thread3.join();
        System.out.println("END");
    }
    
    @Test
    public void testRollback() throws InterruptedException {
        Runnable insert1 = () -> {
            Connection conn = null;
            try {
                conn = DBUtils.getConnection();
                conn.setAutoCommit(false);
                execInsert(conn);
                System.out.println("sleep1");
                Thread.sleep(500);
                throw new Exception("rollback");
//                conn.commit();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            } finally {
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };
    
        Runnable insert2 = () -> {
            Connection conn = null;
            try {
                conn = DBUtils.getConnection();
                conn.setAutoCommit(false);
                execInsert(conn);
                System.out.println("sleep2");
                Thread.sleep(1000);
                conn.commit();
                System.out.println("commit");
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
    
        List<Runnable> runnables = new ArrayList<>();
        runnables.add(insert1);
        runnables.add(insert2);
        runnables.add(insert2);
        ConcurrentRun.executeTasks(runnables);
    }
    
    private void execInsert(Connection conn) throws SQLException {
        String sql = "INSERT IGNORE INTO ttt ( `value` ) VALUES( '111' ),( '112' ),( '113' ),( '114' ),( '115' ),( '116' ),( '117' ),( '118' ),( '119' )";
        PreparedStatement ps = conn.prepareStatement(sql);
        boolean success = ps.execute(sql);
        System.out.println("TreadId : " + Thread.currentThread().getId() + " insert : " + success);
    }

    private Thread createTask(final Runnable task){
        Thread thread = new Thread(){
            public void run() {
                try {
                    //当前线程开始等待
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        return thread;
    }

    private int execSelect(Connection conn) throws SQLException {
        String sql = "select a,b from aaatest WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, 1);
        ResultSet rs = ps.executeQuery();
        rs.next();
        int a = rs.getInt(1);
        System.out.println("TreadId : " + Thread.currentThread().getId() + ", version a : " + a);
        return a;
    }
    
    private int execSelect(Connection conn, String tag) throws SQLException {
        String sql = "select a,b from aaatest WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, 1);
        ResultSet rs = ps.executeQuery();
        rs.next();
        int a = rs.getInt(1);
        System.out.println("TreadId : " + Thread.currentThread().getId() + ", version a : " + a + ", tag:" + tag);
        return a;
    }

    private int execUpdate(final int ver, Connection conn) throws SQLException {
        String sql = "update aaatest set a=a+? where id=? AND a = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, 1);
        ps.setInt(2, 1);
        ps.setInt(3, ver);
        int r = ps.executeUpdate();
        System.out.println("TreadId : " + Thread.currentThread().getId() + " update : " + r);
        return r;
    }

    private void execUpdate(Connection conn) throws SQLException {
        String sql = "update aaatest set a=a+? where id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, 1);
        ps.setInt(2, 1);
        int r = ps.executeUpdate();
        System.out.println("TreadId : " + Thread.currentThread().getId() + " update : " + r);
    }
    
    //方法：使用PreparedStatement插入数据、更新数据
    @Test
    public void insertAndQuery() throws SQLException {
        Connection conn = null;
        try {
            conn = DBUtils.getConnection();
            conn.setAutoCommit(false);//设置为手动提交事务
            String sql1 = "insert into aaatest(a,b) values(?,?)";
            String sql2 = "update aaatest set a=? where id=?";
            PreparedStatement ps = conn.prepareStatement(sql1);
            ps.setInt(1, 2);
            ps.setInt(2, 3);
            ps.executeUpdate();

            ps = conn.prepareStatement(sql2);
            ps.setInt(1, 3);
            ps.setInt(2, 2);
            ps.executeUpdate();
            conn.commit(); //如果所有sql语句成功，则提交事务
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            conn.rollback();//只要有一个sql语句出现错误，则将事务回滚
        }finally {
            conn.close();
        }
    }

    @Test
    public void testTransaction() throws Exception {
        Connection conn = null;
        try {
            conn = DBUtils.getConnection();
            conn.setAutoCommit(false);
            String sql1 = "update aaatest set a = a-1 where id = ? ";
            String sql2 = "update aaatest set a = a+1 where id = ?";
            PreparedStatement ps = conn.prepareStatement(sql1);
            // 只能批量执行某一条固定的sql语句，并且进行参数化设置
            ps.setInt(1, 11); // 为name 为 a的用户减少100元
            ps.addBatch();
            ps.setInt(1, 12); // 为name 为b的用户减少100元
            ps.addBatch();
            ps.executeBatch(); // 只能在执行完成以后提交一次，然后改成新的sql脚本。但容易引发内存泄漏

            ps = conn.prepareStatement(sql2); // 内存泄漏，存在未关闭的链接
            ps.setInt(1, 11);
            ps.addBatch();
            ps.executeBatch();
            conn.commit();
        } catch (Exception e) {
            conn.rollback();
        } finally {
            conn.close();
        }
    }
}

class DBUtils {
    //数据库连接地址
    public static String URL;
    //用户名
    public static String USERNAME;
    //密码
    public static String PASSWORD;
    //mysql的驱动类
    public static String DRIVER;

    //private static ResourceBundle rb = ResourceBundle.getBundle("jdbc");
    private static final String createSql = "DROP TABLE IF EXISTS `aaatest`;" +
            "CREATE TABLE `aaatest` (\n" +
            "  `id` INT(11) DEFAULT NULL,\n" +
            "  `a` INT(11) DEFAULT NULL,\n" +
            "  `b` INT(11) DEFAULT NULL\n" +
            ") ENGINE=INNODB DEFAULT CHARSET=utf8;" +
            "INSERT  INTO `aaatest`(`id`,`a`,`b`) VALUES (1,0,1);";

    private DBUtils() {
    }

    //使用静态块加载驱动程序
    static {
//        URL = rb.getString("jdbc.url");
//        USERNAME = rb.getString("jdbc.username");
//        PASSWORD = rb.getString("jdbc.password");
//        DRIVER = rb.getString("jdbc.driver");
        URL = "jdbc:mysql://192.168.2.150:3306/test";
        USERNAME = "root";
        PASSWORD = "123456";
        DRIVER = "com.mysql.jdbc.Driver";
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //定义一个获取数据库连接的方法
    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("获取连接失败");
        }
        return conn;
    }

    /**
     * 关闭数据库连接
     *
     * @param rs
     * @param stat
     * @param conn
     */
    public static void close(ResultSet rs, Statement stat, Connection conn) {
        try {
            if (rs != null) rs.close();
            if (stat != null) stat.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
