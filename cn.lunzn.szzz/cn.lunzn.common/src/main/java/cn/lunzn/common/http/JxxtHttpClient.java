package cn.lunzn.common.http;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import cn.lunzn.common.constant.ResultCodeJson;

/**
 * 绩效系统HTTP Client帮助类
 * 
 * @author  clark
 * @version  [版本号, 2017年8月23日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public final class JxxtHttpClient
{
    /**
     * 日志记录
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JxxtHttpClient.class);
    
    /** 
     * 受保护的构造函数，静态类无需共有构造函数
     */
    protected JxxtHttpClient()
    {
        
    }
    
    /** 
     * HTTP POST
     * @param uri 请求地址
     * @param param 请求参数
     * @return String
     * @see [类、类#方法、类#成员]
     */
    public static String post(String uri, JSONObject param)
    {
        LOGGER.info("[JXXT] POST URI: {}, Param: {}", uri, param);
        
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
                
                LOGGER.info("[JXXT] POST Response Result ---:{}", result);
                
                return result;
            }
            else
            {
                LOGGER.error("[JXXT] POST Response Error code ---:{}", res.getStatusLine().getStatusCode());
            }
        }
        catch (IOException e)
        {
            LOGGER.error("[JXXT] POST Request  Failed, Error msg:", e);
        }
        
        return ResultCodeJson.get6013001().toString();
    }
}
