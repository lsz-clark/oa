package cn.lunzn.sz.server;

import java.io.InputStream;
import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import cn.lunzn.common.sercurity.DesUtils;

/**
 * @author clark
 * DataSourceAutoConfiguration 不创建自动数据源
 */
@SpringBootApplication
public class SZApplication
{
    /** 
     * 受保护的构造函数，静态类无需共有构造函数
     */
    protected SZApplication()
    {
        
    }
    
    /**
     * 
     * 述职、电子流主入口
     * @param args 参数
     * @throws Exception 异常
     */
    public static void main(String[] args)
        throws Exception
    {
        //获取加密的属性文件流
        Properties properties = new Properties();
        InputStream in = SZApplication.class.getResourceAsStream("/szapplication.properties");
        properties.load(in);
        //用自定义属性文件方式启动服务
        SpringApplication run = new SpringApplication(SZApplication.class);
        //解密配置文件中加密数据
        run.setDefaultProperties(DesUtils.processProperties(properties));
        //启动
        run.run(args);
    }
}
