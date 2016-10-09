import org.junit.Test;

import java.sql.*;
/**
 * Created by aaa on 2016/10/9.
 */
public class DBTest {


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
    public void testThreadUpdate() throws InterruptedException {
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
        System.out.println(Thread.currentThread().getId() + ", a : " + a);
        return a;
    }

    private void execUpdate(int ver, Connection conn) throws SQLException {
        String sql = "update aaatest set a=a+? where id=? AND a = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, 1);
        ps.setInt(2, 1);
        ps.setInt(3, ver);
        int r = ps.executeUpdate();
        System.out.println(Thread.currentThread().getId() + " update : " + r);
    }

    private void execUpdate(Connection conn) throws SQLException {
        String sql = "update aaatest set a=a+? where id=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, 1);
        ps.setInt(2, 1);
        int r = ps.executeUpdate();
        System.out.println(Thread.currentThread().getId() + " update : " + r);
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

    private DBUtils() {
    }

    //使用静态块加载驱动程序
    static {
//        URL = rb.getString("jdbc.url");
//        USERNAME = rb.getString("jdbc.username");
//        PASSWORD = rb.getString("jdbc.password");
//        DRIVER = rb.getString("jdbc.driver");
        URL = "jdbc:mysql://192.168.1.46:3306/p2p";
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
