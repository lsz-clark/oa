package cn.lunzn.service.sz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.lunzn.common.bean.BaseResponse;
import cn.lunzn.common.bean.PaginationBean;
import cn.lunzn.common.cache.PropertyCache;
import cn.lunzn.common.constant.DefaultURI;
import cn.lunzn.common.constant.ResultCodeJson;
import cn.lunzn.common.enums.AuditEnum;
import cn.lunzn.common.enums.CommonResultCode;
import cn.lunzn.common.enums.StateEnum;
import cn.lunzn.common.http.QywxHttpClient;
import cn.lunzn.common.http.ServiceClient;
import cn.lunzn.common.response.ListResponse;
import cn.lunzn.model.common.AuditFlow;
import cn.lunzn.model.sz.SZ;
import cn.lunzn.model.user.Employee;
import cn.lunzn.service.common.AuditFlowService;
import cn.lunzn.util.StaffUtil;

/**
 * @author clark
 * 年度述职服务层
 */
@Service
public class ReportWorkService
{
    
    /**
     * 审核电子流业务类
     */
    @Autowired
    private AuditFlowService auditFlowService;
    
    /**
     * 查询当前员工的年度述职
     * @param em 员工信息
     * @param jsonData 参数
     * @return JSONObject 响应
     */
    public JSONObject queryMe(Employee em, String jsonData)
    {
        // 参数转换
        SZ sz = JSON.parseObject(jsonData, SZ.class);
        //员工编号
        sz.setStaffId(em.getEmployeeId());
        // 设置分页
        sz.setPage(PaginationBean.getPagination(sz.getPage()));
        // 调用述职原子服务查询述职列表
        BaseResponse result =
            ServiceClient.callList(sz, DefaultURI.getReportWorkURIKey(DefaultURI.URI_QUERY), SZ.getTypeReference());
        
        return JSONObject.parseObject(result.toString());
    }
    
    /**
     * 1、获取角色id，判断角色权限，根据权限构造参数调用原子服务请求数据
     *   如果是高级权限则返回“所有非编辑状态的述职报告+本人编辑状态述职报告”
     *    如果是低级角色则返回“本人非编辑状态述职报告+本人编辑状态述职报告”
     *    如果请求参数带有audit参数，则表示为审核界面，审核界面可以查看所有的审核过的信息
     * 2、调用述职原子服务，其中传入参数sz
     * 3、根据返回结果处理且响应界面
     * @param em 员工信息
     * @param jsonData 参数
     * @return JSONObject 响应
     */
    public JSONObject queryReportList(Employee em, String jsonData)
    {
        // 参数转换
        SZ sz = JSON.parseObject(jsonData, SZ.class);
        // 人事 & 管理员角色员工  或者  待我审核界面查看历史审核信息，允许查看
        if (StaffUtil.ROLE_ADMIN_1.equals(em.getRoleId()) || StaffUtil.ROLE_HR_2.equals(em.getRoleId())
            || jsonData.contains("audit"))
        {
            //高级权限
            sz.setAdmin(true);
        }
        
        //员工编号
        sz.setStaffId(em.getEmployeeId());
        // 设置分页
        sz.setPage(PaginationBean.getPagination(sz.getPage()));
        // 调用述职原子服务查询述职列表
        BaseResponse result =
            ServiceClient.callList(sz, DefaultURI.getReportWorkURIKey(DefaultURI.URI_QUERY), SZ.getTypeReference());
        // 查询成功
        if (result != null && result.isSuccess())
        {
            // 返回结果
            return JSONObject.parseObject(result.toString());
        }
        else
        {
            // 提示失败信息
            return new JSONObject();
        }
    }
    
