package cn.lunzn.common.exception;

import com.alibaba.fastjson.JSONObject;

import cn.lunzn.common.bean.BaseResponse;
import cn.lunzn.common.enums.CommonResultCode;

/**
 * @author clark
 * 自定义异常
 */
public class MyRuntimeException extends RuntimeException
{
    private static final long serialVersionUID = -2078744208308010329L;
    
    private Integer code = CommonResultCode.SERVICE_BUSINESS_601.getCode();
    
    private String msg = CommonResultCode.SERVICE_BUSINESS_601.getMsg();
    
    /** 
     * 默认构造函数
     */
    public MyRuntimeException()
    {
    }
    
    /** 
     * 构造函数
     * @param code 错误码
     * @param msg 消息
     */
    public MyRuntimeException(Integer code, String msg)
    {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
    
    /** 
     * 构造函数
     * @param code 错误码
     * @param msg 消息
     * @param paramThrowable 抛出异常
     */
    public MyRuntimeException(Integer code, String msg, Throwable paramThrowable)
    {
        super(msg, paramThrowable);
        this.code = code;
        this.msg = msg;
    }
    
    /** 
     * 构造函数
     * @param paramString 消息
     */
    public MyRuntimeException(String paramString)
    {
        super(paramString);
    }
    
    /** 
     * 构造函数
     * @param paramString 消息
     * @param paramThrowable 抛出异常
     */
    public MyRuntimeException(String paramString, Throwable paramThrowable)
    {
        super(paramString, paramThrowable);
    }
    
    /** 
     * 构造函数
     * @param paramThrowable 抛出异常
     */
    public MyRuntimeException(Throwable paramThrowable)
    {
        super(paramThrowable);
    }
    
    public Integer getCode()
    {
        return code;
    }
    
    public void setCode(Integer code)
    {
        this.code = code;
    }
    
    public String getMsg()
    {
        return msg;
    }
    
    public void setMsg(String msg)
    {
        this.msg = msg;
    }
    
    /** 
     * 返回
     * @return JSONObject
     * @see [类、类#方法、类#成员]
     */
    public JSONObject getJSONObject()
    {
        BaseResponse br = new BaseResponse();
        br.setCode(code);
        br.setMsg(msg);
        
        return JSONObject.parseObject(br.toString());
    }
}
