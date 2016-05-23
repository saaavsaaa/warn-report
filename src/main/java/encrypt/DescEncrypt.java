package encrypt;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;

public class DescEncrypt { 
    private static final String Algorithm = "DES"; // 定义 加密算法,可用 
                                                                // DES,DESede,Blowfish 
    // src为被加密的数据缓冲区（源） 
    public static byte[] encryptMode(byte[] keybyte, byte[] src) { 
         try { 
              // 生成密钥 
              SecretKey deskey = new SecretKeySpec(keybyte, Algorithm); 
              // 加密 
              Cipher c1 = Cipher.getInstance(Algorithm); 
              c1.init(Cipher.ENCRYPT_MODE, deskey); 
              return c1.doFinal(src); 
         } catch (java.security.NoSuchAlgorithmException e1) { 
              e1.printStackTrace(); 
         } catch (javax.crypto.NoSuchPaddingException e2) { 
              e2.printStackTrace(); 
         } catch (Exception e3) {
              e3.printStackTrace();
         }
         return null;
    }
    // keybyte为加密密钥，长度为24字节
    // src为加密后的缓冲区
    public static byte[] decryptMode(byte[] keybyte, byte[] src) {
         try {
              // 生成密钥
              SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
              // 解密
              Cipher c1 = Cipher.getInstance(Algorithm);
              c1.init(Cipher.DECRYPT_MODE, deskey);
              return c1.doFinal(src);
         } catch (java.security.NoSuchAlgorithmException e1) {
              e1.printStackTrace();
         } catch (javax.crypto.NoSuchPaddingException e2) {
              e2.printStackTrace();
         } catch (Exception e3) {
              e3.printStackTrace();
         }
         return null;
    }
    // 转换成十六进制字符串
    public static String byte2hex(byte[] b) {
         String hs = "";
         String stmp = "";
         for (int n = 0; n < b.length; n++) {
              stmp = (Integer.toHexString(b[n] & 0XFF));
              if (stmp.length() == 1) 
                   hs = hs + "0" + stmp; 
              else 
                   hs = hs + stmp; 
              if (n < b.length - 1) 
                   hs = hs + ""; 
         } 
         return hs.toUpperCase(); 
    } 
    // 16 进制 转 2 进制 
    public static byte[] hex2byte(String hex) throws IllegalArgumentException { 
         if (hex.length() % 2 != 0) { 
              throw new IllegalArgumentException(); 
         } 
         char[] arr = hex.toCharArray(); 
         byte[] b = new byte[hex.length() / 2]; 
         for (int i = 0, j = 0, l = hex.length(); i < l; i++, j++) { 
              String swap = "" + arr[i++] + arr[i]; 
              int byteint = Integer.parseInt(swap, 16) & 0xFF; 
              b[j] = new Integer(byteint).byteValue(); 
         } 
         return b; 
    } 
    private static byte[] hex2byte(byte[] b) { 
         if ((b.length % 2) != 0) 
              throw new IllegalArgumentException("长度不是偶数"); 
         byte[] b2 = new byte[b.length / 2]; 
         for (int n = 0; n < b.length; n += 2) { 
              String item = new String(b, n, 2); 
              b2[n / 2] = (byte) Integer.parseInt(item, 16); 
         } 
         return b2; 
    } 
    // 加密 
    public static String encrypt(String str, byte[] key) { 
         Security.addProvider(new com.sun.crypto.provider.SunJCE()); 
         byte[] encrypt = encryptMode(key, str.getBytes()); 
         return byte2hex(encrypt); 
    } 
    // 加密 
    public static byte[] encryptRetByte(byte[] src, byte[] key) { 
         Security.addProvider(new com.sun.crypto.provider.SunJCE()); 
         byte[] encrypt = encryptMode(key, src); 
         return encrypt; 
    } 
    // 解密 
    public static String decrypt(String str, byte[] key) { 
         Security.addProvider(new com.sun.crypto.provider.SunJCE()); 
         byte[] decrypt = decryptMode(key, hex2byte(str)); 
         return new String(decrypt); 
    } 
    
