package run;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import structure.ConcurrentHashMapExtend;
import structure.Pair;
import util.ConcurrentRun;
import util.WebRequestClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aaa on 17-10-10.
 */
public class StressTest {
    private static final String requestURL = "http://192.168.1.215:8080/";
    private static final String sessionId = "002d796d0a8c45c99aa323ba17ab9bcb";
    
    public static void main(String[] args){
        int enlarge = 10;
        List<Runnable> runnableList = new ArrayList<>();
        ConcurrentHashMapExtend<String, String, Integer> urls = buildOldUrls();
        urls.forEachEntry(119, (e) -> {
            String action = e.getKey();
            Pair pair = e.getValue();
            String json = (String) pair.getK();
            int radio = (int) pair.getV() * enlarge;
            Runnable runnable = () -> {
                try {
                    execSingleRequest(action, json);
                } catch (IOException ee) {
                    System.out.println(ee.getMessage());
                }
            };
            for (int i = 0; i < radio; i++) {
                runnableList.add(runnable);
            }
        });
        
        while (true) {
            try {
                ConcurrentRun.executeTasks(runnableList);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }
    
    private static void execSingleRequest(String action, String json) throws IOException {
        StringEntity paras = new StringEntity(json);;
        paras.setContentEncoding("UTF-8");
        paras.setContentType("application/json");
        post(action, "JSESSIONID", sessionId, paras);
    }
    
    private static String post(String action, String cookieKey, String cookieValue,HttpEntity paras) throws IOException {
        String result = null;
        try {
            result = WebRequestClient.testPostWithCookie(requestURL + action, cookieKey, cookieValue, paras);
        } catch (Exception e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
        System.out.println(result);
        return result;
    }
    
    private static ConcurrentHashMapExtend<String, String, Integer> buildOldUrls(){
        ConcurrentHashMapExtend<String, String, Integer> urls = new ConcurrentHashMapExtend<>();
        urls.putKeysList(11,
                new Pair("portal-bos/app/appv4/notice/queryNoticeList.action", "{\"curPage\":1,\"params\":{\"msgtype\":\"3\",\"isindex\":\"1\"},\"hmac\":\"a5a052efb20641d0fd09230e8a20f53c6a4007e4\"}"),
                new Pair("portal-bos/app/activityArea/queryLatestActivity.action", "{\"type\":\"4\",\"hmac\":\"d35c75787e07a679d3a42a9168c5c515a0254c48\"}"),
                new Pair("portal-bos/app/appv5/regularFinance/queryRegularDetailExperience.action", "{\"hmac\":\"033e2c69657a002549e70c114f887ea695096c23\"}"),
                new Pair("portal-bos/app/appv4/firstShowV2/queryFirstShowV3.action", "{\"hmac\":\"033e2c69657a002549e70c114f887ea695096c23\"}"),
                new Pair("portal-bos/app/appv3/financeMenuList.action", "{\"hmac\":\"033e2c69657a002549e70c114f887ea695096c23\"}"));
        urls.putKeysList(7,
                new Pair("portal-bos/app/activityArea/queryActivityAreaList.action", "{\"curPage\":\"1\",\"hmac\":\"57a1e2787405c6fd397f13e9431a4511236d708b\"}"),
                new Pair("portal-bos/app/discovery.action", "{\"hmac\":\"033e2c69657a002549e70c114f887ea695096c23\"}"),
                new Pair("portal-bos/app/appv3/advert/queryAdvertListv4.action", "{\"hmac\":\"033e2c69657a002549e70c114f887ea695096c23\"}"),
                new Pair("portal-bos/app/newTask/taskProgressV2.action", "{\"hmac\":\"033e2c69657a002549e70c114f887ea695096c23\"}"),
                new Pair("portal-bos/app/appv3/notice/queryMyMessNoNotReadV2.action", "{\"curPage\": 1,\"params\": {\"phone\": \"\"  },\"hmac\": \"278abbc069efc1220b47d47f6d75779112cde72a\"}"));
        urls.putKeysList(5,
                new Pair("portal-bos/app/appv5/regularFinance/queryRegularListV5.action", "{\"curPage\":1,\"hmac\":\"b3742274abc71329407aa107f9faffa60e326cd8\"}"),
                new Pair("portal-bos/app/appv5/newCustBid/queryNewCustBidList.action", "{\"hmac\":\"033e2c69657a002549e70c114f887ea695096c23\"}"),
                new Pair("portal-bos/app/appv4/checkOpenGesture.actionn", "{\"params\":{\"loginName\":\"15111111111\"},\"hmac\":\"ea0735be1cdd21bc7bdb2dde215a739271936041\"}"));
        urls.putKeysList(3,
                new Pair("portal-bos/app/appv4/updateAll.action", "{\"params\":{\"appType\":\"1\",\"appVersion\":39},\"hmac\":\"cc3e60efae506f58b6e85311fe8317ea004d16c2\"}"),
                new Pair("portal-bos/app/appv4/advertisement/queryAppPopAdvertisementPic.action", "{\"hmac\":\"033e2c69657a002549e70c114f887ea695096c23\"}"),
                new Pair("portal-bos/app/appv4/advertisement/queryLoadingPic.action", "{\"hmac\":\"033e2c69657a002549e70c114f887ea695096c23\"}"));
        return urls;
    }
}
