package cn.lunzn.model.common;

import com.alibaba.fastjson.TypeReference;

import cn.lunzn.common.annotation.Location;
import cn.lunzn.common.bean.BaseBean;
import cn.lunzn.common.bean.PaginationBean;
import cn.lunzn.common.response.ListResponse;

/**
 * 审核电子流
 * 
 * @author  clark
 * @version  [版本号, 2017年8月18日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Location(module = "szservice")
public class AuditFlow extends BaseBean
{
    private static final long serialVersionUID = 8003371425623984536L;
    
    /**
     * 编号
     */
    private Integer flowId;
    
    /**
     * 提交人姓名
     */
    private String submitStaffName;
    
    /**
     * 提交人
     */
    private String submitStaffId;
    
    /**
     * 处理人姓名
     */
    private String staffName;
    
    /**
     * 处理人
     */
    private String staffId;
    
    /**
     * 1-述职 2-转正
     */
    private Integer auditType;
    
    /**
     * 述职或转正编号
     */
    private Integer auditId;
    
    /**
     * 完成标识
     */
    private Integer handleFlag;
    
    /**
     * 审核步骤
     */
    private Integer auditStep;
    
    /**
     * 审核内容，任意格式
     */
    private String auditValue;
    
    /**
     * 审核状态1-通过 2-驳回
     */
    private Integer state;
    
    /**
     * 提交日期
     */
    private String beginTime;
    
    /**
     * 完成日期
     */
    private String finishTime;
    
    /**
     * 非数据库字段-分页
     */
    private PaginationBean page;
    
    /**
     * 非数据库字段-开始时间
     */
    private String startTime;
    
    /**
     * 非数据库字段-结束时间
     */
    private String endTime;
    
    public Integer getFlowId()
    {
        return flowId;
    }
    
    public void setFlowId(Integer flowId)
    {
        this.flowId = flowId;
    }
    
    public String getSubmitStaffId()
    {
        return submitStaffId;
    }
    
    public void setSubmitStaffId(String submitStaffId)
    {
        this.submitStaffId = submitStaffId;
    }
    
    public String getStaffId()
    {
        return staffId;
    }
    
    public void setStaffId(String staffId)
    {
        this.staffId = staffId;
    }
    
    public Integer getAuditType()
    {
        return auditType;
    }
    
    public void setAuditType(Integer auditType)
    {
        this.auditType = auditType;
    }
    
    public Integer getAuditId()
    {
        return auditId;
    }
    
    public void setAuditId(Integer auditId)
    {
        this.auditId = auditId;
    }
    
    public Integer getHandleFlag()
    {
        return handleFlag;
    }
    
    public void setHandleFlag(Integer handleFlag)
    {
        this.handleFlag = handleFlag;
    }
    
    public String getBeginTime()
    {
        return beginTime;
    }
    
    public void setBeginTime(String beginTime)
    {
        this.beginTime = beginTime;
    }
    
    public String getFinishTime()
    {
        return finishTime;
    }
    
    public void setFinishTime(String finishTime)
    {
        this.finishTime = finishTime;
    }
    
    public PaginationBean getPage()
    {
        return page;
    }
    
    public void setPage(PaginationBean page)
    {
        this.page = page;
    }
    
    public String getStartTime()
    {
        return startTime;
    }
    
    public void setStartTime(String startTime)
    {
        this.startTime = startTime;
    }
    
    public String getEndTime()
    {
        return endTime;
    }
    
    public void setEndTime(String endTime)
    {
        this.endTime = endTime;
    }
    
    public String getSubmitStaffName()
    {
        return submitStaffName;
    }
    
    public void setSubmitStaffName(String submitStaffName)
    {
        this.submitStaffName = submitStaffName;
    }
    
    public Integer getAuditStep()
    {
        return auditStep;
    }
    
    public void setAuditStep(Integer auditStep)
    {
        this.auditStep = auditStep;
    }
    
    public String getAuditValue()
    {
        return auditValue;
    }
    
    public void setAuditValue(String auditValue)
    {
        this.auditValue = auditValue;
    }
    
    public Integer getState()
    {
        return state;
    }
    
    public void setState(Integer state)
    {
        this.state = state;
    }
    
    public String getStaffName()
    {
        return staffName;
    }
    
    public void setStaffName(String staffName)
    {
        this.staffName = staffName;
    }
    
    /**
     * 查询时使用
     * @return TypeReference<ListResponse<AuditFlow>>
     */
    public static TypeReference<ListResponse<AuditFlow>> getTypeReference()
    {
        return new TypeReference<ListResponse<AuditFlow>>()
        {
        };
    }
}
