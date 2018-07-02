package cn.tellwhy.object;

import cn.tellwhy.encrypt.DescEncrypt;
import org.junit.Test;

public class TestDescEncrypt {

    @Test
    public void encryptTest(){

        /*主：192.168.1.46 root/123456
        从：192.168.1.45 root/123456*/

        String strKey = "0001000200030004";
        String url = "jdbc:mysql://192.168.1.45:3307/app?useUnicode=true&autoReconnect=true&allowMultiQueries=true";
        String driver="com.mysql.jdbc.Driver";
        String username="root";
        String password="123456";

        String master_encrypt_driver = DescEncrypt.encrypt(driver, DescEncrypt.hex2byte(strKey));
        System.out.println("主库加密后driver===============>" + master_encrypt_driver);
        String master_decrypt_driver = DescEncrypt.decrypt(master_encrypt_driver,DescEncrypt.hex2byte(strKey));
        System.out.println("主库解密后driver===============>" + master_decrypt_driver);

    	String master_url = url;
    	String master_encrypt_url = DescEncrypt.encrypt(master_url, DescEncrypt.hex2byte(strKey));
    	System.out.println("主库加密后url===============>" + master_encrypt_url);
    	String master_decrypt_url = DescEncrypt.decrypt(master_encrypt_url,DescEncrypt.hex2byte(strKey));
    	System.out.println("主库解密后url===============>" + master_decrypt_url);

    	String master_username = username;
    	String master_encrypt_username = DescEncrypt.encrypt(master_username,DescEncrypt.hex2byte(strKey));
    	System.out.println("主库加密后username===============>" + master_encrypt_username);
    	String master_decrypt_username = DescEncrypt.decrypt(master_encrypt_username,DescEncrypt.hex2byte(strKey));
    	System.out.println("主库解密后username===============>" + master_decrypt_username);

    	String master_psd = password;
    	String master_encrypt_psd = DescEncrypt.encrypt(master_psd,DescEncrypt.hex2byte(strKey));
    	System.out.println("主库加密后psd===============>" + master_encrypt_psd);
    	String master_decrypt_psd = DescEncrypt.decrypt(master_encrypt_psd,DescEncrypt.hex2byte(strKey));
    	System.out.println("主库解密后psd===============>" + master_decrypt_psd);
    }

	@Test
	public void  decrypt(){
		String key = "CD1E7D3A7DEED845FCD8AE9F63A294CC07654C1067D6CD8C4BD7715399773167C38695E8C2E4AA11";
		String strKey = "0001000200030004";
		String value = DescEncrypt.decrypt(key, DescEncrypt.hex2byte(strKey));
        System.out.println("解密===============>" + value);
	}

	@Test
	public void  encrypt(){
		/*
		* jdbc.url=jdbc:mysql://192.168.1.46:3306/p2p
jdbc.driver=com.mysql.jdbc.Driver
jdbc.username=root
jdbc.password=123456
		* */
		String key = "123456";
		String strKey = "0001000200030004";
		String value = DescEncrypt.encrypt(key, DescEncrypt.hex2byte(strKey));
		System.out.println("加密===============>" + value);

		/*
		* 46:CD1E7D3A7DEED845FCD8AE9F63A294CC07654C1067D6CD8C4BD7715399773167C38695E8C2E4AA11
		* 45:CD1E7D3A7DEED845FCD8AE9F63A294CC07654C1067D6CD8C4F427772B73AE589C38695E8C2E4AA11
		* root:63AEB7FA5F01BC70
		* 123456:A71F11AC5D1A24B6
		* */
	}
}
