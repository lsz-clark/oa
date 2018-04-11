package cn.lunzn.controller.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;

import cn.lunzn.common.bean.BaseController;
import cn.lunzn.model.common.AuditFlow;
import cn.lunzn.model.user.Employee;
import cn.lunzn.service.common.AuditFlowService;
import cn.lunzn.util.StaffUtil;

/**
 * @author clark
 * 
 * 审核电子流控制器
 */
@RestController
@RequestMapping(value = "/auditflow")
public class AuditFlowController extends BaseController
{
    /**
     * 日志记录
     */
    private Logger logger = LoggerFactory.getLogger(AuditFlowController.class);
    
    /**
     * 审核电子流业务类
     */
    @Autowired
    private AuditFlowService auditFlowService;
    
    /**
     * 查询电子流
     * @param request HTTP 请求体
     * @param response HTTP 响应体
     * @param jsonData 请求参数
     * @return String  
     */
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public String query(HttpServletRequest request, HttpServletResponse response, @RequestBody String jsonData)
    {
        logger.info("query auditflow, param:{}", jsonData);
        
        // 参数转换
        AuditFlow param = JSON.parseObject(jsonData, AuditFlow.class);
        
        // 获取当前登录员工
        Employee seesionStaff = StaffUtil.seesionStaff(request);
        return auditFlowService.query(seesionStaff, param).toString();
    }
    
    /**
     * 查询电子流
     * @param request HTTP 请求体
     * @param response HTTP 响应体
     * @param jsonData 请求参数
     * @return String  
     */
    @RequestMapping(value = "/queryall", method = RequestMethod.POST)
    public String queryAll(HttpServletRequest request, HttpServletResponse response, @RequestBody String jsonData)
    {
        logger.info("query all auditflow, param:{}", jsonData);
        
        // 参数转换
        AuditFlow param = JSON.parseObject(jsonData, AuditFlow.class);
        
        return auditFlowService.query(param).toString();
    }
    
    /**
     * 查询电子流数量
     * @param request HTTP 请求体
     * @param response HTTP 响应体
     * @param jsonData 请求参数
     * @return String  
     */
    @RequestMapping(value = "/querytotal", method = RequestMethod.POST)
    public String queryTotal(HttpServletRequest request, HttpServletResponse response, @RequestBody String jsonData)
    {
        logger.info("query total auditflow, param:{}", jsonData);
        
        // 参数转换
        AuditFlow param = JSON.parseObject(jsonData, AuditFlow.class);
        
        return auditFlowService.queryTotal(param).toString();
    }
    
    /**
     * 撤销审核
     * @param request HTTP 请求体
     * @param response HTTP 响应体
     * @param jsonData 请求参数
     * @return String  
     */
    @RequestMapping(value = "/undo", method = RequestMethod.POST)
    public String undo(HttpServletRequest request, HttpServletResponse response, @RequestBody String jsonData)
    {
        logger.info("undo auditflow, param:{}", jsonData);
        
        // 参数转换
        AuditFlow param = JSON.parseObject(jsonData, AuditFlow.class);
        Employee seesionStaff = StaffUtil.seesionStaff(request);
        return auditFlowService.undo(param, seesionStaff).toString();
    }
}
