package util;

import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

// https://www.cnblogs.com/haoyul/p/9662757.html
public class ZipUtil {

    public static void unGzipFile(String sourcedir) {
        String ouputfile = "";
        try {
            //建立gzip压缩文件输入流
            FileInputStream fin = new FileInputStream(sourcedir);
            //建立gzip解压工作流
            GZIPInputStream gzin = new GZIPInputStream(fin);
            //建立解压文件输出流
            ouputfile = sourcedir.substring(0,sourcedir.lastIndexOf('.'));
            ouputfile = ouputfile.substring(0,ouputfile.lastIndexOf('.'));
            FileOutputStream fout = new FileOutputStream(ouputfile);

            int num;
            byte[] buf=new byte[1024];

            while ((num = gzin.read(buf,0,buf.length)) != -1)
            {
                fout.write(buf,0,num);
            }

            gzin.close();
            fout.close();
            fin.close();
        } catch (Exception ex){
            System.err.println(ex.toString());
        }
        return;
    }

    /**
     * 解压缩zipFile
     * @param file 要解压的zip文件对象
     * @param outputDir 要解压到某个指定的目录下
     * @throws IOException
     */
    public static void unZip(File file,String outputDir) throws IOException {
        ZipFile zipFile = null;

        try {
            Charset CP866 = Charset.forName("CP866");  //specifying alternative (non UTF-8) charset
            //ZipFile zipFile = new ZipFile(zipArchive, CP866);
            zipFile =  new ZipFile(file, CP866);
            createDirectory(outputDir,null);//创建输出目录

            Enumeration<?> enums = zipFile.entries();
            while(enums.hasMoreElements()){

                ZipEntry entry = (ZipEntry) enums.nextElement();
                System.out.println("解压." +  entry.getName());

                if(entry.isDirectory()){//是目录
                    createDirectory(outputDir,entry.getName());//创建空目录
                }else{//是文件
                    File tmpFile = new File(outputDir + "/" + entry.getName());
                    createDirectory(tmpFile.getParent() + "/",null);//创建输出目录

                    InputStream in = null;
                    OutputStream out = null;
                    try{
                        in = zipFile.getInputStream(entry);;
                        out = new FileOutputStream(tmpFile);
                        int length = 0;

                        byte[] b = new byte[2048];
                        while((length = in.read(b)) != -1){
                            out.write(b, 0, length);
                        }

                    }catch(IOException ex){
                        throw ex;
                    }finally{
                        if(in!=null)
                            in.close();
                        if(out!=null)
                            out.close();
                    }
                }
            }

        } catch (IOException e) {
            throw new IOException("解压缩文件出现异常",e);
        } finally{
            try{
                if(zipFile != null){
                    zipFile.close();
                }
            }catch(IOException ex){
                throw new IOException("关闭zipFile出现异常",ex);
            }
        }
    }

    /**
     * 构建目录
     * @param outputDir
     * @param subDir
     */
    public static void createDirectory(String outputDir,String subDir){
        File file = new File(outputDir);
        if(!(subDir == null || subDir.trim().equals(""))){//子目录不为空
            file = new File(outputDir + "/" + subDir);
        }
        if(!file.exists()){
            if(!file.getParentFile().exists())
                file.getParentFile().mkdirs();
            file.mkdirs();
        }
    }

    //------------------------------------------------------------------------------------------------------
    /**
     * .tar.gz文件可以看做先用tar打包，再使用gz进行压缩
     * @param file 要解压的tar.gz文件对象
     * @param outputDir 要解压到某个指定的目录下
     * @throws IOException
     */
    public static void unTarGz(File file, String outputDir) throws IOException {
        TarInputStream tarIn = null;
        try{
            tarIn = new TarInputStream(new GZIPInputStream(
                    new BufferedInputStream(new FileInputStream(file))),
                    1024 * 2);

            createDirectory(outputDir,null);//创建输出目录

            TarEntry entry = null;
            while( (entry = tarIn.getNextEntry()) != null ){

                if(entry.isDirectory()){//是目录
                    entry.getName();
                    createDirectory(outputDir,entry.getName());//创建空目录
                }else{//是文件
                    File tmpFile = new File(outputDir + "/" + entry.getName());
                    createDirectory(tmpFile.getParent() + "/",null);//创建输出目录
                    OutputStream out = null;
                    try{
                        out = new FileOutputStream(tmpFile);
                        int length = 0;

                        byte[] b = new byte[2048];

                        while((length = tarIn.read(b)) != -1){
                            out.write(b, 0, length);
                        }

                    }catch(IOException ex){
                        throw ex;
                    }finally{

                        if(out!=null)
                            out.close();
                    }
                }
            }
        }catch(IOException ex){
            throw new IOException("解压归档文件出现异常",ex);
        } finally{
            try{
                if(tarIn != null){
                    tarIn.close();
                }
            }catch(IOException ex){
                throw new IOException("关闭tarFile出现异常",ex);
            }
        }
    }
}