    public static void main(String arg[]) { 
    	String strKey = "0001000200030004"; 
    	/*String url = "jdbc:mysql://127.0.0.1:3307/app?useUnicode=true&autoReconnect=true&allowMultiQueries=true";
    	String driver="com.mysql.jdbc.Driver";
    	String username="root";
    	String password="root";*/
    	String url = "jdbc:mysql://rdsiuqmbefybizi.mysql.rds.aliyuncs.com:3306/p2p?useUnicode=true&autoReconnect=true&allowMultiQueries=true";
    	//String url = "jdbc:mysql://rdst0tz220te3j141g64.mysql.rds.aliyuncs.com:3306/p2p?useUnicode=true&autoReconnect=true&allowMultiQueries=true";
    	/*String driver="com.mysql.jdbc.Driver";
    	String username="hzdp2p";
    	String password="HZD123456qwe";
    	String url2 = encrypt(url, hex2byte(strKey));//加密 
    	String driver2 = encrypt(driver, hex2byte(strKey));//加密 
    	String username2 = encrypt(username, hex2byte(strKey));//加密 
    	String password2 = encrypt(password, hex2byte(strKey));//加密 
    	
    	String url3 = decrypt(url2, hex2byte(strKey));//加密 
    	String driver3 = decrypt(driver2, hex2byte(strKey));//加密 
    	String username3 = decrypt(username2, hex2byte(strKey));//加密 
    	String password3 = decrypt(password2, hex2byte(strKey));//加密 
    	System.out.println("url加密后:\n"+url2);
    	System.out.println("url解密后:\n"+url3);
    	System.out.println("driver2加密后:\n"+driver2);
    	System.out.println("driver2解密后:\n"+driver3);
    	System.out.println("username加密后:\n"+username2);
    	System.out.println("username解密后:\n"+username3);
    	System.out.println("password加密后:\n"+password2);
    	System.out.println("password解密后:\n"+password3);*/
    	
    	/////////////////////////////新生产配置////////////////////////////////////////////////////////////////////////////////
    	
//    	主库配置
//    	String master_url = "jdbc:mysql://rdsi8v807ls6pyz465ds.mysql.rds.aliyuncs.com:3306/p2p?useUnicode=true&autoReconnect=true&allowMultiQueries=true";
//    	String master_encrypt_url = encrypt(master_url,hex2byte(strKey));
//    	System.out.println("主库加密后url===============>" + master_encrypt_url);
//    	String master_decrypt_url = decrypt(master_encrypt_url,hex2byte(strKey));
//    	System.out.println("主库解密后url===============>" + master_decrypt_url);
    	
//    	String master_username = "hzdp2p";
//    	String master_encrypt_username = encrypt(master_username,hex2byte(strKey));
//    	System.out.println("主库加密后username===============>" + master_encrypt_username);
//    	String master_decrypt_username = decrypt(master_encrypt_username,hex2byte(strKey));
//    	System.out.println("主库解密后username===============>" + master_decrypt_username);
    	
//    	String master_psd = "HZD123456qwe";
//    	String master_encrypt_psd = encrypt(master_psd,hex2byte(strKey));
//    	System.out.println("主库加密后psd===============>" + master_encrypt_psd);
//    	String master_decrypt_psd = decrypt(master_encrypt_psd,hex2byte(strKey));
//    	System.out.println("主库解密后psd===============>" + master_decrypt_psd);
    	
    	
//    	从库配置
    	String slave_url = "jdbc:mysql://rdsjwj0u306048qor1b4.mysql.rds.aliyuncs.com:3306/p2p?useUnicode=true&autoReconnect=true&allowMultiQueries=true";
    	String slave_encrypt_url = encrypt(slave_url,hex2byte(strKey));
    	System.out.println("从库加密后url===============>" + slave_encrypt_url);
    	String slave_decrypt_url = decrypt(slave_encrypt_url,hex2byte(strKey));
    	System.out.println("从库解密后url===============>" + slave_decrypt_url);
    	
//    	String slave_username = "hzdp2pread";
//    	String slave_encrypt_username = encrypt(slave_username,hex2byte(strKey));
//    	System.out.println("从库加密后username===============>" + slave_encrypt_username);
//    	String slave_decrypt_username = decrypt(slave_encrypt_username,hex2byte(strKey));
//    	System.out.println("从库解密后username===============>" + slave_decrypt_username);
    	
//    	String slave_psd = "HZD123qwe";
//    	String slave_encrypt_psd = encrypt(slave_psd,hex2byte(strKey));
//    	System.out.println("从库加密后psd===============>" + slave_encrypt_psd);
//    	String slave_decrypt_psd = decrypt(slave_encrypt_psd,hex2byte(strKey));
//    	System.out.println("从库解密后psd===============>" + slave_decrypt_psd);
    	
    	
    	
    	///////////////////////////////老生产配置//////////////////////////////////////////////////////////////////////////////
    	
    	
//    	String product_marster_url = "CD1E7D3A7DEED8459952EB18207901EF213214FF15EF08315961E69D543E233DC6302998B74389F2931803AC450B33AAB01951543A5097DB4C732F189BF1FD53A51ADBF0F216479C98A178C9DFCEFEFE848370027A1BC9C764C940587D43C7DC5D8D5C5BAEC5E7C9DA2EE5D9B0AFFBCF2968812B4D917CFC";
//    	System.out.println("生产主库url======>" + decrypt(product_marster_url,hex2byte(strKey)));
//    	
//    	String product_marster_driver = "DFB084E48D901F55B4765B6B6DEEEA685621CEAB85E65590";
//    	System.out.println("生产主库driver======>" + decrypt(product_marster_driver,hex2byte(strKey)));
//    	
//    	String product_username = "059ECAF393B5AF22";
//    	System.out.println("生产主库username======>" + decrypt(product_username,hex2byte(strKey)));
//    	
//    	String product_slave_url = "CD1E7D3A7DEED8459952EB18207901EFD2972E0AD997C472DE932DC232FDC6AC9AFCB39168D5EB61FD10574F028E7073485FC887BC90586FB212267C0B3601A3D5D8D18A217B0D698E7B22A595D275E2632CBCB38DE7E57BCFD9B37BDAB2691BEE1E5065D3287032722345CDF1D0CCD614F7F9CDFFAA692493AFC4C3F3C57DC3";
//    	System.out.println("生产从库url======>" + decrypt(product_slave_url,hex2byte(strKey)));
//    	
//    	String product_slave_username = "059ECAF393B5AF22";
//    	System.out.println("生产从库username======>" + decrypt(product_slave_username,hex2byte(strKey)));
//    	
//    	String product_slave_password = "9243629AF52D708427DFA639B01D481B";
//    	System.out.println("生产从库username======>" + decrypt(product_slave_password,hex2byte(strKey)));
    	
    } 
}