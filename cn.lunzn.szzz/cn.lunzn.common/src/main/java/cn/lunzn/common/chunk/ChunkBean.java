package cn.lunzn.common.chunk;

import java.util.Calendar;
import java.util.Date;

import com.alibaba.fastjson.JSONObject;

import cn.lunzn.common.date.DateUtil;

/**
 * 业务分片（即多线程）处理bean
 * 
 * @author  shizeng.li
 * @version  [版本号, 2017年7月3日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class ChunkBean
{
    /**
     * 注释内容
     */
    public static final int CHUNK_SUCCESS = 0;
    
    /**
     * 业务异常返回码
     */
    public static final int BIZ_EXCEPTION = 1;
    
    /**
     * 线程取消返回码
     */
    public static final int HANDLER_CANCEL = 2;
    
    /**
     * 线程异常返回码
     */
    public static final int THREAD_EXCEPTION = 3;
    
    /**
     * 成功或失败返回码
     */
    private int returnCode = CHUNK_SUCCESS;
    
    /**
     * 成功或失败消息
     */
    private String returnMsg = "success";
    
    /**
     * 当前业务分片的主名称，无实际意义，可任意传
     */
    private String chunkName;
    
    /**
     * 当前分片编号
     */
    private int chunkNo;
    
    /**
     * 每个分片（即线程）处理数据的大小
     */
    private int chunkSize;
    
    /**
     * 当前分片开始时间
     */
    private Date startTime;
    
    /**
     * 当前分片结束时间
     */
    private Date endTime;
    
    /**
     * 参数
     */
    private JSONObject param;
    
    /**
     * 处理结果
     */
    private Object result;
    
    /** 
     * 分片名称
     * @return String
     * @see [类、类#方法、类#成员]
     */
    public String getChunkName()
    {
        return chunkName;
    }
    
    public void setChunkName(String chunkName)
    {
        this.chunkName = chunkName;
    }
    
    public int getChunkNo()
    {
        return chunkNo;
    }
    
    public void setChunkNo(int chunkNo)
    {
        this.chunkNo = chunkNo;
    }
    
    public int getChunkSize()
    {
        return chunkSize;
    }
    
    public void setChunkSize(int chunkSize)
    {
        this.chunkSize = chunkSize;
    }
    
    /** 
     * get开始时间
     * @return Date
     * @see [类、类#方法、类#成员]
     */
    public Date getStartTime()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        return calendar.getTime();
    }
    
    /** 
     * set开始时间
     * @param startTime 开始时间
     * @see [类、类#方法、类#成员]
     */
    public void setStartTime(Date startTime)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);
        
        this.startTime = calendar.getTime();
    }
    
    /** 
     * get结束时间
     * @return Date
     * @see [类、类#方法、类#成员]
     */
    public Date getEndTime()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endTime);
        return calendar.getTime();
    }
    
    /** 
     * set结束时间
     * @param endTime 结束时间
     * @see [类、类#方法、类#成员]
     */
    public void setEndTime(Date endTime)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endTime);
        this.endTime = calendar.getTime();
    }
    
    public JSONObject getParam()
    {
        return param;
    }
    
    public void setParam(JSONObject param)
    {
        this.param = param;
    }
    
    public int getReturnCode()
    {
        return returnCode;
    }
    
    public void setReturnCode(int returnCode)
    {
        this.returnCode = returnCode;
    }
    
    public String getReturnMsg()
    {
        return returnMsg;
    }
    
    public void setReturnMsg(String returnMsg)
    {
        this.returnMsg = returnMsg;
    }
    
    public Object getResult()
    {
        return result;
    }
    
    public void setResult(Object result)
    {
        this.result = result;
    }
    
    /** 
     * 成功true、失败false
     * @return boolean
     * @see [类、类#方法、类#成员]
     */
    public boolean isSuccess()
    {
        return this.returnCode == CHUNK_SUCCESS;
    }
    
    @Override
    public String toString()
    {
        StringBuffer log = new StringBuffer();
        log.append('[');
        log.append(chunkName);
        log.append(']');
        log.append(" - ");
        log.append(chunkNo);
        log.append(" - ");
        log.append(DateUtil.formatDateToString(DateUtil.DATE_FORMAT_MILLI_SECOND_BAR, startTime));
        log.append('~');
        log.append(DateUtil.formatDateToString(DateUtil.DATE_FORMAT_MILLI_SECOND_BAR, endTime));
        log.append(", returnCode=");
        log.append(returnCode);
        log.append(", returnMsg=");
        log.append(returnMsg);
        return log.toString();
    }
    
    /** 
     * 当前切片开始日志
     * @return String
     * @see [类、类#方法、类#成员]
     */
    public String getStartLog()
    {
        StringBuffer log = new StringBuffer();
        log.append('[');
        log.append(chunkName);
        log.append(']');
        log.append(" - ");
        log.append(chunkNo);
        log.append(" - ");
        log.append(DateUtil.formatDateToString(DateUtil.DATE_FORMAT_MILLI_SECOND_BAR, startTime));
        log.append(" begin.");
        return log.toString();
    }
    
    /** 
     * 当前切片结束日志
     * @return String
     * @see [类、类#方法、类#成员]
     */
    public String getEndLog()
    {
        StringBuffer log = new StringBuffer();
        log.append('[');
        log.append(chunkName);
        log.append(']');
        log.append(" - ");
        log.append(chunkNo);
        log.append(" - ");
        log.append(DateUtil.formatDateToString(DateUtil.DATE_FORMAT_MILLI_SECOND_BAR, endTime));
        log.append(" end, returnCode=");
        log.append(returnCode);
        log.append(", returnMsg=");
        log.append(returnMsg);
        return log.toString();
    }
}
