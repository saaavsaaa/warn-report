package ftp;

import run.Watcher;
import sun.net.TelnetInputStream;
import sun.net.TelnetOutputStream;
import sun.net.ftp.FtpClient;
import sun.net.ftp.FtpProtocolException;

import java.io.*;
import java.net.InetSocketAddress;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by root on 17-6-1.
 */
public class Download {
    /*
        1、充值对账
        2、提现对账
        3、交易对账
        4、账户余额对账
    */
    private static final String USER = "M20000030333";
    private static final String pass = "p4ri89m0333";
    private static String ip = "111.203.205.28";
    private static int port = 21;
    public static final String LOCAL_DOWNLOAD_PATH = "/home/aaa/test/";
    
    private static final String WITHDRAW = "WITHDRAW";
    private static final String TRANSACTION = "TRANSACTION";
    private static final String RECHARGE = "RECHARGE";
    
    /**
     * 函数入口
     *
     * @param agrs
     */
    public static void main(String agrs[]) throws IOException, FtpProtocolException {
        
        LocalDate yesterday = LocalDate.now().plusDays(-1);
        int year = yesterday.getYear();
        String sub = yesterday.getMonthValue() > 9 ? String.valueOf(yesterday.getMonthValue()) : "0" + yesterday.getMonthValue();
        
        String path = "/" + year + "/" + sub + "/";

//        String[] keys = getKeys(yesterday);
        FtpUtil ftp = new FtpUtil();
        /*
         * 使用默认的端口号、用户名、密码以及根目录连接FTP服务器
         */
        try {
            ftp.connect(ip, port, USER, pass, path);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        
        String date = yesterday.format(DateTimeFormatter.BASIC_ISO_DATE);
        String prx = date + "_" + USER + "_";
        
        downloadWithdraw(path, prx, ftp);
        downloadTransaction(path, prx, ftp);
        downloadRecharge(path, prx, ftp);
        // 下载
        /*for (int i = 0; i < keys.length; i++) {
            try {
                String rPath = path + keys[i];
                String lPath = localPath + keys[i];
                if (ftp.exist(rPath + ".ok")) {
                    ftp.download(rPath + ".txt", lPath + ".txt");
                }
            } catch (FtpProtocolException e) {
                e.printStackTrace();
            }
        }*/
        ftp.closeConnect();
    }
    
    private static void downloadWithdraw(String basePath, String prx, FtpUtil ftp) throws FtpProtocolException {
        DownloadExec exec = new DownloadExec(WITHDRAW);
        exec.start(basePath, prx, ftp);
    }
    
    private static void downloadTransaction(String basePath, String prx, FtpUtil ftp) throws FtpProtocolException {
        DownloadExec exec = new DownloadExec(TRANSACTION);
        exec.start(basePath, prx, ftp);
    }
    
    private static void downloadRecharge(String basePath, String prx, FtpUtil ftp) throws FtpProtocolException {
        DownloadExec exec = new DownloadExec(RECHARGE);
        exec.start(basePath, prx, ftp);
    }
    /*private static String[] getKeys(LocalDate yesterday){
        String[] keys = new String[3];
        String date = yesterday.format(DateTimeFormatter.BASIC_ISO_DATE);
        String prx = date + "_" + USER + "_";
        keys[0] = prx + WITHDRAW;
        keys[1] = prx + TRANSACTION;
        keys[2] = prx + RECHARGE;
        return keys;
    }*/
}

class DownloadExec {
    private String type;
    private boolean done = false;
    
    DownloadExec(String type) {
        this.type = type;
    }
    
    void start(String basePath, String prx, FtpUtil ftp) {
        System.out.println("case : begin start , path : " + basePath + prx + type);
        done = exec(basePath, prx, ftp);
        
        if (!done) {
            DownloadWatcher watcher = new DownloadWatcher(getType());
            watcher.newTask(() -> exec(basePath, prx, ftp));
        }
    }
    
    private boolean exec(String basePath, String prx, FtpUtil ftp) {
        String key = prx + getType();
        String ftpPath = basePath + key;
        try {
            String localPath = Download.LOCAL_DOWNLOAD_PATH + key;
            if (ftp.exist(ftpPath + ".ok")) {
                ftp.download(ftpPath + ".txt", localPath + ".txt");
                return true;
            } else {
                return false;
            }
        } catch (FtpProtocolException e) {
            System.out.println("case : download error, path : " + ftpPath + ", reason" + e.getMessage());
            return false;
        }
    }
    
    public String getType() {
        return type;
    }
}

class DownloadWatcher {
    private final ScheduledExecutorService scheduledExecutorService;
    private String type;
    
