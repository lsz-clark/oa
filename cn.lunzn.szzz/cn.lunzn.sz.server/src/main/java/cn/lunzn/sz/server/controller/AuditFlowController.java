package cn.lunzn.sz.server.controller;

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
import cn.lunzn.sz.server.service.AuditFlowService;

/**
 * @author clark
 * 审核电子流控制器
 */
@RestController
@RequestMapping(value = "/auditflow")
public class AuditFlowController extends BaseController
{
    private Logger logger = LoggerFactory.getLogger(AuditFlowController.class);
    
    /**
     * 审核电子流业务类
     */
    @Autowired
    private AuditFlowService auditFlowService;
    
    /**
     * 查询审核电子流
     * @param request HTTP 请求体
     * @param response HTTP 响应体
     * @param jsonData 请求参数
     * @return String  
     */
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public String query(HttpServletRequest request, HttpServletResponse response, @RequestBody String jsonData)
    {
        logger.info("[common] query auditflow, request data:" + jsonData);
        
        AuditFlow flow = JSON.parseObject(jsonData, AuditFlow.class);
        
        return auditFlowService.find(flow).toString();
    }
    
    /**
     * 查询审核电子流-数量
     * @param request HTTP 请求体
     * @param response HTTP 响应体
     * @param jsonData 请求参数
     * @return String  
     */
    @RequestMapping(value = "/querytotal", method = RequestMethod.POST)
    public String queryTotal(HttpServletRequest request, HttpServletResponse response, @RequestBody String jsonData)
    {
        logger.info("[common] query total auditflow, request data:" + jsonData);
        
        AuditFlow flow = JSON.parseObject(jsonData, AuditFlow.class);
        
        return auditFlowService.findTotal(flow).toString();
    }
    
    /**
     * 新增审核电子流
     * @param request HTTP 请求体
     * @param response HTTP 响应体
     * @param jsonData 请求参数
     * @return String  
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String add(HttpServletRequest request, HttpServletResponse response, @RequestBody String jsonData)
    {
        logger.info("[common] add auditflow, request data:" + jsonData);
        AuditFlow flow = JSON.parseObject(jsonData, AuditFlow.class);
        
        return auditFlowService.insert(flow).toString();
    }
    
    /**
     * 更新审核电子流
     * @param request HTTP 请求体
     * @param response HTTP 响应体
     * @param jsonData 请求参数
     * @return String  
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String update(HttpServletRequest request, HttpServletResponse response, @RequestBody String jsonData)
    {
        logger.info("[common] update auditflow, request data:" + jsonData);
        AuditFlow flow = JSON.parseObject(jsonData, AuditFlow.class);
        
        return auditFlowService.update(flow).toString();
    }
    
    /**
     * 删除审核电子流
     * @param request HTTP 请求体
     * @param response HTTP 响应体
     * @param jsonData 请求参数
     * @return String  
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String delete(HttpServletRequest request, HttpServletResponse response, @RequestBody String jsonData)
    {
        logger.info("[common] delete auditflow, request data:" + jsonData);
        AuditFlow flow = JSON.parseObject(jsonData, AuditFlow.class);
        
        return auditFlowService.delete(flow).toString();
    }
}
