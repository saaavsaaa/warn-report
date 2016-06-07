import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.junit.Test;

import java.io.IOException;
import java.net.URLEncoder;

public class TestHttp {
    private static final String URL = "http://192.168.1.47:8080/";
    final String loginToken = "115d522ca69ea091c63f09f5d7e5bc9c";
    final String loginUserID = "1";

    @Test
    public void test() throws IOException {
        String action = "gett";
        String paras = "?code=111111111111a&start=" + URLEncoder.encode("2015-01-04 15:03", "UTF-8");
        String result = getResult(action, paras);
    }

    @Test
    public void copyArrayTest(){
        long[] elementData = new long[]{1,2,3,4,5,6};
        long[] a = new long[]{11,12,13,14,15};
        System.arraycopy(elementData, 3, a, 1, 3);
        long[] result = a;
        for (int i = 0; i < result.length; i++) {
            System.out.println(result[i]);
        }
    }
 
    public void post() throws IOException {
        String action = URL + "aaa";
        String json = "";
        StringEntity paras = new StringEntity(json);;
        paras.setContentEncoding("UTF-8");
        paras.setContentType("application/json");
        post(action, paras);
    }

    private String getResult(String action, String paras) throws IOException {
        String result = null;
        try {
            result = TestWebRequestWithLogin.testLinkGet(URL + action + paras, loginToken, loginUserID);
        } catch (Exception e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
        System.out.println(result);
        return result;
    }

    private String post(String action, HttpEntity paras) throws IOException {
        String result = null;
        try {
            result = TestWebRequestWithLogin.testLinkPost(URL + action, loginToken, loginUserID, paras);
        } catch (Exception e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
        System.out.println(result);
        return result;
    }

    @Test
    public void testRegister() throws IOException {
        String action = "portal-bos/fg/register/registerSubmitTuiGuangWithoutPwd.action";
        String json = "{\"loginName\":\"13211111333\"," +
                "\"messageCode\":\"123456\"," +
                "\"channelCode\":\"jinritoutiao\"," +
                "\"callback\":\"_huskd\"" +
                "}";

        System.out.println(json);
        StringEntity paras = new StringEntity(json);;
        paras.setContentEncoding("UTF-8");
        paras.setContentType("application/json");
        post(action, paras);
    }
}
