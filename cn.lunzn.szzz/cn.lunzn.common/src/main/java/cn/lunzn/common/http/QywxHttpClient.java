package cn.lunzn.common.http;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;

import cn.lunzn.common.cache.PropertyCache;
import cn.lunzn.common.constant.Constant;
import cn.lunzn.common.enums.CommonResultCode;

/**
 * 企业微信HTTP Client帮助类
 * 
 * @author  clark
 * @version  [版本号, 2017年8月23日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public final class QywxHttpClient
{
    
    /**
     * 企业微信授权码
     */
    private static String qywxAccessToken;
    
    /**
     * 日志记录
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(QywxHttpClient.class);
    
    /** 
     * 受保护的构造函数，静态类无需共有构造函数
     */
    protected QywxHttpClient()
    {
        
    }
    
    /** 
     * 获取企业微信授权码
     * @return String
     * @see [类、类#方法、类#成员]
     */
    public static String getAccessTokenCache()
    {
        return qywxAccessToken;
    }
    
    /** 
     * 刷新企业微信授权码缓存
     * @param newAccessToken 新的企业微信授权码
     * @see [类、类#方法、类#成员]
     */
    public static void refreshAccessTokenCache(String newAccessToken)
    {
        JSONObject newResult = JSONObject.parseObject(newAccessToken);
        
        qywxAccessToken = newResult.getString("access_token");
    }
    
    /** 
     * 获取企业微信授权码
     * @param corpid 企业ID
     * @param corpsecret 应用的凭证密钥
     * @return String
     * @see [类、类#方法、类#成员]
     */
    public static String getAccessToken(String corpid, String corpsecret)
    {
        StringBuffer uri = new StringBuffer("https://qyapi.weixin.qq.com/cgi-bin/gettoken");
        
        uri.append("?corpid=").append(corpid);
        uri.append("&corpsecret=").append(corpsecret);
        
        return get(uri.toString());
    }
    
    /** 
     * http post
     * @param uri 请求地址
     * @param param 请求参数
     * @return String
     * @see [类、类#方法、类#成员]
     */
    public static String post(String uri, JSONObject param)
    {
        LOGGER.info("[QYWX] POST URI: {}, Param: {}", uri, param);
        
        RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(ServiceClient.DEFAULT_TIMEOUT)
            .setConnectTimeout(ServiceClient.DEFAULT_TIMEOUT)
            .build();
        
        CloseableHttpClient client = HttpClients.createDefault();
        
        HttpPost post = new HttpPost(uri);
        
        // 设置参数
        post.setHeader("Content-Type", "application/json;charset=UTF-8");
        post.setConfig(requestConfig);
        StringEntity strEntity = new StringEntity(param.toString(), "utf-8");//解决中文乱码问题
        strEntity.setContentEncoding("UTF-8");
        strEntity.setContentType("application/json");
        post.setEntity(strEntity);
        
        try
        {
            HttpResponse res = client.execute(post);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            {
                HttpEntity entity = res.getEntity();
                
                String result = EntityUtils.toString(entity);
                
                LOGGER.info("[QYWX] POST Response Result ---:{}", result);
                
                return result;
            }
            else
            {
                LOGGER.error("[QYWX] POST Response Error code ---:{}", res.getStatusLine().getStatusCode());
            }
        }
        catch (IOException e)
        {
            LOGGER.error("[QYWX] POST Request  Failed, Error msg:", e);
        }
        
        return get6012001().toString();
    }
    
    /** 
     * http get
     * @param uri 请求地址
     * @return String
     * @see [类、类#方法、类#成员]
     */
    public static String get(String uri)
    {
        LOGGER.info("[QYWX] get URI: {}", uri);
        
        RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(ServiceClient.DEFAULT_TIMEOUT)
            .setConnectTimeout(ServiceClient.DEFAULT_TIMEOUT)
            .build();
        
        CloseableHttpClient client = HttpClients.createDefault();
        
        HttpGet get = new HttpGet(uri);
        
        get.setConfig(requestConfig);
        
        try
        {
            HttpResponse res = client.execute(get);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            {
                HttpEntity entity = res.getEntity();
                
                String result = EntityUtils.toString(entity);
                
                LOGGER.info("[QYWX] GET Response Success, Result ---:{}", result);
                
                return result;
            }
            else
            {
                LOGGER.error("[QYWX] GET Response Failed, Error code ---:{}", res.getStatusLine().getStatusCode());
            }
        }
        catch (IOException e)
        {
            LOGGER.error("[QYWX] GET Request Failed, Error msg:", e);
        }
        
        return get6012001().toString();
    }
    
    /** 
     * 企业微信发送消息
     * @param userId 接收人-单人
     * @param msg 消息体-文本
     * @see [类、类#方法、类#成员]
     */
    public static void sendMsg(String userId, String msg)
    {
        LOGGER.info("[QYWX] Send Message, Send who:{}, Send msg:{}", userId, msg);
        
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(msg))
        {
            return;
        }
        // 发送消息请求地址 POST请求
        StringBuffer uri = new StringBuffer("https://qyapi.weixin.qq.com/cgi-bin/message/send");
        // 授权码
        uri.append("?access_token=").append(getAccessTokenCache());
        
        // 请求主体
        JSONObject requestBoby = new JSONObject();
        // 成员ID列表（消息接收者，多个接收者用‘|’分隔，最多支持1000个）。特殊情况：指定为@all，则向该企业应用的全部成员发送
        requestBoby.put("touser", userId);
        // 消息类型，此时固定为：text
        requestBoby.put("msgtype", "text");
        // 企业应用的id，整型。可在应用的设置页面查看
        requestBoby.put("agentid", PropertyCache.getProp().get(Constant.QYWX_AGENTID_KEY));
        // 消息内容，最长不超过2048个字节
        JSONObject content = new JSONObject();
        content.put("content", msg);
        requestBoby.put("text", content);
        
        // 表示是否是保密消息，0表示否，1表示是，默认0
        requestBoby.put("safe", 0);
        
        JSONObject result = JSONObject.parseObject(post(uri.toString(), requestBoby));
        if (result.getIntValue("errcode") == 0)
        {
            LOGGER.info("[QYWX] Send Message Success");
        }
        else
        {
            LOGGER.info("[QYWX] Send Message Failed, Error code{} msg:{}", result.get("errcode"), result.get("errmsg"));
        }
    }
    
    /** 
     * 企业微信-请求异常
     * Tips企业微信错误码：0-表示成功， 非0表示失败
     * @return JSONObject 默认错误码
     * @see [类、类#方法、类#成员]
     */
    public static JSONObject get6012001()
    {
        JSONObject err6012001 = new JSONObject();
        err6012001.put("errcode", CommonResultCode.SERVICE_BUSINESS_6012001.getCode());
        err6012001.put("errmsg", CommonResultCode.SERVICE_BUSINESS_6012001.getMsg());
        return err6012001;
    }
}
