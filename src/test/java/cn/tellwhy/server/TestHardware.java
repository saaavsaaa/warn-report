package cn.tellwhy.server;

import org.junit.Test;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by root on 17-4-25.
 */
public class TestHardware {
    
    @Test
    public void getNetCardTest(){
        try {
            System.out.println(getMacAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void printCardTest() throws SocketException {
        Enumeration<NetworkInterface> ni = NetworkInterface.getNetworkInterfaces();
    
        NetworkInterface netI = ni.nextElement();
        byte[] bytes = netI.getHardwareAddress();
        StringBuffer sb = new StringBuffer()
                .append(bytes[0]).append(bytes[1])
                .append(bytes[2]).append(bytes[3])
                .append(bytes[4]).append(bytes[5]);
        
        System.out.println(sb.toString().toUpperCase());
    }
    
    /**
     * 按照"XX-XX-XX-XX-XX-XX"格式，获取本机MAC地址
     * @return
     * @throws Exception
     */
    private String getMacAddress() throws Exception{
        Enumeration<NetworkInterface> ni = NetworkInterface.getNetworkInterfaces();
        
        while(ni.hasMoreElements()){
            NetworkInterface netI = ni.nextElement();
            
            byte[] bytes = netI.getHardwareAddress();
            if(netI.isUp() && netI != null && bytes != null && bytes.length == 6){
                StringBuffer sb = new StringBuffer();
                for(byte b:bytes){
                    //与11110000作按位与运算以便读取当前字节高4位
                    sb.append(Integer.toHexString((b&240)>>4));
                    //与00001111作按位与运算以便读取当前字节低4位
                    sb.append(Integer.toHexString(b&15));
                    sb.append("-");
                }
                sb.deleteCharAt(sb.length()-1);
                return sb.toString().toUpperCase();
            }
        }
        return null;
    }
}
