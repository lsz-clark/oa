package cn.lunzn.common.constant;

/**
 * @author clark
 * 默认接口地址
 */
public class DefaultURI
{
    /**
     * 查询
     */
    public static final String URI_QUERY = "query";
    
    /**
     * 查询数量
     */
    public static final String URI_QUERY_TOTAL = "querytotal";
    
    /**
     * 添加
     */
    public static final String URI_ADD = "add";
    
    /**
     * 更新
     */
    public static final String URI_UPDATE = "update";
    
    /**
     * 删除
     */
    public static final String URI_DELETE = "delete";
    
    /**
     * 更新状态
     */
    public static final String URI_UPDATE_STATE = "updateState";
    
    /**
     * 查询驳回次数
     */
    public static final String URI_REJECT_TIME = "queryRejectTime";
    
    /**
     * 查询述职提交次数
     */
    public static final String URI_SUBMIT_TIME = "querySubmitTime";
    
    /**
     * 删除
     */
    public static final String URI_DEL = "del";
    
    /** 
     * 受保护的构造函数，静态类无需共有构造函数
     */
    protected DefaultURI()
    {
        
    }
    
    /**
     * 审核电子流接口地址 key
     * @param uriSuffix 后缀
     * @return String
     */
    public static String getAuditFlowURIKey(String uriSuffix)
    {
        return "auditflow." + uriSuffix;
    }
    
    /**
     * 年度述职接口地址 key
     * @param uriSuffix 后缀
     * @return String
     */
    public static String getReportWorkURIKey(String uriSuffix)
    {
        return "report." + uriSuffix;
    }
    
    /**
     * 员工转正接口地址 key
     * @param uriSuffix 后缀
     * @return String
     */
    public static String getZZURIKey(String uriSuffix)
    {
        return "zz." + uriSuffix;
    }
}
