package cn.lunzn.service.zz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.lunzn.common.bean.BaseResponse;
import cn.lunzn.common.bean.PaginationBean;
import cn.lunzn.common.constant.DefaultURI;
import cn.lunzn.common.constant.ResultCodeJson;
import cn.lunzn.common.enums.AuditEnum;
import cn.lunzn.common.enums.CommonResultCode;
import cn.lunzn.common.enums.StateEnum;
import cn.lunzn.common.http.QywxHttpClient;
import cn.lunzn.common.http.ServiceClient;
import cn.lunzn.common.response.ListResponse;
import cn.lunzn.model.common.AuditFlow;
import cn.lunzn.model.user.Employee;
import cn.lunzn.model.zz.ZZ;
import cn.lunzn.service.common.AuditFlowService;
import cn.lunzn.util.StaffUtil;

/**
 * 员工转正业务类
 * 
 * @author  clark
 * @version  [版本号, 2017年8月30日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Service
public class ZZService
{
    /**
     * 审核电子流业务类
     */
    @Autowired
    private AuditFlowService auditFlowService;
    
    /** 
     * 查询当前员工的转正申请
     * @param param 查询条件
     * @param sessionE 当前登录的员工
     * @return ListResponse<ZZ>
     * @see [类、类#方法、类#成员]
     */
    public ListResponse<ZZ> queryMe(ZZ param, Employee sessionE)
    {
        // 设置当前员工id，只能查看自己的转正记录
        param.setStaffId(sessionE.getEmployeeId().toString());
        
        // 设置分页
        param.setPage(PaginationBean.getPagination(param.getPage()));
        
        // 调用转正原子服务，获取数据
        ListResponse<ZZ> list =
            ServiceClient.callList(param, DefaultURI.getZZURIKey(DefaultURI.URI_QUERY), ZZ.getTypeReference());
        
        return list;
    }
    
    /** 
     * 查询员工转正记录
     * @param param 查询条件
     * @param sessionE 当前登录的员工
     * @return ListResponse<ZZ>
     * @see [类、类#方法、类#成员]
     */
    public ListResponse<ZZ> query(ZZ param, Employee sessionE)
    {
        // 人事 & 管理员角色员工可以查询所有非编辑中的记录（但包括自己编辑中的记录）,普通员工只能查看自身相关的转正记录
        if (StaffUtil.ROLE_ADMIN_1.equals(sessionE.getRoleId()) || StaffUtil.ROLE_HR_2.equals(sessionE.getRoleId()))
        {
            param.setAdmin(true);
        }
        // 设置当前员工id，只能查看自己的转正记录
        param.setStaffId(sessionE.getEmployeeId().toString());
        
        // 设置分页
        param.setPage(PaginationBean.getPagination(param.getPage()));
        
        // 调用转正原子服务，获取数据
        ListResponse<ZZ> list =
            ServiceClient.callList(param, DefaultURI.getZZURIKey(DefaultURI.URI_QUERY), ZZ.getTypeReference());
        
        return list;
    }
    
    /** 
     * 保存或更新
     * @param param 转正申请对象
     * @param sessionE 当前登录员工
     * @return BaseResponse
     * @see [类、类#方法、类#成员]
     */
    public BaseResponse addOrUpdate(ZZ param, Employee sessionE)
    {
        // 设置当前员工id
        param.setStaffId(sessionE.getEmployeeId());
        
        BaseResponse response = null;
        
        // 根据员工编号查询员工的申请信息
        ZZ queryParam = new ZZ();
        queryParam.setStaffId(sessionE.getEmployeeId());
        // 调用转正原子服务，获取数据
        ListResponse<ZZ> list =
            ServiceClient.callList(queryParam, DefaultURI.getZZURIKey(DefaultURI.URI_QUERY), ZZ.getTypeReference());
        
        // 查询失败，返回错误
        if (!list.isSuccess())
        {
            return list;
        }
        
        // [1] 不存在ID则新增
        if (null == param.getZzId())
        {
            // 新增之前先校验是否存在重复申请
            if (list.getTotal() > 0)
            {
                // 错误码 2 重复记录
                return ResultCodeJson.getBr2();
            }
            // 添加转正申请
            response = ServiceClient.call(param, DefaultURI.getZZURIKey(DefaultURI.URI_ADD), BaseResponse.class);
        }
        else
        {
            // [2] 存在ID则更新
            if (list.getTotal() == 0 || null == list.getResult())
            {
                // 错误码601 业务异常
                return ResultCodeJson.getBr601();
            }
            
            // 获取以前转正申请记录
            ZZ dbZZ = list.getResult().get(0);
            
            // 判断状态, 审核中2与审核通过3的数据不允许修改
            if (StateEnum.AUDIT_STATE_AUDITING_2.getId() == dbZZ.getState().intValue()
                || StateEnum.AUDIT_STATE_APPROVE_3.getId() == dbZZ.getState().intValue())
            {
                // 错误码4 数据状态不正确
                return ResultCodeJson.getBr3();
            }
            
            // 更新转正申请
            response = ServiceClient.call(param, DefaultURI.getZZURIKey(DefaultURI.URI_UPDATE), BaseResponse.class);
        }
        
        return response;
    }
    
    /** 
     * 保存或更新，员工提交
     * @param param 转正申请对象
     * @param sessionE 当前登录员工
     * @return BaseResponse
     * @see [类、类#方法、类#成员]
     */
    public BaseResponse saveAndSbumit(ZZ param, Employee sessionE)
    {
        if (StringUtils.isEmpty(param.getAuditor()))
        {
            // shenren为空，返回失败
            BaseResponse error = new BaseResponse();
            error.setCode(CommonResultCode.SERVICE_BUSINESS_6014001.getCode());
            return error;
        }
        
        // 保存转正申请信息
        BaseResponse updateResponse = addOrUpdate(param, sessionE);
        // 更新失败返回
        if (!updateResponse.isSuccess())
        {
            return updateResponse;
        }
        
        // 获取当前转正申请
        ZZ query = new ZZ();
        query.setStaffId(sessionE.getEmployeeId());
        // 调用转正原子服务，获取数据
        ListResponse<ZZ> list =
            ServiceClient.callList(query, DefaultURI.getZZURIKey(DefaultURI.URI_QUERY), ZZ.getTypeReference());
        // 查询失败，返回错误
        if (!list.isSuccess())
        {
            return list;
        }
        
        // 记录审核电子流
        ZZ curZZ = list.getResult().get(0);
        
        // 获取第一个审核人
        JSONArray auditors = JSONArray.parseArray(curZZ.getAuditor());
        JSONObject firstAuditor = auditors.getJSONObject(0);
        
        AuditFlow flow = new AuditFlow();
        // 审核人信息
        flow.setStaffId(firstAuditor.getString("staffId"));
        flow.setStaffName(firstAuditor.getString("staffName"));
        // 提交人信息
        flow.setSubmitStaffId(sessionE.getEmployeeId());
        flow.setSubmitStaffName(sessionE.getName());
        // 业务ID
        flow.setAuditId(curZZ.getZzId());
        // 审核类型-转正申请
        flow.setAuditType(AuditEnum.ZZ_TYPE_2.getId());
        // 审核步骤 1
        flow.setAuditStep(AuditEnum.AUDIT_STEP_DEPT_1.getId());
        
        BaseResponse auditResponse = auditFlowService.addAuditFlow(flow);
        
        // 提交审核失败，将数据改成编辑中
        if (!auditResponse.isSuccess())
        {
            // 编辑中
            curZZ.setState(StateEnum.AUDIT_STATE_EDITING_1.getId());
            ServiceClient.call(curZZ, DefaultURI.getZZURIKey(DefaultURI.URI_UPDATE), BaseResponse.class);
        }
        
        // 发送企业微信消息给审核人
        QywxHttpClient.sendMsg(firstAuditor.getString("staffId"), curZZ.getStaffName() + "提交了转正申请，请审批");
        // 发送企业微信消息给抄送人
        String copyAditors = param.getCc();
        if (!StringUtils.isEmpty(copyAditors))
        {
            // 需要抄送的
            String sendee = "";
            JSONArray ccs = JSON.parseArray(copyAditors);
            if (ccs.size() > 0)
            {
                for (int i = 0; i < ccs.size(); i++)
                {
                    JSONObject cc = ccs.getJSONObject(i);
                    sendee += cc.getString("staffId") + "|";
                }
            }
            // 去掉最后一个"|"
            sendee = sendee.substring(0, sendee.length() - 1);
            
            // 发送给多个106|105|123
            QywxHttpClient.sendMsg(sendee, curZZ.getStaffName() + "提交了转正申请");
        }
        
        return auditResponse;
    }
    
    /**
     * 审核处理，转正
     * @param sessionStaff 当前登录的用户
     * @param flow 参数
     * @return JSONObject
     */
    public BaseResponse auditHandler(Employee sessionStaff, AuditFlow flow)
    {
        // 查询转正信息
        ZZ query = new ZZ();
        query.setZzId(flow.getAuditId());
        // 调用转正原子服务，获取数据
        ListResponse<ZZ> list =
            ServiceClient.callList(query, DefaultURI.getZZURIKey(DefaultURI.URI_QUERY), ZZ.getTypeReference());
        // 查询失败，返回错误
        if (!list.isSuccess())
        {
            return list;
        }
        
        BaseResponse updateResponse = new BaseResponse();
        
        // 获取员工转正信息
        ZZ curZZ = list.getResult().get(0);
        
        // 审核处理
        // 根据flowId查询当前电子流信息，已处理的电子流无法再次处理
        AuditFlow queryParam = new AuditFlow();
        queryParam.setFlowId(flow.getFlowId());
        ListResponse<AuditFlow> flowList = ServiceClient.callList(queryParam,
            DefaultURI.getAuditFlowURIKey(DefaultURI.URI_QUERY),
            AuditFlow.getTypeReference());
        
        if (!flowList.isSuccess() || CollectionUtils.isEmpty(flowList.getResult()))
        {
            return ResultCodeJson.getBr2();
        }
        
        AuditFlow queryFlow = flowList.getResult().get(0);
        BaseResponse auditResponse = auditFlowService.auditHandler(flow, queryFlow, curZZ.getAuditor());
        if (!auditResponse.isSuccess())
        {
            // 审核失败返回
            return auditResponse;
        }
        
        // 通过
        if (flow.getState().intValue() == StateEnum.FLOW_STATE_PASS_1.getId())
        {
            // 如果当前审核步骤等于审核人总个数，那么不再生成电子流
            JSONArray auditorArray = JSONArray.parseArray(curZZ.getAuditor());
            if (queryFlow.getAuditStep() >= auditorArray.size())
            {
                // 通过状态
                curZZ.setState(StateEnum.AUDIT_STATE_APPROVE_3.getId());
                updateResponse =
                    ServiceClient.call(curZZ, DefaultURI.getZZURIKey(DefaultURI.URI_UPDATE), BaseResponse.class);
                
                // 发送消息
                QywxHttpClient.sendMsg(curZZ.getStaffId(), "您的转正申请已通过");
                
                // 发送消息给抄送人
                if (!StringUtils.isEmpty(curZZ.getCc()))
                {
                    JSONArray ccs = JSONArray.parseArray(curZZ.getCc());
                    QywxHttpClient.sendMsg(getCcStaffIds(ccs), curZZ.getStaffName() + "的年度述职申请已通过");
                }
            }
        }
        else
        {
            // 驳回状态
            curZZ.setState(StateEnum.AUDIT_STATE_REJECT_4.getId());
            updateResponse =
                ServiceClient.call(curZZ, DefaultURI.getZZURIKey(DefaultURI.URI_UPDATE), BaseResponse.class);
            
            // 发送消息
            QywxHttpClient.sendMsg(curZZ.getStaffId(), "您的转正申请已驳回");
            
            // 发送消息给抄送人
            if (!StringUtils.isEmpty(curZZ.getCc()))
            {
                JSONArray ccs = JSONArray.parseArray(curZZ.getCc());
                QywxHttpClient.sendMsg(getCcStaffIds(ccs), curZZ.getStaffName() + "的年度述职申请已驳回");
            }
        }
        
        return updateResponse;
    }
    
    /** 
     * 编辑-查询员工以前填写信息
     * @param param 查询条件
     * @param sessionE 当前登录员工
     * @param isCheck 是否检查状态
     * @return BaseResponse
     * @see [类、类#方法、类#成员]
     */
    public BaseResponse editQuery(ZZ param, Employee sessionE, boolean isCheck)
    {
        // 根据员工编号查询员工的申请信息
        // param.setStaffId(sessionE.getEmployeeId());
        
        // 调用转正原子服务，获取数据
        ListResponse<ZZ> list =
            ServiceClient.callList(param, DefaultURI.getZZURIKey(DefaultURI.URI_QUERY), ZZ.getTypeReference());
        
        // 数据状态不正确，不能继续编辑
        if (isCheck && list.isSuccess()
            && list.getResult().get(0).getState().intValue() == StateEnum.AUDIT_STATE_AUDITING_2.getId())
        {
            return ResultCodeJson.getBr3();
        }
        
        return list;
    }
    
    /** 
     * 转正申请重复提交检查
     * @param sessionE 当前登录员工
     * @return BaseResponse
     * @see [类、类#方法、类#成员]
     */
    public BaseResponse appyCheck(Employee sessionE)
    {
        // 根据员工编号查询员工的申请信息
        ZZ queryParam = new ZZ();
        queryParam.setStaffId(sessionE.getEmployeeId());
        // 调用转正原子服务，获取数据
        ListResponse<ZZ> list =
            ServiceClient.callList(queryParam, DefaultURI.getZZURIKey(DefaultURI.URI_QUERY), ZZ.getTypeReference());
        
        // 查询失败，返回错误
        if (!list.isSuccess())
        {
            return list;
        }
        
        // 新增之前先校验是否存在重复申请
        if (list.getTotal() > 0)
        {
            // 错误码 2 重复记录
            return ResultCodeJson.getBr2();
        }
        
        // 返回成功
        return new BaseResponse();
    }
    
    /** 
     * 查询转正申请归档信息
     * @param zzId 转正业务ID
     * @return JSONObject info 业务信息,auditInfo 评定信息,rejectCount 驳回次数
     * @see [类、类#方法、类#成员]
     */
    public JSONObject queryArchiveInfo(Integer zzId)
    {
        // 一、获取转正申请信息
        ZZ zzParam = new ZZ();
        zzParam.setZzId(zzId);
        
        // 调用转正原子服务
        ListResponse<ZZ> infoList =
            ServiceClient.callList(zzParam, DefaultURI.getZZURIKey(DefaultURI.URI_QUERY), ZZ.getTypeReference());
        // 查询失败，返回错误
        if (!infoList.isSuccess())
        {
            return JSONObject.parseObject(infoList.toString());
        }
        
        // 二、获取评定信息
        AuditFlow flowParam = new AuditFlow();
        flowParam.setAuditId(zzId);
        
        // 调用电子流原子服务
        ListResponse<AuditFlow> flowList = ServiceClient.callList(flowParam,
            DefaultURI.getAuditFlowURIKey(DefaultURI.URI_QUERY),
            AuditFlow.getTypeReference());
        // 查询失败，返回错误
        if (!flowList.isSuccess())
        {
            return JSONObject.parseObject(flowList.toString());
        }
        
        // 三、获取驳回次数
        flowParam = new AuditFlow();
        flowParam.setAuditId(zzId);
        // 驳回状态
        flowParam.setState(StateEnum.FLOW_STATE_REJECT_2.getId());
        
        // 调用电子流原子服务
        ListResponse<AuditFlow> rejectList = ServiceClient.callList(flowParam,
            DefaultURI.getAuditFlowURIKey(DefaultURI.URI_QUERY),
            AuditFlow.getTypeReference());
        // 查询失败，返回错误
        if (!rejectList.isSuccess())
        {
            return JSONObject.parseObject(rejectList.toString());
        }
        
        // 四、返回结果
        JSONObject result = ResultCodeJson.getSuccess();
        // 业务信息
        result.put("info", infoList.getResult().get(0));
        // 评定信息
        result.put("auditInfo", flowList);
        // 驳回次数
        result.put("rejectCount", rejectList.getTotal());
        
        return result;
    }
    
    /** 
     * 返回抄送人编号列表
     * <功能详细描述>
     * @param ccs 抄送人列表
     * @return String 抄送人编号列表
     * @see [类、类#方法、类#成员]
     */
    private String getCcStaffIds(JSONArray ccs)
    {
        // 抄送人编号
        String sendee = "";
        if (ccs.size() > 0)
        {
            for (int i = 0; i < ccs.size(); i++)
            {
                JSONObject cc = ccs.getJSONObject(i);
                sendee += cc.getString("staffId") + "|";
            }
        }
        // 去掉最后一个"|"
        sendee = sendee.substring(0, sendee.length() - 1);
        return sendee;
    }
}