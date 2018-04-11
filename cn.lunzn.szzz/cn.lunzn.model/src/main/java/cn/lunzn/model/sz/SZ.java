package cn.lunzn.model.sz;

import com.alibaba.fastjson.TypeReference;

import cn.lunzn.common.annotation.Location;
import cn.lunzn.common.bean.BaseBean;
import cn.lunzn.common.bean.PaginationBean;
import cn.lunzn.common.response.ListResponse;

/**
 * @author 杜浩
 * 年度述职信息bean
 */
@Location(module = "szservice")
public class SZ extends BaseBean
{
    private static final long serialVersionUID = 4893982224998391917L;
    
    /**
     * 述职编号,主键,自增长
     */
    private Integer szId;
    
    /**
     * 处理人员工号
     */
    private String staffId;
    
    /**
     * 员工姓名
     */
    private String staffName;
    
    /**
     * 员工职位
     */
    private String position;
    
    /**
     * 员工部门
     */
    private String department;
    
    /**
     * 年度述职对应的年度区间
     */
    private String timeQuantum;
    
    /**
     * 员工入职日期
     */
    private String entryDate;
    
    /**
     * 员工毕业日期
     */
    private String gradDate;
    
    /**
     * 员工学历
     */
    private String education;
    
    /**
     * 审核状态 1-编辑中2-待审批 3-审核通过 4-驳回
     */
    private Integer state;
    
    /**
     * 审核日期
     */
    private String submitdate;
    
    /**
     * 工作成果
     */
    private String workresults;
    
    /**
     * 自我评价
     */
    private String evaluation;
    
    /**
     * 不足&改进
     */
    private String deficiency;
    
    /**
     * 成长规划
     */
    private String plan;
    
    /**
     * 驳回次数
     */
    private String rejectTime;
    
    /**
     * 提交次数
     */
    private String submitTime;
    
    /**
     * 分页对象
     */
    private PaginationBean page;
    
    /**
     * 是否为HR、管理员
     */
    private boolean isAdmin;
    
    /**
     * 审核人
     */
    private String auditor;
    
    /**
     * 抄送人
     */
    private String cc;
    
    /**
     * 查询时使用
     * @return TypeReference<ListResponse<AuditFlow>>
     */
    public static TypeReference<ListResponse<SZ>> getTypeReference()
    {
        return new TypeReference<ListResponse<SZ>>()
        {
        };
    }
    
    public Integer getSzId()
    {
        return szId;
    }
    
    public void setSzId(Integer szId)
    {
        this.szId = szId;
    }
    
    public String getStaffId()
    {
        return staffId;
    }
    
    public void setStaffId(String staffId)
    {
        this.staffId = staffId;
    }
    
    public String getStaffName()
    {
        return staffName;
    }
    
    public void setStaffName(String staffName)
    {
        this.staffName = staffName;
    }
    
    public String getEntryDate()
    {
        return entryDate;
    }
    
    public void setEntryDate(String entryDate)
    {
        this.entryDate = entryDate;
    }
    
    public String getGradDate()
    {
        return gradDate;
    }
    
    public void setGradDate(String gradDate)
    {
        this.gradDate = gradDate;
    }
    
    public String getEducation()
    {
        return education;
    }
    
    public void setEducation(String education)
    {
        this.education = education;
    }
    
    public Integer getState()
    {
        return state;
    }
    
    public void setState(Integer state)
    {
        this.state = state;
    }
    
    public String getSubmitdate()
    {
        return submitdate;
    }
    
    public void setSubmitdate(String submitdate)
    {
        this.submitdate = submitdate;
    }
    
    public String getWorkresults()
    {
        return workresults;
    }
    
    public void setWorkresults(String workresults)
    {
        this.workresults = workresults;
    }
    
    public String getEvaluation()
    {
        return evaluation;
    }
    
    public void setEvaluation(String evaluation)
    {
        this.evaluation = evaluation;
    }
    
    public String getDeficiency()
    {
        return deficiency;
    }
    
    public void setDeficiency(String deficiency)
    {
        this.deficiency = deficiency;
    }
    
    public String getPlan()
    {
        return plan;
    }
    
    public void setPlan(String plan)
    {
        this.plan = plan;
    }
    
    public PaginationBean getPage()
    {
        return page;
    }
    
    public void setPage(PaginationBean page)
    {
        this.page = page;
    }
    
    public String getTimeQuantum()
    {
        return timeQuantum;
    }
    
    public void setTimeQuantum(String timeQuantum)
    {
        this.timeQuantum = timeQuantum;
    }
    
    public String getPosition()
    {
        return position;
    }
    
    public void setPosition(String position)
    {
        this.position = position;
    }
    
    public String getDepartment()
    {
        return department;
    }
    
    public void setDepartment(String department)
    {
        this.department = department;
    }
    
    public String getRejectTime()
    {
        return rejectTime;
    }
    
    public void setRejectTime(String rejectTime)
    {
        this.rejectTime = rejectTime;
    }
    
    public boolean isAdmin()
    {
        return isAdmin;
    }
    
    public void setAdmin(boolean isAdmin)
    {
        this.isAdmin = isAdmin;
    }
    
    public String getSubmitTime()
    {
        return submitTime;
    }
    
    public void setSubmitTime(String submitTime)
    {
        this.submitTime = submitTime;
    }
    
    public String getAuditor()
    {
        return auditor;
    }
    
    public void setAuditor(String auditor)
    {
        this.auditor = auditor;
    }
    
    public String getCc()
    {
        return cc;
    }
    
    public void setCc(String cc)
    {
        this.cc = cc;
    }
    
}
