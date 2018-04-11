package cn.lunzn.controller.sz;

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
import cn.lunzn.service.sz.ReportWorkService;
import cn.lunzn.util.StaffUtil;

/**
 * @author clark
 * 
 * 记账控制器
 */
@RestController
//@Controller
@RequestMapping(value = "/report")
public class ReportWorkController extends BaseController
{
    
    /**
     * 日志记录
     */
    private Logger logger = LoggerFactory.getLogger(ReportWorkController.class);
    
    /**
     * 年度述职服务层
     */
    @Autowired
    private ReportWorkService reportWorkService;
    
    /**
     * 查询当前员工的年度述职
     * @param request HTTP 请求体
     * @param response HTTP 响应体
     * @param jsonData 请求参数
     * @return String  响应
     */
    @RequestMapping(value = "/queryme", method = RequestMethod.POST)
    public String queryMe(HttpServletRequest request, HttpServletResponse response, @RequestBody String jsonData)
    {
        logger.info("start queryme, param:{}", jsonData);
        
        // 获取当前登录用户
        Employee em = StaffUtil.seesionStaff(request);
        return reportWorkService.queryMe(em, jsonData).toString();
    }
    
    /**
     * 查询述职报告信息列表
     * @param request HTTP 请求体
     * @param response HTTP 响应体
     * @param jsonData 请求参数
     * @return String  响应
     */
    @RequestMapping(value = "/queryReportList", method = RequestMethod.POST)
    public String queryReportList(HttpServletRequest request, HttpServletResponse response,
        @RequestBody String jsonData)
    {
        logger.info("start queryReportList, param:{}", jsonData);
        
        // 获取当前登录用户
        Employee em = StaffUtil.seesionStaff(request);
        return reportWorkService.queryReportList(em, jsonData).toString();
    }
    
    /**
     * 查询述职报告信息列表
     * @param request HTTP 请求体
     * @param response HTTP 响应体
     * @param jsonData 请求参数
     * @return String  响应
     */
    @RequestMapping(value = "/queryReportInfo", method = RequestMethod.POST)
    public String queryReportInfo(HttpServletRequest request, HttpServletResponse response,
        @RequestBody String jsonData)
    {
        logger.info("start queryReportInfo, param:{}", jsonData);
        
        // 获取当前登录用户
        Employee em = StaffUtil.seesionStaff(request);
        return reportWorkService.queryReportInfo(em, jsonData).toString();
    }
    
    /**
     * 查询员工基本信息
     * @param request HTTP 请求体
     * @param response HTTP 响应体
     * @param jsonData 请求参数
     * @return String  响应
     */
    @RequestMapping(value = "/queryEmployeeInfo", method = RequestMethod.POST)
    public String queryEmployeeInfo(HttpServletRequest request, HttpServletResponse response,
        @RequestBody String jsonData)
    {
        logger.info("start queryEmployeeInfo, param:{}", jsonData);
        
        // 获取当前登录用户
        Employee em = StaffUtil.seesionStaff(request);
        return em.toString();
    }
    
    /**
     * 保存述职报告
     * @param request HTTP 请求体
     * @param response HTTP 响应体
     * @param jsonData 请求参数
     * @return String  响应
     */
    @RequestMapping(value = "/saveReportWorkInfo", method = RequestMethod.POST)
    public String saveReportWorkInfo(HttpServletRequest request, HttpServletResponse response,
        @RequestBody String jsonData)
    {
        logger.info("start saveReportWorkInfo, param:{}", jsonData);
        // 当前登录员工
        Employee sessionE = StaffUtil.seesionStaff(request);
        return reportWorkService.saveReportWorkInfo(jsonData, sessionE).toString();
    }
    
    /**
     * 保存述职报告
     * @param request HTTP 请求体
     * @param response HTTP 响应体
     * @param jsonData 请求参数
     * @return String  响应
     */
    @RequestMapping(value = "/queryRejectTime", method = RequestMethod.POST)
    public String queryRejectTime(HttpServletRequest request, HttpServletResponse response,
        @RequestBody String jsonData)
    {
        logger.info("start queryRejectTime, param:{}", jsonData);
        return reportWorkService.queryRejectTime(jsonData).toString();
    }
    
    /**
     * 审核处理，述职
     * @param request HTTP 请求体
     * @param response HTTP 响应体
     * @param jsonData 请求参数
     * @return String  
     */
    @RequestMapping(value = "/audithandler", method = RequestMethod.POST)
    public String auditHandler(HttpServletRequest request, HttpServletResponse response, @RequestBody String jsonData)
    {
        // 参数转换
        AuditFlow param = JSON.parseObject(jsonData, AuditFlow.class);
        
        // 获取当前登录员工
        Employee seesionStaff = StaffUtil.seesionStaff(request);
        
        return reportWorkService.auditHandler(seesionStaff, param).toString();
    }
    
}
