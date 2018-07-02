package cn.tellwhy.request;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * Created by aaa on 18-5-11.
 */
public class HadoopTest {
    Configuration conf;
    
    @Before
    public void start(){
        conf = new Configuration();
        conf.set("fs.defaultFS","hdfs://192.168.2.164:8020");
    }
    
    @Test
    public void createTest() throws IOException {
        create("qqq", conf);
    }
    @Test
    public void readTest() throws IOException {
        // 创建FileSystem对象
        String url = "hdfs://192.168.2.164:8020/user/qqq";
        read(conf, url);
    }
    
    
    private void read(Configuration conf, String url) throws IOException {
        FileSystem fs = FileSystem.get(URI.create(url),conf);
        FSDataInputStream is = fs.open(new Path(url));
        //      FileStatus fileStatus = fileSystem.getFileStatus(new Path(uri));
//      byte[] buffer=new byte[1024];
//      in.read(4096, buffer, 0, 1024);
        IOUtils.copyBytes(is, System.out, 4096, false);
        IOUtils.closeStream(is);
    }
    
    public void create(String user, Configuration conf) throws IOException {
        FileSystem fileSystem = FileSystem.get(conf);
        System.out.println(fileSystem.getUri());
        Path file = new Path("/user/" + user);
        if (fileSystem.exists(file)) {
            System.out.println("haddop hdfs user foler  exists.");
            fileSystem.delete(file, true);
            System.out.println("haddop hdfs user foler  delete success.");
            
        }
        fileSystem.mkdirs(file);
        System.out.println("haddop hdfs user foler  creat success.");
    }
    
    /**
     * 创建一个新文件（此程序可以升级putMerge功能）
     * @param filename
     * @param content
     * @throws IOException
     */
    public static void newFile(String filename, byte[] content) throws IOException{
        Configuration conf = new Configuration();
        FileSystem fileSystem = FileSystem.get(conf);
        
        Path filePath = new Path(filename);
        FSDataOutputStream outputStream = fileSystem.create(filePath);
        
        outputStream.write(content);
        outputStream.close();
        fileSystem.close();
        System.out.println("创建文件成功！");
    }
    
    /**
     * 上传文件
     * @param localPath
     * @param hdfsPath
     * @throws IOException
     */
    public static void uploadFile(String localPath, String hdfsPath) throws IOException{
        Configuration configuration = new Configuration();
        FileSystem fileSystem = FileSystem.get(configuration);
        
        Path src = new Path(localPath);
        Path dst = new Path(hdfsPath);
        //fileSystem.copyFromLocalFile(src, dst);
        //第一个false，是否删除原文件；第二个false，是否overwrite。
        fileSystem.copyFromLocalFile(false, false, src, dst);
        fileSystem.close();
    }
    
    /**
     * 重命名
     * @param oldName
     * @param newName
     * @throws IOException
     */
    public static void rename(String oldName,String newName) throws IOException{
        Configuration configuration = new Configuration();
        FileSystem fileSystem = FileSystem.get(configuration);
        
        Path oldPath = new Path(oldName);
        Path newPath = new Path(newName);
        
        boolean isSuccess = fileSystem.rename(oldPath, newPath);
        if (isSuccess) {
            System.err.println("成功");
        }
        else {
            System.err.println("失败");
        }
        fileSystem.close();
    }
    
    /**
     * 删除文件操作
     * @param filePath
     * @throws IOException
     */
    public static void delete(String filePath) throws IOException{
        Configuration configuration = new Configuration();
        FileSystem fileSystem = FileSystem.get(configuration);
        Path hdfspath = new Path(filePath);
        fileSystem.deleteOnExit(hdfspath);
        fileSystem.close();
    }
    
    /**
     * 创建目录
     * @throws IOException
     */
    public static void mkdir(String dirPath) throws IOException{
        Configuration configuration = new Configuration();
        FileSystem fileSystem = FileSystem.get(configuration);
        Path path = new Path(dirPath);
        boolean isSuccess = fileSystem.mkdirs(path);
        if (isSuccess) {
            System.err.println("创建目录成功！");
        }else {
            System.err.println("创建目录失败！");
        }
        fileSystem.close();
    }
    
    public static void readFile(String filePath) throws IOException{
        Configuration configuration = new Configuration();
        FileSystem fs = FileSystem.get(configuration);
        Path path = new Path(filePath);
        InputStream in = null;
        in = fs.open(path);
        IOUtils.copyBytes(in, System.out, 4096);
        IOUtils.closeStream(in);
        fs.close();//TODO: ?
    }
}