    /**
     * 保存述职信息草稿、保存述职信息且提交电子流
     * 1、判断是否需要提交电子流
     * 	    没有则报错提示，有提交审核电子流；不需要提交电子流则直接返回
     * 2、判断是否达到当年述职上限，达到则提示
     * 3、调用述职原子服务，保存述职报告（述职报告状态为编辑中）
     * 4、电子流提交成功则更新述职报告状态为审核中，失败则报错
     * @param em 员工信息
     * @param jsonData 参数
     * @return JSONObject 响应
     */
    public JSONObject saveReportWorkInfo(String jsonData, Employee em)
    {
        //参数转换
        SZ sz = JSON.parseObject(jsonData, SZ.class);
        //如果提交，判断是否选择了审核人
        if (JSONObject.parseObject(jsonData).getBoolean("isSubmit") && StringUtils.isEmpty(sz.getAuditor()))
        {
            //直接主管为空，返回失败
            BaseResponse error = new BaseResponse();
            error.setCode(CommonResultCode.SERVICE_BUSINESS_6014001.getCode());
            return JSONObject.parseObject(error.toString());
        }
        //获取述职次数限制
        JSONObject properties = PropertyCache.getProp();
        int submitCount = properties.getIntValue("sz.count");
        //查询当前年份述职次数
        ListResponse<SZ> result1 = ServiceClient.callList(sz,
            DefaultURI.getReportWorkURIKey(DefaultURI.URI_SUBMIT_TIME),
            SZ.getTypeReference());
        // 查询成功
        if (result1.isSuccess())
        {
            //已经提交述职记录大于等于限制次数，则提示超过上限
            if (submitCount <= Integer.parseInt(result1.getResult().get(0).getSubmitTime()))
            {
                // 提示超过限制
                return ResultCodeJson.get4();
            }
        }
        else
        {
            // 提示失败信息
            return ResultCodeJson.get601();
        }
        //述职报告状态设为编辑中
        sz.setState(StateEnum.AUDIT_STATE_EDITING_1.getId());
        //调用述职原子服务保存
        BaseResponse result =
            ServiceClient.callList(sz, DefaultURI.getReportWorkURIKey(DefaultURI.URI_ADD), SZ.getTypeReference());
        //是否需要提交电子流
        if (JSONObject.parseObject(jsonData).getBoolean("isSubmit") && result.isSuccess())
        {
            //查询保存成功后的述职记录，从而获取szId
            sz.setPage(PaginationBean.getPagination(sz.getPage()));
            //调用述职原子服务查询述职列表
            ListResponse<SZ> list =
                ServiceClient.callList(sz, DefaultURI.getReportWorkURIKey(DefaultURI.URI_QUERY), SZ.getTypeReference());
            //查询失败，返回错误
            if (!list.isSuccess())
            {
                return JSONObject.parseObject(list.toString());
            }
            
            // 第一次提交，取第一个审核人
            JSONArray auditors = JSONArray.parseArray(sz.getAuditor());
            JSONObject firstAuditor = auditors.getJSONObject(0);
            
            //记录审核电子流
            SZ curSZ = list.getResult().get(0);
            
            // 创建电子流
            AuditFlow flow = new AuditFlow();
            // 审核人信息
            flow.setStaffId(firstAuditor.getString("staffId"));
            flow.setStaffName(firstAuditor.getString("staffName"));
            // 提交人信息
            flow.setSubmitStaffId(curSZ.getStaffId());
            flow.setSubmitStaffName(curSZ.getStaffName());
            // 业务ID
            flow.setAuditId(curSZ.getSzId());
            // 待处理
            flow.setHandleFlag(1);
            // 审核类型-述职申请
            flow.setAuditType(AuditEnum.SZ_TYPE_1.getId());
            // 审核步骤 1
            flow.setAuditStep(1);
            BaseResponse auditResponse = auditFlowService.addAuditFlow(flow);
            if (auditResponse.isSuccess())
            {
                //提交审核成功则将述职报告状态更新为审核中
                curSZ.setState(StateEnum.AUDIT_STATE_AUDITING_2.getId());
                result = ServiceClient.call(curSZ,
                    DefaultURI.getReportWorkURIKey(DefaultURI.URI_UPDATE_STATE),
                    BaseResponse.class);
                // 发送消息给审核人
                QywxHttpClient.sendMsg(firstAuditor.getString("staffId"), em.getName() + "提交了年度述职申请，请审批");
                
                // 发送消息给抄送人
                if (!StringUtils.isEmpty(sz.getCc()))
                {
                    JSONArray ccs = JSONArray.parseArray(sz.getCc());
                    QywxHttpClient.sendMsg(getCcStaffIds(ccs), em.getName() + "提交了年度述职申请");
                }
            }
            else
            {
                //电子流提交失败，提示报错
                return JSONObject.parseObject(auditResponse.toString());
            }
        }
        return JSONObject.parseObject(result.toString());
    }
    
