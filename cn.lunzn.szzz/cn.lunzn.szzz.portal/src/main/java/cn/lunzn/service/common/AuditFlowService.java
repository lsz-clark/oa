package cn.lunzn.service.common;

import java.util.Calendar;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.lunzn.common.bean.BaseResponse;
import cn.lunzn.common.bean.PaginationBean;
import cn.lunzn.common.constant.DefaultURI;
import cn.lunzn.common.constant.ResultCodeJson;
import cn.lunzn.common.date.DateUtil;
import cn.lunzn.common.enums.AuditEnum;
import cn.lunzn.common.enums.CommonResultCode;
import cn.lunzn.common.enums.StateEnum;
import cn.lunzn.common.http.QywxHttpClient;
import cn.lunzn.common.http.ServiceClient;
import cn.lunzn.common.response.ListResponse;
import cn.lunzn.model.common.AuditFlow;
import cn.lunzn.model.sz.SZ;
import cn.lunzn.model.user.Employee;
import cn.lunzn.model.zz.ZZ;

/**
 * @author clark
 * 审核电子流类业务
 */
@Service
public class AuditFlowService
{
    
    /** 
     * 新增电子流 - 默认步骤：部门<br>
     * 适用于年度述职
     * @param flow 电子流对象
     * @return BaseResponse
     * @see [类、类#方法、类#成员]
     */
    public BaseResponse addAuditFlow(AuditFlow flow)
    {
        // 待处理
        flow.setHandleFlag(1);
        // 提交时间
        flow.setBeginTime(
            DateUtil.formatDateToString(DateUtil.DATE_FORMAT_SECOND_BAR, Calendar.getInstance().getTime()));
        
        BaseResponse reuslt =
            ServiceClient.call(flow, DefaultURI.getAuditFlowURIKey(DefaultURI.URI_ADD), BaseResponse.class);
        return reuslt;
        
    }
    
    /** 
     * 新增电子流 - 默认步骤：部门<br>
     * 适用于年度述职
     * @param auditId 业务ID
     * @param auditType 审核类型  1-年度述职 2-员工转正
     * @param auditStaffId 审核人编号
     * @param auditStaffName 审核人名称
     * @param staffInfo 当前登录员工信息
     * @return BaseResponse
     * @see [类、类#方法、类#成员]
     */
    public BaseResponse addAuditFlow(Integer auditId, Integer auditType, String auditStaffId, String auditStaffName,
        Employee staffInfo)
    {
        AuditFlow flow = new AuditFlow();
        // 审核人信息
        flow.setStaffId(auditStaffId);
        flow.setStaffName(auditStaffName);
        
        // 提交人信息
        flow.setSubmitStaffId(staffInfo.getEmployeeId());
        flow.setSubmitStaffName(staffInfo.getName());
        
        // 业务ID
        flow.setAuditId(auditId);
        
        // 待处理
        flow.setHandleFlag(1);
        
        // 审核类型-转正申请
        flow.setAuditType(auditType);
        // 审核步骤 1-部门 2-人事 3-总经理
        flow.setAuditStep(AuditEnum.AUDIT_STEP_DEPT_1.getId());
        
        flow.setBeginTime(
            DateUtil.formatDateToString(DateUtil.DATE_FORMAT_SECOND_BAR, Calendar.getInstance().getTime()));
        
        BaseResponse reuslt =
            ServiceClient.call(flow, DefaultURI.getAuditFlowURIKey(DefaultURI.URI_ADD), BaseResponse.class);
        return reuslt;
    }
    
    /**
     * 查询
     * @param sessionStaff 当前登录的用户
     * @param param 参数
     * @return JSONObject
     */
    public JSONObject query(Employee sessionStaff, AuditFlow param)
    {
        // 设置当前用户id
        param.setStaffId(sessionStaff.getEmployeeId().toString());
        
        // 设置分页
        param.setPage(PaginationBean.getPagination(param.getPage()));
        
        String reuslt = ServiceClient.call(param, DefaultURI.getAuditFlowURIKey(DefaultURI.URI_QUERY));
        return JSONObject.parseObject(reuslt);
    }
    
