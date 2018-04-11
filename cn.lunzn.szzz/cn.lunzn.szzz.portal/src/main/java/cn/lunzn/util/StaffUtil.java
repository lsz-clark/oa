package cn.lunzn.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import cn.lunzn.common.constant.Constant;
import cn.lunzn.common.enums.CommonResultCode;
import cn.lunzn.common.exception.MyRuntimeException;
import cn.lunzn.model.user.Employee;

/**
 * @author clark
 * 员工登录/注销处理类
 */
public class StaffUtil
{
    /**
     * 管理员
     */
    public static final Integer ROLE_ADMIN_1 = 1;
    
    /**
     * HR-人事
     */
    public static final Integer ROLE_HR_2 = 2;
    
    /** 
     * 受保护的构造函数，静态类无需共有构造函数
     */
    protected StaffUtil()
    {
        
    }
    
    /**
     * 登录
     * @param request HTTP请求
     * @param em 员工
     */
    public static void seesionLogin(HttpServletRequest request, Employee em)
    {
        HttpSession httpSession = request.getSession(true);
        
        httpSession.setAttribute(Constant.SESSION_STAFF_KEY, em);
    }
    
    /**
     * 获取会话员工，如果员工未登录则会抛出异常
     * @param request HTTP请求
     * 
     * @return Employee
     */
    public static Employee seesionStaff(HttpServletRequest request)
    {
        
        HttpSession httpSession = request.getSession();
        if (null != httpSession && null != httpSession.getAttribute(Constant.SESSION_STAFF_KEY))
        {
            return (Employee)httpSession.getAttribute(Constant.SESSION_STAFF_KEY);
        }
        
        // 抛出请登录运行异常
        throw new MyRuntimeException(CommonResultCode.SERVICE_BUSINESS_6011000.getCode(),
            CommonResultCode.SERVICE_BUSINESS_6011000.getMsg());
    }
    
    /**
     * 获取会话员工，不抛异常
     * @param request HTTP请求
     * 
     * @return Employee
     */
    public static Employee seesionStaffNoThrow(HttpServletRequest request)
    {
        HttpSession httpSession = request.getSession();
        if (null != httpSession && null != httpSession.getAttribute(Constant.SESSION_STAFF_KEY))
        {
            return (Employee)httpSession.getAttribute(Constant.SESSION_STAFF_KEY);
        }
        return null;
    }
    
    /**
     * 注销
     * @param request HTTP请求
     */
    public static void seesionLoginOut(HttpServletRequest request)
    {
        HttpSession httpSession = request.getSession();
        if (null != httpSession)
        {
            httpSession.removeAttribute(Constant.SESSION_STAFF_KEY);
        }
    }
}
