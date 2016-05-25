import encrypt.DescEncrypt;
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
		String key = "CD1E7D3A7DEED8459952EB18207901EFA2609D1EFBE126EAC3B3CE6B2F92E84E388AB6DA10601630FD10574F028E7073485FC887BC90586FB212267C0B3601A3D5D8D18A217B0D698E7B22A595D275E2632CBCB38DE7E57BCFD9B37BDAB2691BEE1E5065D3287032722345CDF1D0CCD614F7F9CDFFAA692493AFC4C3F3C57DC3";
		String strKey = "0001000200030004";
		String value = DescEncrypt.decrypt(key,DescEncrypt.hex2byte(strKey));
        System.out.println("解密===============>" + value);
	}
}