    /**
     * 查询-不分页
     * @param param 参数
     * @return JSONObject
     */
    public JSONObject query(AuditFlow param)
    {
        String reuslt = ServiceClient.call(param, DefaultURI.getAuditFlowURIKey(DefaultURI.URI_QUERY));
        return JSONObject.parseObject(reuslt);
    }
    
    /**
     * 查询数量
     * @param param 参数
     * @return JSONObject
     */
    public JSONObject queryTotal(AuditFlow param)
    {
        String reuslt = ServiceClient.call(param, DefaultURI.getAuditFlowURIKey(DefaultURI.URI_QUERY_TOTAL));
        return JSONObject.parseObject(reuslt);
    }
    
    /**
     * 查询数量
     * @param param 参数
     * @param seesionStaff 当前登录员工
     * @return JSONObject
     */
    public JSONObject undo(AuditFlow param, Employee seesionStaff)
    {
        BaseResponse br = new BaseResponse();
        
        ListResponse<AuditFlow> auditFlows = ServiceClient.callList(param,
            DefaultURI.getAuditFlowURIKey(DefaultURI.URI_QUERY),
            AuditFlow.getTypeReference());
        
        if (!auditFlows.isSuccess())
        {
            return JSONObject.parseObject(auditFlows.toString());
        }
        
        if (CollectionUtils.isEmpty(auditFlows.getResult()))
        {
            br.setCode(CommonResultCode.INVALID_DATA_STATUS.getCode());
            return JSONObject.parseObject(br.toString());
        }
        
        // 本人才能撤销
        AuditFlow oneAuditFlow = auditFlows.getResult().get(0);
        if (!oneAuditFlow.getSubmitStaffId().equals(seesionStaff.getEmployeeId()))
        {
            br.setCode(CommonResultCode.SERVICE_BUSINESS_6015001.getCode());
            return JSONObject.parseObject(br.toString());
        }
        
        // 已处理过的不能撤销
        if (auditFlows.getTotal() > 1 || oneAuditFlow.getHandleFlag().intValue() == AuditEnum.AUDIT_HANDLE_OK.getId())
        {
            br.setCode(CommonResultCode.SERVICE_BUSINESS_6015002.getCode());
            return JSONObject.parseObject(br.toString());
        }
        
        // 调接口撤销
        br = ServiceClient.call(param, DefaultURI.getAuditFlowURIKey(DefaultURI.URI_DELETE), BaseResponse.class);
        
        if (br.isSuccess())
        {
            // 更新各个业务的审核状态
            if (param.getAuditType().intValue() == AuditEnum.SZ_TYPE_1.getId())
            {
                SZ sz = new SZ();
                sz.setSzId(param.getAuditId());
                ListResponse<SZ> list = ServiceClient.callList(sz,
                    DefaultURI.getReportWorkURIKey(DefaultURI.URI_QUERY),
                    SZ.getTypeReference());
                
                SZ curSz = list.getResult().get(0);
                curSz.setState(StateEnum.AUDIT_STATE_EDITING_1.getId());
                ServiceClient.call(curSz,
                    DefaultURI.getReportWorkURIKey(DefaultURI.URI_UPDATE_STATE),
                    BaseResponse.class);
                
                // 审核人通知
                QywxHttpClient.sendMsg(oneAuditFlow.getStaffId(), curSz.getStaffName() + "的年度述职申请，已偷偷撤销");
                // 抄送人通知
                if (!StringUtils.isEmpty(curSz.getCc()))
                {
                    JSONArray ccs = JSONArray.parseArray(curSz.getCc());
                    QywxHttpClient.sendMsg(getCcStaffIds(ccs), curSz.getStaffName() + "的年度述职申请，已偷偷撤销");
                }
            }
            else
            {
                ZZ query = new ZZ();
                query.setZzId(param.getAuditId());
                ListResponse<ZZ> list =
                    ServiceClient.callList(query, DefaultURI.getZZURIKey(DefaultURI.URI_QUERY), ZZ.getTypeReference());
                
                ZZ curZZ = list.getResult().get(0);
                curZZ.setState(StateEnum.AUDIT_STATE_EDITING_1.getId());
                ServiceClient.call(curZZ, DefaultURI.getZZURIKey(DefaultURI.URI_UPDATE), BaseResponse.class);
                
                // 审核人通知
                QywxHttpClient.sendMsg(oneAuditFlow.getStaffId(), curZZ.getStaffName() + "的转正申请，已偷偷撤销");
                // 抄送人通知
                if (!StringUtils.isEmpty(curZZ.getCc()))
                {
                    JSONArray ccs = JSONArray.parseArray(curZZ.getCc());
                    QywxHttpClient.sendMsg(getCcStaffIds(ccs), curZZ.getStaffName() + "的转正申请，已偷偷撤销");
                }
            }
        }
        
        return JSONObject.parseObject(br.toString());
    }
    