    /**
     * 查询驳回次数
     * @param jsonData 参数
     * @return JSONObject 响应
     */
    public JSONObject queryRejectTime(String jsonData)
    {
        // 参数转换
        SZ sz = JSON.parseObject(jsonData, SZ.class);
        // 调用述职原子服务查询驳回次数
        BaseResponse result = ServiceClient.callList(sz,
            DefaultURI.getReportWorkURIKey(DefaultURI.URI_REJECT_TIME),
            SZ.getTypeReference());
        // 查询成功
        if (result != null && result.isSuccess())
        {
            // 返回结果
            return JSONObject.parseObject(result.toString());
        }
        else
        {
            // 提示失败信息
            return ResultCodeJson.get601();
        }
    }
    
    /**
     * 查询驳回次数
     * @param em 员工信息
     * @param param 参数
     * @return BaseResponse 响应
     */
    public BaseResponse auditHandler(Employee em, AuditFlow param)
    {
        // 查询转正信息
        SZ sz = new SZ();
        sz.setSzId(param.getAuditId());
        //查询保存成功后的述职记录，从而获取szId
        sz.setPage(PaginationBean.getPagination(sz.getPage()));
        //调用述职原子服务查询述职列表
        ListResponse<SZ> list =
            ServiceClient.callList(sz, DefaultURI.getReportWorkURIKey(DefaultURI.URI_QUERY), SZ.getTypeReference());
        //查询失败，返回错误
        if (!list.isSuccess())
        {
            return list;
        }
        BaseResponse updateResponse = new BaseResponse();
        // 获取年度述职信息
        SZ curSZ = list.getResult().get(0);
        
        // 根据flowId查询当前电子流信息，已处理的电子流无法再次处理
        AuditFlow queryParam = new AuditFlow();
        queryParam.setFlowId(param.getFlowId());
        ListResponse<AuditFlow> flowList = ServiceClient.callList(queryParam,
            DefaultURI.getAuditFlowURIKey(DefaultURI.URI_QUERY),
            AuditFlow.getTypeReference());
        if (!flowList.isSuccess() || CollectionUtils.isEmpty(flowList.getResult()))
        {
            return ResultCodeJson.getBr2();
        }
        
        AuditFlow queryFlow = flowList.getResult().get(0);
        
        // 电子流处理
        updateResponse = auditFlowService.auditHandler(param, queryFlow, curSZ.getAuditor());
        if (!updateResponse.isSuccess())
        {
            return updateResponse;
        }
        
        // 通过
        if (param.getState().intValue() == StateEnum.FLOW_STATE_PASS_1.getId())
        {
            // 如果当前审核步骤等于审核人总个数，那么整个审核已完成
            JSONArray auditorArray = JSONArray.parseArray(curSZ.getAuditor());
            if (queryFlow.getAuditStep() >= auditorArray.size())
            {
                // 通过状态
                curSZ.setState(StateEnum.AUDIT_STATE_APPROVE_3.getId());
                updateResponse = ServiceClient.call(curSZ,
                    DefaultURI.getReportWorkURIKey(DefaultURI.URI_UPDATE_STATE),
                    BaseResponse.class);
                // 发送消息
                QywxHttpClient.sendMsg(curSZ.getStaffId(), "您的年度述职申请已通过");
                
                // 发送消息给抄送人
                if (!StringUtils.isEmpty(curSZ.getCc()))
                {
                    JSONArray ccs = JSONArray.parseArray(curSZ.getCc());
                    QywxHttpClient.sendMsg(getCcStaffIds(ccs), curSZ.getStaffName() + "的年度述职申请已通过");
                }
            }
        }
        else
        {
            // 驳回状态
            curSZ.setState(StateEnum.AUDIT_STATE_REJECT_4.getId());
            updateResponse = ServiceClient.call(curSZ,
                DefaultURI.getReportWorkURIKey(DefaultURI.URI_UPDATE_STATE),
                BaseResponse.class);
            
            // 发送消息
            QywxHttpClient.sendMsg(curSZ.getStaffId(), "您的年度述职申请已驳回");
            
            // 发送消息给抄送人
            if (!StringUtils.isEmpty(curSZ.getCc()))
            {
                JSONArray ccs = JSONArray.parseArray(curSZ.getCc());
                QywxHttpClient.sendMsg(getCcStaffIds(ccs), curSZ.getStaffName() + "的年度述职申请已驳回");
            }
        }
        
        return updateResponse;
    }
    