    DownloadWatcher(final String type) {
        this.type = type;
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "download-" + type + "-Thread"));
        System.out.println("case : start watcher, type : " + type);
    }
    
    Runnable newTask(Supplier<Boolean> action) {
        return (Runnable) () -> {
            Boolean done = action.get();
            if (done) {
                System.out.println("stop : start watcher, type : " + type);
                this.scheduledExecutorService.shutdown();
            }
        };
    }
    
    public void watch(Runnable runnable) {
        this.scheduledExecutorService.scheduleAtFixedRate(runnable, 30, 5, TimeUnit.SECONDS);
    }
}

class FtpUtil {
    /**
     * 本地文件名
     */
    private String localfilename;
    /**
     * 远程文件名
     */
    private String remotefilename;
    /**
     * FTP客户端
     */
    private FtpClient ftpClient;
    
    /**
     * 服务器连接
     *
     * @param ip       服务器IP
     * @param port     服务器端口
     * @param user     用户名
     * @param password 密码
     * @param path     服务器路径
     * @throws FtpProtocolException
     */
    public void connect(String ip, int port, String user, String password, String path) {
        try {
            ftpClient = FtpClient.create();
            ftpClient.connect(new InetSocketAddress(ip, port));
//            ftpClient = FtpClient.create(new InetSocketAddress(ip, port));
            ftpClient.login(user, password.toCharArray());
            // 设置成2进制传输
            ftpClient.setBinaryType();
//            ftpClient.setAsciiType();
            System.out.println("login success!");
            
            if (path.length() != 0) {
                // 把远程系统上的目录切换到参数path所指定的目录
                ftpClient.changeDirectory(path);
            }
            ftpClient.setBinaryType();
//            ftpClient.setAsciiType();
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        } catch (FtpProtocolException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 下载文件
     *
     * @param remoteFile 远程文件路径(服务器端)
     * @param localFile  本地文件路径(客户端)
     * @throws FtpProtocolException
     */
    public void download(String remoteFile, String localFile) throws FtpProtocolException {
        InputStream inputStream = null;
//        FileOutputStream outputStream = null;
        try {
            
            // 获取远程机器上的文件filename，借助TelnetInputStream把该文件传送到本地。
            inputStream = ftpClient.getFileStream(remoteFile);
//            File file_in = new File(localFile);
//            outputStream = new FileOutputStream(file_in);
            
//            byte[] bytes = new byte[1024];
//            int c;
            /*while ((c = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, c);
            }*/
            System.out.println(getContent(inputStream, "UTF-8"));
            System.out.println("download success");
        } catch (IOException ex) {
            System.out.println("download error");
            ex.printStackTrace();
            throw new RuntimeException(ex);
        } finally {
//            clear(inputStream, outputStream);
            clear(inputStream, null);
        }
    }
    
    public static String getContent(InputStream inputStream, String encode) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, encode);
        String content = "";
        int tChar = 0;
        while ((tChar = inputStreamReader.read()) != -1) {
            //if not end,the total content add the value of the stream read this time
            content += (char) tChar;
        }
        inputStreamReader.close();
        return content;
    }
    
    public boolean exist(String remoteFile) {
        try {
            ftpClient.getFileStream(remoteFile);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    
    private void clear(InputStream inputStream, OutputStream outputStream) {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 关闭连接
     */
    
    public void closeConnect() {
        try {
            ftpClient.close();
            System.out.println("disconnect success");
        } catch (IOException ex) {
            System.out.println("not disconnect");
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
    
    
    /**
     * 上传文件
     *
     * @param localFile  本地文件
     * @param remoteFile 远程文件
     * @throws FtpProtocolException
     */
    public void upload(String localFile, String remoteFile) throws FtpProtocolException {
        this.localfilename = localFile;
        this.remotefilename = remoteFile;
        TelnetOutputStream telnetOutputStream = null;
        FileInputStream inputStream = null;
        try {
            // 将远程文件加入输出流中
            telnetOutputStream = (TelnetOutputStream) ftpClient.putFileStream(this.remotefilename, true);
            
            // 获取本地文件的输入流
            File file_in = new File(this.localfilename);
            inputStream = new FileInputStream(file_in);
            
            // 创建一个缓冲区
            byte[] bytes = new byte[1024];
            int c;
            while ((c = inputStream.read(bytes)) != -1) {
                telnetOutputStream.write(bytes, 0, c);
            }
            System.out.println("upload success");
        } catch (IOException ex) {
            System.out.println("not upload");
            ex.printStackTrace();
            throw new RuntimeException(ex);
        } finally {
            clear(inputStream, telnetOutputStream);
        }
    }
}