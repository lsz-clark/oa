package cn.lunzn.service.user;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.lunzn.common.bean.BaseResponse;
import cn.lunzn.common.cache.PropertyCache;
import cn.lunzn.common.enums.CommonResultCode;
import cn.lunzn.common.http.JxxtHttpClient;
import cn.lunzn.common.http.QywxHttpClient;
import cn.lunzn.common.response.ListResponse;
import cn.lunzn.model.user.Employee;
import cn.lunzn.util.StaffUtil;

/**
 * 员工业务类
 * 
 * @author  clark
 * @version  [版本号, 2017年8月23日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Service
public class StaffService
{
    /**
     * 日志记录
     */
    private Logger logger = LoggerFactory.getLogger(StaffService.class);
    
    /**
     * 企业微信-网页授权
     * @param request HTTP请求体
     */
    public void alogin(HttpServletRequest request)
    {
        // 获取code
        String code = request.getParameter("code");
        
        // 获取用户id
        StringBuffer uri = new StringBuffer("https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo");
        uri.append("?access_token=").append(QywxHttpClient.getAccessTokenCache());
        uri.append("&code=").append(code);
        String userIdStr = QywxHttpClient.get(uri.toString());
        JSONObject userIdJson = JSONObject.parseObject(userIdStr);
        
        if (userIdJson.getIntValue("errcode") == 0)
        {
            String userId = userIdJson.getString("UserId");
            
            // 获取用户信息
            uri.delete(0, uri.length());
            uri.append("https://qyapi.weixin.qq.com/cgi-bin/user/get");
            uri.append("?access_token=").append(QywxHttpClient.getAccessTokenCache());
            uri.append("&userid=").append(userId);
            String userInfoStr = QywxHttpClient.get(uri.toString());
            JSONObject userInfoJson = JSONObject.parseObject(userInfoStr);
            
            if (userInfoJson.getIntValue("errcode") == 0)
            {
                // 将企业微信成员转换成绩效系统员工
                Employee em = convertToEmployee(userInfoJson);
                
                // 调用绩效系统，新增员工（如果已经添加，则不会添加）
                addStaff(em);
                
                // 调用绩效系统，查询当前员工信息
                ListResponse<Employee> list = queryStaff(userId, null, null);
                if (list.isSuccess() && !CollectionUtils.isEmpty(list.getResult()))
                {
                    // 放入会话中，完成登录
                    StaffUtil.seesionLogin(request, list.getResult().get(0));
                }
            }
        }
    }
    
    /** 
     * 查询员工-不分页
     * @param roleId 角色编号
     * @return ListResponse<Employee>
     * @see [类、类#方法、类#成员]
     */
    public ListResponse<Employee> queryStaffByRoleId(Integer roleId)
    {
        ListResponse<Employee> list = queryStaff(null, null, roleId);
        return list;
    }
    
    /** 
     * 查询员工-不分页
     * @param staffId 员工编号
     * @param staffName 员工姓名
     * @param roleId 角色编号
     * @return ListResponse<Employee>
     * @see [类、类#方法、类#成员]
     */
    public ListResponse<Employee> queryStaff(String staffId, String staffName, Integer roleId)
    {
        // 设置参数
        Employee param = new Employee();
        param.setEmployeeId(staffId);
        param.setName(staffName);
        param.setRoleId(roleId);
        
        // 接口日志
        logger.info("[JXXT] Query Staff, Staff info {}", param.toString());
        
        // 绩效系统地址
        String jxUri = PropertyCache.getProp().getString("jxxt.server")
            + PropertyCache.getProp().getString("jxxt.server.staff.query");
        
        // 请求绩效系统
        String result = JxxtHttpClient.post(jxUri, JSONObject.parseObject(param.toString()));
        JSONObject resultJson = JSONObject.parseObject(result);
        
        ListResponse<Employee> staffs = new ListResponse<Employee>();
        if (resultJson.getIntValue("code") == 0)
        {
            if (!StringUtils.isEmpty(resultJson.getString("rows")))
            {
                List<Employee> list = new ArrayList<Employee>();
                JSONArray staffJsonArray = JSONArray.parseArray(resultJson.getString("rows"));
                for (Object objJson : staffJsonArray)
                {
                    list.add(JSONObject.toJavaObject(JSONObject.parseObject(objJson.toString()), Employee.class));
                }
                
                staffs.setResult(list);
                staffs.setTotal(list.size());
            }
        }
        else
        {
            staffs.setCode(CommonResultCode.SERVICE_BUSINESS_6013001.getCode());
            staffs.setMsg(CommonResultCode.SERVICE_BUSINESS_6013001.getMsg());
        }
        
        return staffs;
    }
    
    /** 
     * 调用绩效系统，新增员工（如果已经添加，则不会添加）
     * @param staff 员工信息
     * @return BaseResponse
     * @see [类、类#方法、类#成员]
     */
    private BaseResponse addStaff(Employee staff)
    {
        logger.info("[JXXT] Add Staff, Staff info {}", staff.toString());
        
        String jxUri = PropertyCache.getProp().getString("jxxt.server")
            + PropertyCache.getProp().getString("jxxt.server.staff.add");
        
        String result = JxxtHttpClient.post(jxUri, JSONObject.parseObject(staff.toString()));
        
        JSONObject resultJson = JSONObject.parseObject(result);
        if (resultJson.getIntValue("code") != 0)
        {
            BaseResponse br = new BaseResponse();
            br.setCode(CommonResultCode.SERVICE_BUSINESS_6013001.getCode());
            br.setMsg(CommonResultCode.SERVICE_BUSINESS_6013001.getMsg());
            
            return br;
        }
        return new BaseResponse();
    }
    
    /** 
     * 将企业微信成员转换成绩效系统员工
     * @param userInfoJson 企业微信用户信息
     * @return Employee
     * @see [类、类#方法、类#成员]
     */
    private Employee convertToEmployee(JSONObject userInfoJson)
    {
        Employee emee = new Employee();
        emee.setEmployeeId(userInfoJson.getString("userid"));
        emee.setName(userInfoJson.getString("name"));
        
        emee.setEmail(userInfoJson.getString("email"));
        emee.setMobiles(userInfoJson.getString("mobile"));
        emee.setTelephone(userInfoJson.getString("telephone"));
        emee.setGender(userInfoJson.getInteger("gender"));
        emee.setEnName(userInfoJson.getString("english_name"));
        
        // 根据部门id获取部门名称
        if (!StringUtils.isEmpty(userInfoJson.getString("department")))
        {
            JSONArray department = userInfoJson.getJSONArray("department");
            if (null != department && department.size() > 0)
            {
                StringBuffer uri = new StringBuffer();
                uri.append("https://qyapi.weixin.qq.com/cgi-bin/department/list");
                uri.append("?access_token=").append(QywxHttpClient.getAccessTokenCache());
                uri.append("&id=").append(department.get(department.size() - 1));
                String departmentInfoStr = QywxHttpClient.get(uri.toString());
                JSONObject departmentInfo = JSONObject.parseObject(departmentInfoStr);
                if (departmentInfo.getIntValue("errcode") == 0)
                {
                    JSONArray infos = departmentInfo.getJSONArray("department");
                    if (null != infos && infos.size() > 0)
                    {
                        JSONObject info = infos.getJSONObject(0);
                        emee.setDepartment(info.getString("name"));
                    }
                }
            }
        }
        
        emee.setPosition(userInfoJson.getString("position"));
        
        emee.setAvatar(userInfoJson.getString("avatar"));
        return emee;
    }
}