    /**
     * 审核处理，转正、述职
     * @param sessionStaff 当前登录的用户
     * @param param 参数
     * @return JSONObject
     * @deprecated
     */
    public BaseResponse auditHandler(Employee sessionStaff, AuditFlow param)
    {
        BaseResponse response = new BaseResponse();
        
        param.setHandleFlag(AuditEnum.AUDIT_HANDLE_OK.getId());
        param.setFinishTime(
            DateUtil.formatDateToString(DateUtil.DATE_FORMAT_SECOND_BAR, Calendar.getInstance().getTime()));
        
        // 根据flowId查询当前电子流信息，已处理的电子流无法再次处理
        AuditFlow queryParam = new AuditFlow();
        queryParam.setFlowId(param.getFlowId());
        ListResponse<AuditFlow> flowList = ServiceClient.callList(queryParam,
            DefaultURI.getAuditFlowURIKey(DefaultURI.URI_QUERY),
            AuditFlow.getTypeReference());
        if (flowList.isSuccess() && !CollectionUtils.isEmpty(flowList.getResult()))
        {
            AuditFlow queryFlow = flowList.getResult().get(0);
            if (queryFlow.getHandleFlag().intValue() == AuditEnum.AUDIT_HANDLE_OK.getId())
            {
                // 已处理的电子流无法再次处理
                return ResultCodeJson.getBr3();
            }
        }
        
        // 更新审核结果
        BaseResponse updateResponse =
            ServiceClient.call(param, DefaultURI.getAuditFlowURIKey(DefaultURI.URI_UPDATE), BaseResponse.class);
        if (!updateResponse.isSuccess())
        {
            // 更新失败直接返回
            return updateResponse;
        }
        
        // 驳回直接返回
        if (param.getState().intValue() == StateEnum.FLOW_STATE_REJECT_2.getId())
        {
            return response;
        }
        
        // 更新成功继续
        if (param.getAuditType().intValue() == AuditEnum.SZ_TYPE_1.getId())
        {
            // 述职
            // 判断当前审核人是否还有上一级
            if (!StringUtils.isEmpty(sessionStaff.getSupervisor()))
            {
                // 给下一个审核人记录电子流
                response = this.addAuditFlow(param.getAuditId(),
                    param.getAuditType(),
                    sessionStaff.getSupervisor(),
                    sessionStaff.getSupervisorName(),
                    sessionStaff);
                
                if (response.isSuccess())
                {
                    // 发送企业微信消息
                    QywxHttpClient.sendMsg(sessionStaff.getSupervisor(), "您有一则年度述职需要审批");
                }
            }
        }
        else
        {
            // 查询转正信息
            ZZ query = new ZZ();
            query.setZzId(param.getAuditId());
            // 调用转正原子服务，获取数据
            ListResponse<ZZ> list =
                ServiceClient.callList(query, DefaultURI.getZZURIKey(DefaultURI.URI_QUERY), ZZ.getTypeReference());
            // 查询失败，返回错误
            if (!list.isSuccess())
            {
                return list;
            }
            
            // 获取员工转正信息
            ZZ curZZ = list.getResult().get(0);
            // 获取下一个审核人
            String auditors = curZZ.getAuditor();
            JSONArray auditorArray = JSONArray.parseArray(auditors);
            // 记录第几个审核人
            int step = param.getAuditStep() + 1;
            JSONObject auditor = auditorArray.getJSONObject(param.getAuditStep() + 1);
            // 如果没有下一个审核人，则不生成新的电子流
            if (null == auditor)
            {
                return response;
            }
            
            // 存在下一个审核人，生成新的电子流
            AuditFlow flow = new AuditFlow();
            String staffId = auditor.getString("staffId");
            String staffName = auditor.getString("staffName");
            // 审核人信息
            flow.setStaffId(staffId);
            flow.setStaffName(staffName);
            // 提交人信息
            flow.setSubmitStaffId(sessionStaff.getEmployeeId());
            flow.setSubmitStaffName(sessionStaff.getName());
            // 业务ID
            flow.setAuditId(param.getAuditId());
            // 审核类型-转正申请
            flow.setAuditType(param.getAuditType());
            // 审核步骤 2...
            flow.setAuditStep(step);
            response = this.addAuditFlow(flow);
            
            if (response.isSuccess())
            {
                // 发送企业微信消息
                QywxHttpClient.sendMsg(staffId, "您有一则转正申请需要审批");
            }
        }
        
        return response;
    }
    
