package cn.lunzn.common.bean;

import java.io.Serializable;

import com.alibaba.fastjson.JSON;

/**
 * @author clark
 * 父bean
 */
public class BaseBean implements Serializable
{
    private static final long serialVersionUID = -733107688107717742L;
    
    /**
     * 用于组合查询
     */
    private String combiField;
    
    public String getCombiField()
    {
        return combiField;
    }
    
    public void setCombiField(String combiField)
    {
        this.combiField = combiField;
    }
    
    @Override
    public String toString()
    {
        return JSON.toJSONString(this);
    }
}
