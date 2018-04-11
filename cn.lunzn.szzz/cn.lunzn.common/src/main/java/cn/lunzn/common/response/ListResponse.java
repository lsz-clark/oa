package cn.lunzn.common.response;

import java.util.List;

import cn.lunzn.common.bean.BaseResponse;

/**
 * @author clark
 * 查询时响应的对象
 *
 * @param <T> 泛型
 */
public class ListResponse<T> extends BaseResponse
{
    private static final long serialVersionUID = -7748147003259465765L;
    
    /**
     * 相关结果
     */
    private List<T> result;
    
    /**
     * 总记录数
     */
    private int total;
    
    public List<T> getResult()
    {
        return result;
    }
    
    public void setResult(List<T> result)
    {
        this.result = result;
    }
    
    public int getTotal()
    {
        return total;
    }
    
    public void setTotal(int total)
    {
        this.total = total;
    }
    
}
