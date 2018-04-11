package cn.lunzn.config;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.lunzn.common.cache.PropertyCache;
import cn.lunzn.common.constant.Constant;
import cn.lunzn.common.http.QywxHttpClient;

/**
 * 启动加载监听器
 * 
 * @author  clark
 * @version  [版本号, 2017年9月11日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@WebListener
public class PortalStartupListener implements ServletContextListener
{
    /**
     * 日志记录
     */
    private Logger logger = LoggerFactory.getLogger(PortalStartupListener.class);
    
    @Override
    public void contextInitialized(ServletContextEvent arg0)
    {
        logger.info("hi-hello");
        
        // 加载配置文件
        PropertyCache.initPoperty();
        
        // 手动刷新一次授权码
        String newAccessToken =
            QywxHttpClient.getAccessToken(PropertyCache.getProp().getString(Constant.QYWX_CORPID_KEY),
                PropertyCache.getProp().getString(Constant.QYWX_CORPSECRET_KEY));
        QywxHttpClient.refreshAccessTokenCache(newAccessToken);
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent arg0)
    {
        logger.debug("bye-bye");
    }
    
}
