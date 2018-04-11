package cn.lunzn.model.user;

import java.io.Serializable;

/**
 * 用户角色实体类
 * 
 * @author  yi.li
 * @version  [版本号, 2017年8月10日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class UserRole implements Serializable
{
    /**
     * 序列化
     */
    private static final long serialVersionUID = -1842341589722767885L;
    
    /**
     * 角色编号
     */
    private Integer roleId;
    
    /**
     * 角色名称
     */
    private String roleName;
    
    public Integer getRoleId()
    {
        return roleId;
    }
    
    public void setRoleId(Integer roleId)
    {
        this.roleId = roleId;
    }
    
    public String getRoleName()
    {
        return roleName;
    }
    
    public void setRoleName(String roleName)
    {
        this.roleName = roleName;
    }
}
