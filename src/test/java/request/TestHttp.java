package request;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.junit.Test;
import run.StressTest;
import structure.ConcurrentHashMapExtend;
import structure.Pair;
import util.ConcurrentRun;
import util.WebRequestClient;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class TestHttp {
    
//    curl -i -X POST -H "'Content-type':'application/json', 'charset':'utf-8' -d 'json_data={"loginName":"aaaa","registerType":"1","deviceId":"aaa","channelCode":"aaa","msgCode":"123456"},"hmac":"aaaaa"}'" http://192.168.1.68:8080/portal-bos/app/appv4/appLoginWit.action
//    private static final String URL = "http://192.168.1.47:8080/";
    private static final String URL = "http://192.168.1.2:8080/";
    final String loginToken = "e2d0576cb7fb4193b2e480e9e96f2339";
    final String loginUserID = "1";

    
    private ConcurrentHashMapExtend<String, String, Integer> urls;
    private ConcurrentHashMapExtend<String, String, Integer> buildUrls(){
        urls = new ConcurrentHashMapExtend<>();
        urls.putKeysList(11,
                new Pair("app/firstShow/not/queryFirstShow.action", "{\"hmac\":\"NyKPvrVqqjmdR0gkoQEFlvw/GuEucDHUKUgf7KHLlYldlE2LjwttbF2qREij1KaxEtrYuNlBd2tWemFPfncaTjfaXjTkddRvKiUezjHeaYQmfY/qFZT1MNRB/kZt/BmM54dEOCnEcBnsnJcRfmvd2WmWxF0c0ptYwo21q5hNFQc=\",\"params\":{}}"),
                new Pair("app/notice/not/queryNoticeList.action", "{\"hmac\":\"UI7QbO+oCcGsmCLgr/bLlmkV2moq7ngZkemuIp0BqbxlLn7oWOz75aQ+5ODCwW5tDH+UTQgkvyj88D8PnH4t1TEwTZXBrWfmBMwWskKS4wE5HQMxyOu+4MpfPg5u2scrAmSy42TxF9PlVdwCDZL6LOM5Wq22DSFU+QRbKz3M4Bw=\",\"curPage\":\"1\",\"params\":{\"isindex\":\"0\",\"msgtype\":\"3\",\"phone\":\"18500342698\"}}"),
                new Pair("app/notice/queryMyMessNoNotRead.action", "{\"curPage\":1,\"params\":{\"msgtype\":\"1\",\"phone\":\"15111111111\"},\"hmac\":\"XDgU1q6p3kro6rX1nlxCiKgScqVOXGHlB1IqpfZxoBG+wncE00Ut6upnYmKWyEJGSoWqzJywG2ZShkFbkSPVzznuY+nXxhuoIkak7tMH9mBx37TNgXT8tekwkkem/Scx1CpOwN93nbsZxvXQZg6jma6465h3knQjduofaOmnq1o=\"}"),
                new Pair("/app/advertisement/not/queryHomePageAdvertList.action", "{\"hmac\":\"a29Lz0eEz5OGGvigpoOKQGnYfc1yXVJ9q8B5y1Pk5R87CGCOW33yuanihExPcJ1PnI8ezzXM6ObZ3IpjHze4NJmRrPuOF0p/ZaY7wXSVcBZt/8useaQsZU6lryWoGo/eI6k/UiO9M+sjYCWKh9qalkd2kOA6wcAOBgcgsgszceU=\",\"params\":{}}"),
                new Pair("app/newTask/taskProgress.action", "{\"hmac\":\"Ib/jJuoQ5r8LQLa3eHm3GNm4EwuElbyECTkJ3rI+v7mx+kL6olfqGnqjugx6LDjeKhDUlcRGO85q3qqtkzHD6MYD2BcJiSCmv7hlGLYYgy/gXEQ5CdUirghb7jsME2HnJgQQvibbx2SAaDpm6MGTvs0Vf3ohhC3dLvv8Yx+1BmY=\"}"));
        urls.putKeysList(7,
                new Pair("app/advertisement/not/queryAppPopAdvertisementPic.action", "{\"hmac\":\"dveo5qx2oXJRKoucGwQdF8g8EtzAINW/vyJZXjR0RqiyrK5ST4EFuOpRtosuadztVJbYUsP7+v5Xuy5n7/8qoTHxETZkYAGNv3pi2IXnMkvXjaYD+BMON02j1ggveWnzU9iPVotCnKIOoxYn1MbXwI4sF4xFopeadGSczxvn5lM=\",\"params\":{}}"),
                new Pair("app/discovery/not/queryDiscovery.action", "{\"hmac\":\"ThBtfTMPCAWdR/3Y8UUGHBg5uAmJPFQ6Q+Lc+KUHiaELySmIQmWxrPPcMm70aVtI1rFAHvdKmX4URZKSAQpGzC2U0CarXW59ycuLFPUd9/Sm/sBgunFQAhxOLHF2t/DxNolf2OLNmVqjTsMZ2QQ+AhFdbJHzJrUWtAwdfzVp1M0=\"}"),
                new Pair("app/menu/not/list.action", "{\"hmac\":\"j8zPP4qbxgM/88jIwEmISIZJLTWtWnK0KleTs0w8fLw2xN9CmFqAOtnNamaN6U2Dz3w11tiSzoFTdeJZ1iJhGapeCkcF9PUgYa692k0Qx2slX1vSrYY+zADn5XQz8c7U+KEVRkNOTVpgS+wTK88vgQpn56qMI5yOIzXd+WsRjvU=\",\"params\":{}}"),
                new Pair("app/activityArea/queryActivityAreaList.action", "{\"hmac\":\"iC9VinR1FUojMJJXALKeBlde1An5lX2Z9tRh1ObpYjWOKR9Bt3kRS0jA87N+xQBjViUleuWGayF4MvvNBBUYWLVk92h+mYv7tsQyFagIEqVEQ6V9k6QanjzZdBWjUBkSjjvBRTfF4LJYOkNHaw0B71cB3BKZSyUFEFOgYLA+qPo=\",\"curPage\":\"1\",\"params\":{\"activityType\":\"0\"}}"),
                new Pair("app/activityArea/not/queryFinanceActivity.action", "{\"hmac\":\"Qde4rWdDULlfgUFL2IfwicDyWIf5GBdRisEPiTPQOWidpKJp1n5f7pb8wdcALbPZMEctm6zvfCwnVhC7pnqpXCRbswm7Vfk3K9feKToYIxstbf9laJVLHfOm2bKAiLJbWMCtBY/7RyEvLg/VWvrMIyoL0t1v36zmjR1JkhlXnHo=\",\"params\":{\"type\":\"1\"}}"));
        urls.putKeysList(5,
//                new Pair("/share/getMicroMsgInfoCrossDomain.action", ""),
//                new Pair("/newTask/taskBanner.action", ""),
                new Pair("app/bos/myaccount/myAccountComprehensive.action", "{\"hmac\":\"T46zg8mZ/TxjqobKRdEujI+bBIJRdcb4hVVKcJ0HiFwCosGODv2HRWEppAlFpl2sfKUo3sgqOEnqm8+USs4oHGCPS95dTCAxvMGrpjMZOmmqIl4vCLyuVm4U8QdSWLhWoZud+qEM7EwN5Q0HagwLAOmijzFQj9dqzMVxqeeRgWM=\",\"params\":{}}"),
                new Pair("app/bos/finance/not/queryRegularList.action", "{\"hmac\":\"VH3Qj+2nIpEsBNXIed9wPn3aCGcoIrCOApZdlrIGE2h25FZE10/c5RcjdE2MuFuvD+5sBPKRycPQhQMnLeCe2eU+lrKtcw3fbjw2LzRs6WW3mU4EWMpVS3An44mxFYmdf1+CTOjNt07adAgZfyKR7lYWri1WB9vMBE3MUWB/fe0=\"}"),
                new Pair("app/bos/finance/not/queryNewCustBidList.action", "{\"hmac\":\"NT1nRSCVtYfbvkMVKlSCJrnjbi/aRpo444qyrip9T6yhyPB69a0WgcrcNI2LT3/UyBN0YPBX/Tusyko+nhDxola7pOqhQwBcQu+ozhwTyQQPusWjGA1Dc1I7Z6j79iRA3VHeae2h5UpptvPEJz6696BYT+bgUsXI/IQdD8E6UiI=\"}"),
                new Pair("app/gesture/checkGesture.action", "{\"hmac\":\"b6SUfZcLRJPeP+w2IqvI+Yqzh72vce02jQZ2LFyJgFgjfU5ZGK2WnAzWQH+aBb3Q1MGxhCl5BgTBAJpeJc3uaaF/Kqu13oSq5jTGGLhRxyYhZS82P/r3p/7EObBYOmaGJoKlTCYD4nzaECiRmoPXfTod3/5AST2RDo50AllpWTU=\",\"params\":{\"errorTimes\":\"5\",\"gesturePsd\":\"76d1dabed06c7a234de1caab62e84969e34d770e\",\"loginName\":\"15210080001\"}}"));
        urls.putKeysList(3,
                new Pair("app/bos/finance/not/queryExpFinanceDetail.action", "{\"hmac\":\"RotvNuoD6uGmgoq0L90IVmzZZy1UefeOVY8E6p4Jq2+yHcvca1qklzcI8sL6Reqx/+3jKUlhsHeXWJwWgGOJlX5NKq1k983KjSm2Fvs1sU/Wyt1HHOH8jMp1V7dkw8abkhJCyXLvYUDZhSRg9MvqJ6NybcSIKmt9yh4qG95kzT0=\"}"),
//                new Pair("/signIn/newSignIn.action", ""),
                new Pair("app/version/not/updateAll.action", "{\"hmac\":\"G7z0tsihl1VLWtWQjg7AsqV47eNQmQhNoMMnIOTdNX9kRuK7WbAg8dCn1pSCpv1tKwrHIXQsOGmJ09IYjEze2YQ/02QjMTskIt57yXawsdV9C0qEfW/RjrWK94tsZlt9QWfCsxJnRJfTqcDuFlGZMlTSSH0MSxxAftNwroUxNXs=\",\"params\":{\"appType\":\"2\",\"appVersion\":\"20\"}}"),
                new Pair("app/advertisement/not/queryLoadingPic.action", "{\"hmac\":\"ONeNxw1vue7vhJM6Ib9TG/29oL9ORgbdCgeT6evsYNozhXwGexNbd+81+0FVHX+hn43RHwCZbqG7rHL7s2iery6fysN5E97eXjk8xiDBl3rx2h13qyROPpEDQ+pmy2NYrZCNi3F77h8uhP0xGn4Zvh34/nhRT5RPzvSKe+qv/t4=\",\"params\":{}}"));
        return urls;
    }
    
    @Test
    public void postPress() throws IOException, InterruptedException {
        int enlarge = 10;
        List<Runnable> runnableList = new ArrayList<>();
        urls = buildUrls();
//        urls = buildOldUrls();
        StressTest.circleRequests(119, enlarge, urls, runnableList);

        ConcurrentRun.executeTasks(runnableList);
    }
    
    private void execSingleRequest(String action, String json) throws IOException {
        StringEntity paras = new StringEntity(json);;
        paras.setContentEncoding("UTF-8");
        paras.setContentType("application/json");
        post(action, "JSESSIONID", "15111111111", paras);
    }
    
    @Test
    public void postSinglePress() throws IOException {
        String cookieValue = "e2d0576cb7fb4193b2e480e9e96f2339";
        String action = "app/bos/finance/financeInvest.action";
        String json = "{\"params\":{\"checkPwd\":\"7c4a8d09ca3762af61e59520943dc26494f8941b\",\"fid\":\"11886\",\"financeMoney\":\"200\"},\"hmac\":\"CBSDWEY34nIi+e04jlxxFrp2Er10NzxW0wevsbhERY9YxypGTq3gQSbkhjyeKkCxIQcHEqx4t9CHkPRB/7nF3dLD4FA0pPS2AmuIqRo2gkSxgMyTJz8occocao1ha4Gfr0jmTE0ep3BlZb/lIh35Wx8AFa+CiXrUvzFyOHeT1tk=\"}\n";
//        String cookieValue = "8c593810de1447f987bde834f24900b6";
//        String action = "promotion-service/advertisement/not/queryShopAdvertList.action";
//        String json = "{\"hmac\": \"4931fc0765a17293c8635b1759724e146b078193\", \"params\": {\"bannerType\": \"1\"}}";
        StringEntity paras = new StringEntity(json);;
        paras.setContentEncoding("UTF-8");
        paras.setContentType("application/json");
        String result = post(action, "JSESSIONID", cookieValue, paras);
        System.out.println("result:" + result);
    }
    
    private ConcurrentHashMapExtend<String, String, Integer> buildOldUrls(){
        urls = new ConcurrentHashMapExtend<>();
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
//                new Pair("/share/getMicroMsgInfoCrossDomain.action", ""),
//                new Pair("/newTask/taskBanner.action", ""),
//                new Pair("app/bos/myaccount/myAccountComprehensive.action", "{\"hmac\":\"T46zg8mZ/TxjqobKRdEujI+bBIJRdcb4hVVKcJ0HiFwCosGODv2HRWEppAlFpl2sfKUo3sgqOEnqm8+USs4oHGCPS95dTCAxvMGrpjMZOmmqIl4vCLyuVm4U8QdSWLhWoZud+qEM7EwN5Q0HagwLAOmijzFQj9dqzMVxqeeRgWM=\",\"params\":{}}"),
                new Pair("portal-bos/app/appv5/regularFinance/queryRegularListV5.action", "{\"curPage\":1,\"hmac\":\"b3742274abc71329407aa107f9faffa60e326cd8\"}"),
                new Pair("portal-bos/app/appv5/newCustBid/queryNewCustBidList.action", "{\"hmac\":\"033e2c69657a002549e70c114f887ea695096c23\"}"),
//                new Pair("portal-bos/app/appv5/regularFinance/queryRegularDetailExperience.action", "{\"hmac\":\"033e2c69657a002549e70c114f887ea695096c23\"}"),
                new Pair("portal-bos/app/appv4/checkOpenGesture.actionn", "{\"params\":{\"loginName\":\"15111111111\"},\"hmac\":\"ea0735be1cdd21bc7bdb2dde215a739271936041\"}"));
        urls.putKeysList(3,
                new Pair("portal-bos/app/appv4/updateAll.action", "{\"params\":{\"appType\":\"1\",\"appVersion\":39},\"hmac\":\"cc3e60efae506f58b6e85311fe8317ea004d16c2\"}"),
                new Pair("portal-bos/app/appv4/advertisement/queryAppPopAdvertisementPic.action", "{\"hmac\":\"033e2c69657a002549e70c114f887ea695096c23\"}"),
//                new Pair("/signIn/newSignIn.action", ""),
                new Pair("portal-bos/app/appv4/advertisement/queryLoadingPic.action", "{\"hmac\":\"033e2c69657a002549e70c114f887ea695096c23\"}"));
        return urls;
    }
    
    @Test
    public void postOld() throws IOException {
        String cookieValue = "dfa1f765c71b43a89cb3ab8bb8f2c903";
        String action = "portal-bos/app/appv5/regularFinance/queryRegularListV5.action";
        String json = "{\"curPage\":1,\"hmac\":\"b3742274abc71329407aa107f9faffa60e326cd8\"}";
        StringEntity paras = new StringEntity(json);;
        paras.setContentEncoding("UTF-8");
        paras.setContentType("application/json");
        String result = post(action, "JSESSIONID", cookieValue, paras);
        System.out.println("result:" + result);
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
            result = WebRequestClient.testLinkGet(URL + action + paras, loginToken, loginUserID);
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
            result = WebRequestClient.testPostWithCookie(URL + action, cookieKey, cookieValue, paras);
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
            result = WebRequestClient.testLinkPost(URL + action, loginToken, loginUserID, paras);
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
