package cn.lunzn.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import cn.lunzn.common.cache.PropertyCache;
import cn.lunzn.common.constant.Constant;
import cn.lunzn.common.http.QywxHttpClient;

/**
 * 定时任务
 * 
 * @author  clark
 * @version  [版本号, 2017年8月21日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Service
public class SZZZScheduled
{
    /**
     * 日志记录
     */
    private Logger logger = LoggerFactory.getLogger(SZZZScheduled.class);
    
    /** 
     * 每小时刷新一次
     * @see [类、类#方法、类#成员]
     */
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void refreshAccessToken()
    {
        logger.info("begin refresh access_token...");
        
        // 刷新授权码
        String newAccessToken =
            QywxHttpClient.getAccessToken(PropertyCache.getProp().getString(Constant.QYWX_CORPID_KEY),
                PropertyCache.getProp().getString(Constant.QYWX_CORPSECRET_KEY));
        QywxHttpClient.refreshAccessTokenCache(newAccessToken);
        
        logger.info("end refresh access_token...");
    }
}
