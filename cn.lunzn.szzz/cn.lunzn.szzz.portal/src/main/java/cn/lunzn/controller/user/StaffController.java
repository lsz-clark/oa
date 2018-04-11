package cn.lunzn.controller.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

import cn.lunzn.common.bean.BaseController;
import cn.lunzn.common.constant.ResultCodeJson;
import cn.lunzn.model.user.Employee;
import cn.lunzn.service.user.StaffService;
import cn.lunzn.util.StaffUtil;

/**
 * @author clark
 * 员工控制器
 */
@Controller
@RequestMapping(value = "/staff")
public class StaffController extends BaseController
{
    /**
     * 日志记录
     */
    private Logger logger = LoggerFactory.getLogger(StaffController.class);
    
    /**
     * 用户业务类
     */
    @Autowired
    private StaffService staffService;
    
    /**
     * 统一登录页面
     * @param request HTTP 请求体
     * @param response HTTP 响应体
     * @return String  
     */
    @RequestMapping(value = "/loginsso")
    public String loginsso(HttpServletRequest request, HttpServletResponse response)
    {
        return "redirect:/portal/common/login_oa.html?code=" + request.getParameter("code");
    }
    
    /**
     * 企业微信-网页授权登录
     * @param request HTTP 请求体
     * @param response HTTP 响应体
     * @return String  
     */
    @RequestMapping(value = "/alogin")
    public String login(HttpServletRequest request, HttpServletResponse response)
    {
        logger.debug("somenone alogin now");
        
        staffService.alogin(request);
        
        return "redirect:/portal/home.html";
    }
    
    /** 
     * 自动渲染登录员工信息
     * @param request 请求体
     * @param response 响应体
     * @return String
     * @see [类、类#方法、类#成员]
     */
    @RequestMapping(value = "/auto")
    @ResponseBody
    public String auto(HttpServletRequest request, HttpServletResponse response)
    {
        Employee em = StaffUtil.seesionStaffNoThrow(request);
        
        if (null == em)
        {
            JSONObject nullJson = new JSONObject();
            return nullJson.toString();
        }
        return em.toString();
    }
    
    /** 
     * 员工转正-根据角色查询员工
     * @param request 请求体
     * @param response 响应体
     * @param jsonData 请求参数
     * @return String
     * @see [类、类#方法、类#成员]
     */
    @RequestMapping(value = "/query")
    @ResponseBody
    public String query(HttpServletRequest request, HttpServletResponse response, @RequestBody String jsonData)
    {
        // Employee em = StaffUtil.seesionStaff(request);
        
        JSONObject json = JSONObject.parseObject(jsonData);
        
        return staffService.queryStaffByRoleId(json.getInteger("roleId")).toString();
    }
    
    /** 
     * 测试用途-登录
     * @param request 请求体
     * @param response 响应体
     * @param jsonData 请求参数
     * @return String
     * @see [类、类#方法、类#成员]
     */
    @RequestMapping(value = "/tlogin")
    @ResponseBody
    public String tlogin(HttpServletRequest request, HttpServletResponse response, @RequestBody String jsonData)
    {
        Employee em = new Employee();
        
        JSONObject json = JSONObject.parseObject(jsonData);
        
        em.setEmployeeId(json.getString("employeeId"));
        em.setName(json.getString("name"));
        em.setRoleId(json.getInteger("roleId"));
        em.setSupervisor(json.getString("supervisor"));
        em.setSupervisorName(json.getString("supervisorName"));
        em.setPosition("户部尚书");
        em.setDepartment("户部");
        
        StaffUtil.seesionLogin(request, em);
        
        return ResultCodeJson.getSuccess().toString();
    }
    
    /** 
     * 测试用途-注销
     * @param request 请求体
     * @param response 响应体
     * @return String
     * @see [类、类#方法、类#成员]
     */
    @RequestMapping(value = "/tlogout")
    @ResponseBody
    public String tlogout(HttpServletRequest request, HttpServletResponse response)
    {
        StaffUtil.seesionLoginOut(request);
        
        return ResultCodeJson.getSuccess().toString();
    }
}
