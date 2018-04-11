package cn.lunzn.common.enums;

/**
 * 审核类枚举
 * @author clark
 * @version  [版本号, 2017年09月04日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public enum AuditEnum
{
    /**
     * 年度述职
     */
    SZ_TYPE_1(1),
    
    /**
     * 员工转正
     */
    ZZ_TYPE_2(2),
    
    /**
     * 审核步骤-部门
     */
    AUDIT_STEP_DEPT_1(1),
    
    /**
     * 审核步骤-人事
     */
    AUDIT_STEP_HR_2(2),
    
    /**
     * 审核步骤-总经理
     */
    AUDIT_STEP_GM_3(3),
    
    /**
     * 处理标识，待处理
     */
    AUDIT_HANDLE_NO(1),
    
    /**
     * 处理标识，已处理
     */
    AUDIT_HANDLE_OK(2);
    
    /**
     * 编号
     */
    private int id;
    
    AuditEnum(int id)
    {
        this.id = id;
    }
    
    public int getId()
    {
        return id;
    }
}