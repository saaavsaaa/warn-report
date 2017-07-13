import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.junit.Test;
import structure.ConcurrentHashMapExtend;
import structure.Pair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class TestHttp {
    
//    curl -i -X POST -H "'Content-type':'application/json', 'charset':'utf-8' -d 'json_data={"loginName":"aaaa","registerType":"1","deviceId":"aaa","channelCode":"aaa","msgCode":"123456"},"hmac":"aaaaa"}'" http://192.168.1.68:8080/portal-bos/app/appv4/appLoginWit.action
//    private static final String URL = "http://192.168.1.47:8080/";
    private static final String URL = "http://192.168.1.213:8088/";
    final String loginToken = "115d522ca69ea091c63f09f5d7e5bc9c";
    final String loginUserID = "1";

    
    private ConcurrentHashMapExtend<String, String, Integer> urls;
    private ConcurrentHashMapExtend<String, String, Integer> buildUrls(){
        urls = new ConcurrentHashMapExtend<>();
        urls.putKeysList(11,
                new Pair("app/firstShow/not/queryFirstShow.action", "{\"hmac\":\"NyKPvrVqqjmdR0gkoQEFlvw/GuEucDHUKUgf7KHLlYldlE2LjwttbF2qREij1KaxEtrYuNlBd2tWemFPfncaTjfaXjTkddRvKiUezjHeaYQmfY/qFZT1MNRB/kZt/BmM54dEOCnEcBnsnJcRfmvd2WmWxF0c0ptYwo21q5hNFQc=\",\"params\":{}}"),
                new Pair("app/firstShow/not/queryNoticeList.action", "{\"curPage\":1,\"params\":{\"msgtype\":\"1\",\"phone\":\"15111111111\"},\"hmac\":\"XDgU1q6p3kro6rX1nlxCiKgScqVOXGHlB1IqpfZxoBG+wncE00Ut6upnYmKWyEJGSoWqzJywG2ZShkFbkSPVzznuY+nXxhuoIkak7tMH9mBx37TNgXT8tekwkkem/Scx1CpOwN93nbsZxvXQZg6jma6465h3knQjduofaOmnq1o=\"}"),
                new Pair("/notice/queryMyMessNoNotRead.action", ""),
                new Pair("/advertisement/not/queryHomePageAdvertList.action", ""),
                new Pair("/newTask/taskProgress.action", ""));
        urls.putKeysList(7,
                new Pair("/advertisement/not/queryAppPopAdvertisementPic.action", ""),
                new Pair("/discovery/not/queryDiscovery.action", ""),
                new Pair("/menu/not/list.action", ""),
                new Pair("/activityArea/queryActivityAreaList.action", ""),
                new Pair("/activityArea/not/queryFinanceActivity.action", ""));
        urls.putKeysList(5,
                new Pair("/share/getMicroMsgInfoCrossDomain.action", ""),
                new Pair("/newTask/taskBanner.action", ""),
                new Pair("/bos/myaccount/myAccountComprehensive.action", ""),
                new Pair("/bos/finance/not/queryRegularList.action", ""),
                new Pair("/bos/finance/not/queryNewCustBidList.action", ""),
                new Pair("/gesture/checkGesture.action", ""));
        urls.putKeysList(3,
                new Pair("/bos/finance/not/queryExpFinanceDetail.action", ""),
                new Pair("/signIn/newSignIn.action", ""),
                new Pair("/version/not/updateAll.action", ""),
                new Pair("/advertisement/not/queryLoadingPic.action", ""));
        return urls;
    }
    
    @Test
    public void postPress() throws IOException {
        //（/app/firstShow/not/queryFirstShow.action）:{"hmac":"NyKPvrVqqjmdR0gkoQEFlvw/GuEucDHUKUgf7KHLlYldlE2LjwttbF2qREij1KaxEtrYuNlBd2tWemFPfncaTjfaXjTkddRvKiUezjHeaYQmfY/qFZT1MNRB/kZt/BmM54dEOCnEcBnsnJcRfmvd2WmWxF0c0ptYwo21q5hNFQc=","params":{}}
        String action = "app/firstShow/not/queryFirstShow.action";
        String json = "{\"hmac\":\"NyKPvrVqqjmdR0gkoQEFlvw/GuEucDHUKUgf7KHLlYldlE2LjwttbF2qREij1KaxEtrYuNlBd2tWemFPfncaTjfaXjTkddRvKiUezjHeaYQmfY/qFZT1MNRB/kZt/BmM54dEOCnEcBnsnJcRfmvd2WmWxF0c0ptYwo21q5hNFQc=\",\"params\":{}}";
        StringEntity paras = new StringEntity(json);;
        paras.setContentEncoding("UTF-8");
        paras.setContentType("application/json");
        post(action, "JSESSIONID", "15111111111", paras);
    }
    /*
    * /firstShow/not/queryFirstShow.action 查询首页展示项目
    * /notice/not/queryNoticeList.action 查询资讯消息列表
    * {"curPage":1,
    * "params":{"msgtype":"1","phone":"15111111111"},
    * "hmac":"XDgU1q6p3kro6rX1nlxCiKgScqVOXGHlB1IqpfZxoBG+wncE00Ut6upnYmKWyEJGSoWqzJywG2ZShkFbkSPVzznuY+nXxhuoIkak7tMH9mBx37TNgXT8tekwkkem/Scx1CpOwN93nbsZxvXQZg6jma6465h3knQjduofaOmnq1o="}
    * /notice/queryMyMessNoNotRead.action 查询我的未读消息条数
    * /advertisement/not/queryHomePageAdvertList.action 首页广告轮播图
    * /newTask/taskProgress.action 查看任务进度
    *
    * /advertisement/not/queryAppPopAdvertisementPic.action
    * /discovery/not/queryDiscovery.action 发现控制器
    * /menu/not/list.action
    * /activityArea/queryActivityAreaList.action
    * /activityArea/not/queryFinanceActivity.action 跟据类型查询投资页活动
    *
    * /share/getMicroMsgInfoCrossDomain.action 获取微信js sdk signature 跨域访问
    * /newTask/taskBanner.action 新手任务跳转
    * /bos/myaccount/myAccountComprehensive.action 查询  我的账户页的数据展示
    * /bos/finance/not/queryRegularList.action 查询定期项目列表
    * /bos/finance/not/queryNewCustBidList.action 查询新手标列表
    * /gesture/checkGesture.action 校验手势密码接口
    *
    * /bos/finance/not/queryExpFinanceDetail.action 查询最新体验标详情，不需要参数
    * /signIn/newSignIn.action 用户每日签到、分享
    * /version/not/updateAll.action 图片广告
    * /advertisement/not/queryLoadingPic.action
    * */
    
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
    
    private String post(String action, String cookieKey, String cookieValue,HttpEntity paras) throws IOException {
        String result = null;
        try {
            result = TestWebRequestWithLogin.testPostWithCookie(URL + action, cookieKey, cookieValue, paras);
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
        String action = "portal-bos/fg/register/registerSubmit.action";
        String json = "{" +
                "\"loginName\":\"13211111333\"," +
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
