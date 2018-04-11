package cn.lunzn.common.bean;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lunzn.common.constant.ResultCodeJson;
import cn.lunzn.common.exception.MyException;
import cn.lunzn.common.exception.MyRuntimeException;

/**
 * 所有控制器异常处理
 * @author clark
 */
public class BaseController
{
    /**
     * 日志记录
     */
    private Logger logger = LoggerFactory.getLogger(BaseController.class);
    
    /**
     * 捕获所有异常，并归纳异常信息
     * @param request HTTP请求体
     * @param e 异常
     * @return String
     */
    @ResponseBody
    @ExceptionHandler
    public String commonException(HttpServletRequest request, Exception e)
    {
        logger.error("Oops!", e);
        
        // 违反数据库唯一约束异常
        if (e instanceof DuplicateKeyException)
        {
            return ResultCodeJson.get2().toString();
        }
        
        if (e instanceof MyRuntimeException)
        {
            MyRuntimeException mre = (MyRuntimeException)e;
            return mre.getJSONObject().toString();
        }
        
        if (e instanceof MyException)
        {
            MyException mre = (MyException)e;
            return mre.getJSONObject().toString();
        }
        
        return ResultCodeJson.get500().toString();
    }
}
