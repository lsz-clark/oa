package cn.lunzn.common.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import cn.lunzn.common.annotation.Location;
import cn.lunzn.common.bean.BaseBean;
import cn.lunzn.common.bean.BaseResponse;
import cn.lunzn.common.cache.PropertyCache;
import cn.lunzn.common.enums.CommonResultCode;

/**
 * @author clark
 * 远程请求帮助类
 */
public final class ServiceClient
{
    /**
     * 默认超时时间
     */
    public static final int DEFAULT_TIMEOUT = 60000;
    
    /**
     * 日志记录
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceClient.class);
    
    /** 
     * 受保护的构造函数，静态类无需共有构造函数
     */
    protected ServiceClient()
    {
        
    }
    
    /**
     * 针对接口返回集合定制方法
     * 
     * @param reqData 请求参数
     * @param uriSuffix 接口key
     * @param type 泛型
     * @param <T> 返回类型
     * @return ListResponse
     */
    public static <T> T callList(BaseBean reqData, String uriSuffix, TypeReference<T> type)
    {
        String resultJson = call(reqData, uriSuffix);
        
        return JSONObject.parseObject(resultJson, type);
    }
    
    /**
     * 请求远程服务接口
     * @param reqData 请求参数
     * @param uriSuffix 请求配置名称，后缀
     * @param cls 响应对象
     * @param <T> 返回类型
     * @return T
     */
    @SuppressWarnings("all")
    public static <T> T call(BaseBean reqData, String uriSuffix, Class<T> cls)
    {
        T t = JSONObject.toJavaObject(JSON.parseObject(call(reqData, uriSuffix)), cls);
        return t;
    }
    
    /**
     * 请求远程服务接口
     * @param reqData 请求参数
     * @param uriSuffix 请求配置名称，后缀
     * @return String
     */
    @SuppressWarnings("all")
    public static String call(BaseBean reqData, String uriSuffix)
    {
        RequestConfig requestConfig =
            RequestConfig.custom().setSocketTimeout(DEFAULT_TIMEOUT).setConnectTimeout(DEFAULT_TIMEOUT).build();
        
        /*Map<String, String> params = new HashMap<String, String>();
        
        params.put("data", JSONObject.toJSON((reqData)).toString());
        params.put("serverId", "303");
        params.put("requestTime", DateUtil.formatDateToString("yyyyMMddHHmmss", new Date()));
        params.put("startTime", new Date().getTime() + "");
        params.put("digest", "213");*/
        
        CloseableHttpClient client = HttpClients.createDefault();
        
        Location location = reqData.getClass().getAnnotation(Location.class);
        String remoteUrl = PropertyCache.getProp().getString(location.module() + ".server.url");
        String uri = PropertyCache.getProp().getString(location.module() + '.' + uriSuffix);
        try
        {
            // 拼装参数
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(remoteUrl);
            urlBuilder.append(uri);
            
            LOGGER.info("--- Request URL ---:{}", urlBuilder.toString());
            
            HttpPost post = new HttpPost(urlBuilder.toString());
            
            post.setHeader("Content-Type", "application/json;charset=UTF-8");
            
            post.setConfig(requestConfig);
            
            /*List<NameValuePair> list = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> param : params.entrySet())
            {
                list.add(new BasicNameValuePair(param.getKey(), String.valueOf(param.getValue())));
            }*/
            
            StringEntity strEntity = new StringEntity(JSONObject.toJSON((reqData)).toString(), "utf-8");//解决中文乱码问题
            strEntity.setContentEncoding("UTF-8");
            strEntity.setContentType("application/json");
            post.setEntity(strEntity);
            
            /*UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(list, "utf-8");
            formEntity.setContentType("application/json;charset=UTF-8");
            post.setEntity(formEntity);*/
            
            HttpResponse res = client.execute(post);
            
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
            {
                HttpEntity entity = res.getEntity();
                
                String result = EntityUtils.toString(entity);
                
                LOGGER.info("--- Response Result ---:{}", result);
                
                return result;
            }
            else
            {
                LOGGER.error("--- Response Error Code ---:{}", res.getStatusLine().getStatusCode());
            }
        }
        catch (UnsupportedEncodingException e)
        {
            LOGGER.error("Http Request Failed...", e);
        }
        catch (ClientProtocolException e)
        {
            LOGGER.error("Http Request Failed...", e);
        }
        catch (IOException e)
        {
            LOGGER.error("Http Request Failed...", e);
        }
        finally
        {
            try
            {
                client.close();
            }
            catch (IOException e)
            {
                LOGGER.error("Http Client Close Failed...", e);
            }
        }
        
        // 失败，返回内部错误
        BaseResponse br = new BaseResponse();
        br.setCode(CommonResultCode.SERVER_BUSY_NOW.getCode());
        br.setMsg(CommonResultCode.SERVER_BUSY_NOW.getMsg());
        
        return br.toString();
    }
}
