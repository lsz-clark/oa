package cn.lunzn.common.exception;

import com.alibaba.fastjson.JSONObject;

import cn.lunzn.common.bean.BaseResponse;
import cn.lunzn.common.enums.CommonResultCode;

/**
 * @author clark
 */
public class MyException extends Exception
{
    private static final long serialVersionUID = -1462948601340416275L;
    
    private Integer code = CommonResultCode.SERVER_BUSY_NOW.getCode();
    
    private String msg = CommonResultCode.SERVER_BUSY_NOW.getMsg();
    
    /** 
     * 默认构造函数
     */
    public MyException()
    {
    }
    
    /** 
     * 构造函数
     * @param code 错误码
     * @param msg 信息
     */
    public MyException(Integer code, String msg)
    {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
    
    /** 
     * 构造函数
     * @param code 错误码
     * @param msg 信息
     * @param paramThrowable 抛出异常
     */
    public MyException(Integer code, String msg, Throwable paramThrowable)
    {
        super(msg, paramThrowable);
        this.code = code;
        this.msg = msg;
    }
    
    /** 
     * 构造函数
     * @param paramString 信息
     */
    public MyException(String paramString)
    {
        super(paramString);
    }
    
    /** 
     * 构造函数
     * @param paramString 信息
     * @param paramThrowable 抛出异常
     */
    public MyException(String paramString, Throwable paramThrowable)
    {
        super(paramString, paramThrowable);
    }
    
    /** 
     * 构造函数
     * @param paramThrowable 抛出异常
     */
    public MyException(Throwable paramThrowable)
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