    /**
     * 审核处理，转正、述职
     * @param param 电子流信息
     * @param auditors 审核人
     * @param queryFlow 数据库电子流
     * @return BaseResponse
     */
    public BaseResponse auditHandler(AuditFlow param, AuditFlow queryFlow, String auditors)
    {
        BaseResponse response = new BaseResponse();
        
        param.setHandleFlag(AuditEnum.AUDIT_HANDLE_OK.getId());
        param.setFinishTime(
            DateUtil.formatDateToString(DateUtil.DATE_FORMAT_SECOND_BAR, Calendar.getInstance().getTime()));
        
        if (queryFlow.getHandleFlag().intValue() == AuditEnum.AUDIT_HANDLE_OK.getId())
        {
            // 已处理的电子流无法再次处理
            return ResultCodeJson.getBr3();
        }
        
        // 更新审核结果
        BaseResponse updateResponse =
            ServiceClient.call(param, DefaultURI.getAuditFlowURIKey(DefaultURI.URI_UPDATE), BaseResponse.class);
        if (!updateResponse.isSuccess())
        {
            // 更新失败直接返回
            return updateResponse;
        }
        
        // 驳回直接返回
        if (param.getState().intValue() == StateEnum.FLOW_STATE_REJECT_2.getId())
        {
            return response;
        }
        
        // 如果当前审核步骤等于审核人总个数，那么不再生成电子流
        JSONArray auditorArray = JSONArray.parseArray(auditors);
        if (queryFlow.getAuditStep() >= auditorArray.size())
        {
            return response;
        }
        
        // 下一个审核人
        JSONObject nextAuditor = auditorArray.getJSONObject(queryFlow.getAuditStep());
        // 述职, 生成下一个电子流
        AuditFlow flow = new AuditFlow();
        // 审核人信息
        flow.setStaffId(nextAuditor.getString("staffId"));
        flow.setStaffName(nextAuditor.getString("staffName"));
        // 提交人信息
        flow.setSubmitStaffId(queryFlow.getSubmitStaffId());
        flow.setSubmitStaffName(queryFlow.getSubmitStaffName());
        // 业务ID
        flow.setAuditId(queryFlow.getAuditId());
        // 审核类型-转正申请
        flow.setAuditType(queryFlow.getAuditType());
        // 审核步骤
        flow.setAuditStep(queryFlow.getAuditStep() + 1);
        response = this.addAuditFlow(flow);
        
        // 发送审核消息
        if (queryFlow.getAuditType().intValue() == AuditEnum.SZ_TYPE_1.getId())
        {
            // 述职
            QywxHttpClient.sendMsg(nextAuditor.getString("staffId"), queryFlow.getSubmitStaffName() + "提交了年度述职申请，请审批");
        }
        else
        {
            // 转正
            QywxHttpClient.sendMsg(nextAuditor.getString("staffId"), queryFlow.getSubmitStaffName() + "提交了转正申请，请审批");
        }
        
        return response;
    }
    
    /** 
     * 获取抄送人员ID
     * @param ccs 抄送人
     * @return String
     * @see [类、类#方法、类#成员]
     */
    private String getCcStaffIds(JSONArray ccs)
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < ccs.size(); i++)
        {
            JSONObject cc = ccs.getJSONObject(i);
            sb.append(cc.getString("staffId")).append('|');
        }
        
        String ccStaffIds = sb.toString();
        return ccStaffIds.substring(0, ccStaffIds.length() - 1);
    }
}