    /**
     * 查询述职报告所有信息，包括：述职报告内容信息，历史审核记录，驳回次数
     * @param em 员工信息
     * @param jsonData 参数
     * @return JSONObject 响应
     */
    public JSONObject queryReportInfo(Employee em, String jsonData)
    {
        // 参数转换
        SZ sz = JSON.parseObject(jsonData, SZ.class);
        // 设置分页
        sz.setPage(PaginationBean.getPagination(sz.getPage()));
        // 调用述职原子服务查询述职列表
        BaseResponse result =
            ServiceClient.callList(sz, DefaultURI.getReportWorkURIKey(DefaultURI.URI_QUERY), SZ.getTypeReference());
        
        // 返回结果
        return JSONObject.parseObject(result.toString());
    }
    
    /**
     * 查询述职报告所有信息，包括：述职报告内容信息，历史审核记录，驳回次数
     * @param szId 述职编号
     * @return JSONObject 响应
     */
    public JSONObject queryReportInfo(Integer szId)
    {
        // 参数转换
        SZ sz = new SZ();
        sz.setSzId(szId);
        //        // 设置分页
        //    	sz.setPage(PaginationBean.getPagination(sz.getPage()));
        // 调用述职原子服务查询述职列表
        ListResponse<SZ> list =
            ServiceClient.callList(sz, DefaultURI.getReportWorkURIKey(DefaultURI.URI_QUERY), SZ.getTypeReference());
        //查询失败，返回错误
        if (!list.isSuccess())
        {
            return JSONObject.parseObject(list.toString());
        }
        JSONObject json = ResultCodeJson.getSuccess();
        //1、述职信息
        json.put("info", list.getResult().get(0));
        //2、历史审核记录
        AuditFlow param = new AuditFlow();
        param.setAuditId(szId);
        //审核处理过的
        param.setHandleFlag(2);
        //述职记录的
        param.setAuditType(1);
        JSONObject auditInfo = auditFlowService.query(param);
        json.put("auditInfo", auditInfo);
        //3、驳回记录
        // 调用述职原子服务查询驳回次数
        list = ServiceClient.callList(sz,
            DefaultURI.getReportWorkURIKey(DefaultURI.URI_REJECT_TIME),
            SZ.getTypeReference());//查询失败，返回错误
        if (!list.isSuccess())
        {
            return JSONObject.parseObject(list.toString());
        }
        json.put("rejectCount", list.getResult().get(0).getRejectTime());
        return json;
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
