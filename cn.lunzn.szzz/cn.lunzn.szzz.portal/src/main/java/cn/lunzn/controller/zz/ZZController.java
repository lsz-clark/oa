package cn.lunzn.controller.zz;

import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.lunzn.common.bean.BaseController;
import cn.lunzn.common.bean.BaseResponse;
import cn.lunzn.common.date.DateUtil;
import cn.lunzn.common.enums.StateEnum;
import cn.lunzn.model.common.AuditFlow;
import cn.lunzn.model.user.Employee;
import cn.lunzn.model.zz.ZZ;
import cn.lunzn.service.zz.ZZService;
import cn.lunzn.util.StaffUtil;

/**
 * @author 雷懿
 * 员工转正控制器
 */
@RestController
@RequestMapping(value = "/zz")
public class ZZController extends BaseController
{
    /**
     * 员工转正业务类
     */
    @Autowired
    private ZZService zzService;
    
    /**
     * 查询当前员工的转正申请
     * @param request HTTP请求体
     * @param response HTTP响应体
     * @param jsonData 请求参数
     * @return String
     */
    @RequestMapping(value = "/queryme", method = RequestMethod.POST)
    public String queryMe(HttpServletRequest request, HttpServletResponse response, @RequestBody String jsonData)
    {
        // 查询条件
        ZZ zz = JSON.parseObject(jsonData, ZZ.class);
        
        // 登录员工
        Employee sessionE = StaffUtil.seesionStaff(request);
        
        // 查询结果
        return zzService.queryMe(zz, sessionE).toString();
    }
    
    /**
     * 查询员工转正记录
     * @param request HTTP请求体
     * @param response HTTP响应体
     * @param jsonData 请求参数
     * @return String
     */
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public String query(HttpServletRequest request, HttpServletResponse response, @RequestBody String jsonData)
    {
        // 查询条件
        ZZ zz = JSON.parseObject(jsonData, ZZ.class);
        
        // 登录员工
        Employee sessionE = StaffUtil.seesionStaff(request);
        
        // 查询结果
        return zzService.query(zz, sessionE).toString();
    }
    
    /**
     * 员工转正申请查询
     * @param request HTTP请求体
     * @param response HTTP响应体
     * @return String
     */
    @RequestMapping(value = "/applyquery", method = RequestMethod.POST)
    public String addQuery(HttpServletRequest request, HttpServletResponse response)
    {
        // 当前登录员工
        Employee sessionE = StaffUtil.seesionStaff(request);
        JSONObject employeeJson = JSONObject.parseObject(sessionE.toString());
        
        BaseResponse br = new BaseResponse();
        JSONObject result = JSONObject.parseObject(br.toString());
        
        // 填表日期
        employeeJson.put("formDate", DateUtil.formatDateToString(Calendar.getInstance().getTime()));
        result.put("data", employeeJson);
        
        // 查询结果
        return result.toString();
    }
    
    /**
     * 转正申请 重复检查
     * @param request HTTP请求体
     * @param response HTTP响应体
     * @return String
     */
    @RequestMapping(value = "/applycheck", method = RequestMethod.POST)
    public String appycheck(HttpServletRequest request, HttpServletResponse response)
    {
        // 当前登录员工
        Employee sessionE = StaffUtil.seesionStaff(request);
        
        return zzService.appyCheck(sessionE).toString();
    }
    
    /**
     * 保存草稿员工转正申请
     * @param request HTTP请求体
     * @param response HTTP响应体
     * @param jsonData 请求参数
     * @return String
     */
    @RequestMapping(value = "/savedraft", method = RequestMethod.POST)
    public String saveDraft(HttpServletRequest request, HttpServletResponse response, @RequestBody String jsonData)
    {
        // 转正申请对象
        ZZ zz = JSON.parseObject(jsonData, ZZ.class);
        
        // 编辑中
        zz.setState(StateEnum.AUDIT_STATE_EDITING_1.getId());
        
        // 当前登录员工
        Employee sessionE = StaffUtil.seesionStaff(request);
        
        return zzService.addOrUpdate(zz, sessionE).toString();
    }
    
    /**
     * 保存并提交
     * @param request HTTP请求体
     * @param response HTTP响应体
     * @param jsonData 请求参数
     * @return String
     */
    @RequestMapping(value = "/saveandsubmit", method = RequestMethod.POST)
    public String saveAndSubmit(HttpServletRequest request, HttpServletResponse response, @RequestBody String jsonData)
    {
        // 转正申请对象
        ZZ zz = JSON.parseObject(jsonData, ZZ.class);
        
        // 审核中
        zz.setState(StateEnum.AUDIT_STATE_AUDITING_2.getId());
        
        // 当前登录员工
        Employee sessionE = StaffUtil.seesionStaff(request);
        
        return zzService.saveAndSbumit(zz, sessionE).toString();
    }
    
    /**
     * 编辑-查询员工以前填写信息
     * @param request HTTP请求体
     * @param response HTTP响应体
     * @param jsonData 请求参数
     * @return String
     */
    @RequestMapping(value = "/editquery", method = RequestMethod.POST)
    public String editQuery(HttpServletRequest request, HttpServletResponse response, @RequestBody String jsonData)
    {
        // 转正申请对象
        ZZ zz = JSON.parseObject(jsonData, ZZ.class);
        
        // 当前登录员工
        Employee sessionE = StaffUtil.seesionStaff(request);
        
        // 查询填写新
        BaseResponse bResponse = zzService.editQuery(zz, sessionE, true);
        
        // 准备返回结果
        // JSONObject result = JSONObject.parseObject(bResponse.toString());
        // result.put("staffInfo", sessionE);
        
        return bResponse.toString();
    }
    
    /**
     * 查看-查询员工以前填写信息
     * @param request HTTP请求体
     * @param response HTTP响应体
     * @param jsonData 请求参数
     * @return String
     */
    @RequestMapping(value = "/viewquery", method = RequestMethod.POST)
    public String viewQuery(HttpServletRequest request, HttpServletResponse response, @RequestBody String jsonData)
    {
        // 转正申请对象
        ZZ zz = JSON.parseObject(jsonData, ZZ.class);
        
        // 当前登录员工
        Employee sessionE = StaffUtil.seesionStaff(request);
        
        // 查询填写新
        BaseResponse bResponse = zzService.editQuery(zz, sessionE, false);
        
        // 准备返回结果
        // JSONObject result = JSONObject.parseObject(bResponse.toString());
        // result.put("staffInfo", sessionE);
        
        return bResponse.toString();
    }
    
    /**
     * 审核处理，转正、述职
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
        
        return zzService.auditHandler(seesionStaff, param).toString();
    }
}
