package cn.lunzn.model.user;

import com.alibaba.fastjson.JSON;

/**
 * 职员
 * @author  tianqiuming
 * @version  [版本号, 2017年8月10日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class Employee extends UserRole
{
    
    /**
     * 序列化
     */
    private static final long serialVersionUID = -6143908308945780902L;
    
    // 主键
    private String id;
    
    //  工号
    private String employeeId;
    
    //  员工姓名
    private String name;
    
    //  英文名
    private String enName;
    
    //  主管id
    private String supervisor;
    
    // 主管名
    private String supervisorName;
    
    // 移动电话
    private String mobiles;
    
    //   固定电话
    private String telephone;
    
    //  部门
    private String department;
    
    //    职位
    private String position;
    
    //  性别
    private Integer gender;
    
    //  邮箱
    private String email;
    
    //   状态
    private Integer state;
    
    /**
     * 首次登陆时间
     */
    private Long createTime;
    
    /**
     * 头像
     */
    private String avatar;
    
    public String getId()
    {
        return id;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public String getEmployeeId()
    {
        return employeeId;
    }
    
    public void setEmployeeId(String employeeId)
    {
        this.employeeId = employeeId;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getEnName()
    {
        return enName;
    }
    
    public void setEnName(String enName)
    {
        this.enName = enName;
    }
    
    public String getSupervisor()
    {
        return supervisor;
    }
    
    public void setSupervisor(String supervisor)
    {
        this.supervisor = supervisor;
    }
    
    public String getSupervisorName()
    {
        return supervisorName;
    }
    
    public void setSupervisorName(String supervisorName)
    {
        this.supervisorName = supervisorName;
    }
    
    public String getMobiles()
    {
        return mobiles;
    }
    
    public void setMobiles(String mobiles)
    {
        this.mobiles = mobiles;
    }
    
    public String getTelephone()
    {
        return telephone;
    }
    
    public void setTelephone(String telephone)
    {
        this.telephone = telephone;
    }
    
    public String getDepartment()
    {
        return department;
    }
    
    public void setDepartment(String department)
    {
        this.department = department;
    }
    
    public String getPosition()
    {
        return position;
    }
    
    public void setPosition(String position)
    {
        this.position = position;
    }
    
    public Integer getGender()
    {
        return gender;
    }
    
    public void setGender(Integer gender)
    {
        this.gender = gender;
    }
    
    public String getEmail()
    {
        return email;
    }
    
    public void setEmail(String email)
    {
        this.email = email;
    }
    
    public Integer getState()
    {
        return state;
    }
    
    public void setState(Integer state)
    {
        this.state = state;
    }
    
    public Long getCreateTime()
    {
        return createTime;
    }
    
    public void setCreateTime(Long createTime)
    {
        this.createTime = createTime;
    }
    
    public static long getSerialversionuid()
    {
        return serialVersionUID;
    }
    
    public String getAvatar()
    {
        return avatar;
    }
    
    public void setAvatar(String avatar)
    {
        this.avatar = avatar;
    }
    
    @Override
    public String toString()
    {
        return JSON.toJSONString(this);
    }
}