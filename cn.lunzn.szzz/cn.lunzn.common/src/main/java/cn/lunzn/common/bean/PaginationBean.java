package cn.lunzn.common.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 分页bean
 * 
 * @author  clark
 * @version  [版本号, 2017年7月11日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class PaginationBean
{
    /**
     * 默认一页显示10条
     */
    private static final Integer DEFAULT_PAGE_SIZE = 10;
    
    /**
     * 页大小
     */
    private Integer pageSize;
    
    /**
     * 偏移量
     */
    private Integer offset;
    
    /**
     * 当前页
     */
    @JSONField(serialize = false)
    private Integer pageIndex;
    
    public Integer getPageSize()
    {
        return pageSize;
    }
    
    public void setPageSize(Integer pageSize)
    {
        this.pageSize = pageSize;
    }
    
    public Integer getOffset()
    {
        return offset;
    }
    
    public void setOffset(Integer offset)
    {
        this.offset = offset;
    }
    
    public Integer getPageIndex()
    {
        return pageIndex;
    }
    
    public void setPageIndex(Integer pageIndex)
    {
        this.pageIndex = pageIndex;
    }
    
    /** 
     * 设置分页
     * @param pageBean 参数
     * @return PaginationBean
     * @see [类、类#方法、类#成员]
     */
    public static PaginationBean getPagination(PaginationBean pageBean)
    {
        PaginationBean getPage = new PaginationBean();
        
        if (null != pageBean)
        {
            getPage.setPageIndex(pageBean.getPageIndex());
            getPage.setPageSize(pageBean.getPageSize());
        }
        else
        {
            // 默认
            getPage.setPageIndex(1);
            getPage.setPageSize(DEFAULT_PAGE_SIZE);
        }
        getPage.setOffset((getPage.getPageIndex() - 1) * getPage.getPageSize());
        
        return getPage;
    }
}
