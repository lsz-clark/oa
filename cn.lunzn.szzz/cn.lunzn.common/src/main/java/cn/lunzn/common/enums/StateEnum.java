package cn.lunzn.common.enums;

/**
 * 上线状态、下线状态、审核状态枚举
 * @author shizeng.li
 * @version  [版本号, 2017年03月09日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public enum StateEnum
{
    /**
     * 审核状态-编辑中
     */
    AUDIT_STATE_EDITING_1(1, "编辑中"),
    
    /**
     * 审核状态-审核中
     */
    AUDIT_STATE_AUDITING_2(2, "审核中"),
    
    /**
     * 审核状态-审核通过
     */
    AUDIT_STATE_APPROVE_3(3, "审核通过"),
    
    /**
     * 审核状态-驳回
     */
    AUDIT_STATE_REJECT_4(4, "驳回"),
    
    /**
     * 电子流审核-通过
     */
    FLOW_STATE_PASS_1(1, "通过"),
    
    /**
     * 电子流审核-驳回
     */
    FLOW_STATE_REJECT_2(2, "驳回");
    
    /**
     * 编号
     */
    private int id;
    
    /**
     *名称
     */
    private String name;
    
    StateEnum(int id, String name)
    {
        this.id = id;
        this.name = name;
    }
    
    public int getId()
    {
        return id;
    }
    
    public String getName()
    {
        return name;
    }
}