package cn.lunzn.model.zz;

import com.alibaba.fastjson.TypeReference;

import cn.lunzn.common.annotation.Location;
import cn.lunzn.common.bean.BaseBean;
import cn.lunzn.common.bean.PaginationBean;
import cn.lunzn.common.response.ListResponse;

/**
 * @author clark
 * 员工转正信息bean
 */
@Location(module = "zzservice")
public class ZZ extends BaseBean
{
    private static final long serialVersionUID = 6231831016456345261L;
    
    /**
     * 主键，自增长
     */
    private Integer zzId;
    
    /**
     * 员工编号
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
     * 员工入职日期
     */
    private String entryDate;
    
    /**
     * 员工毕业日期
     */
    private String gradDate;
    
    /**
     * 学历
     */
    private String education;
    
    /**
     * 转正日期
     */
    private String zzDate;
    
    /**
     * 转正状态 1-编辑中 2-待审批 3-审核通过 4-驳回
     */
    private Integer state;
    
    /**
     * 提交日期
     */
    private String submitDate;
    
    /**
     * 工作成果
     */
    private String effort;
    
    /**
     * 个人收获
     */
    private String gain;
    
    /**
     * 心得&建议
     */
    private String suggest;
    
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
    public static TypeReference<ListResponse<ZZ>> getTypeReference()
    {
        return new TypeReference<ListResponse<ZZ>>()
        {
        };
    }
    
    public Integer getZzId()
    {
        return zzId;
    }
    
    public void setZzId(Integer zzId)
    {
        this.zzId = zzId;
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
    
    public String getZzDate()
    {
        return zzDate;
    }
    
    public void setZzDate(String zzDate)
    {
        this.zzDate = zzDate;
    }
    
    public Integer getState()
    {
        return state;
    }
    
    public void setState(Integer state)
    {
        this.state = state;
    }
    
    public String getSubmitDate()
    {
        return submitDate;
    }
    
    public void setSubmitDate(String submitDate)
    {
        this.submitDate = submitDate;
    }
    
    public String getEffort()
    {
        return effort;
    }
    
    public void setEffort(String effort)
    {
        this.effort = effort;
    }
    
    public String getGain()
    {
        return gain;
    }
    
    public void setGain(String gain)
    {
        this.gain = gain;
    }
    
    public String getSuggest()
    {
        return suggest;
    }
    
    public void setSuggest(String suggest)
    {
        this.suggest = suggest;
    }
    
    public PaginationBean getPage()
    {
        return page;
    }
    
    public void setPage(PaginationBean page)
    {
        this.page = page;
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
    
    public boolean isAdmin()
    {
        return isAdmin;
    }
    
    public void setAdmin(boolean isAdmin)
    {
        this.isAdmin = isAdmin;
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
