package cn.tellwhy.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>json工具类</p>
 * @author xugang Email:xugang@caijinquan.com
 * @company 财金圈（北京）金融服务外包有限公司@版权所有
 * @since 2017年5月25日上午11:09:56
 */
public class JsonUtil {

	private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);

	/**
	 * 字符串转换为json对象
	 * @methodDesc <p>  </p>
	 * @author xugang
	 * @since 2017年5月25日上午11:18:32
	 * @version appVersion 3.4
	 * @param jsonString json字符串
	 */
	public static JSONObject getJsonObj(String jsonString){
    	JSONObject obj = null;
    	try{
    		obj = JSON.parseObject(jsonString);

    	}catch (Exception e) {
    		e.printStackTrace();
    		log.error("转换为json对象失败，字符串为:{}",jsonString);
		}
    	return null == obj?new JSONObject():obj;
	}
}

