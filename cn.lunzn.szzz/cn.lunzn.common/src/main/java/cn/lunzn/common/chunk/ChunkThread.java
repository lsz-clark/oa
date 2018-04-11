package cn.lunzn.common.chunk;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.lunzn.common.exception.MyException;

/**
 * 线程实现类
 * 
 * @author  shizeng.li
 * @version  [版本号, 2017年7月3日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class ChunkThread implements Callable<ChunkBean>
{
    /**
     * 日志记录
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ChunkThread.class);
    
    /**
     * 当前切片回调处理方法对象
     */
    private IChunk chunk;
    
    /**
     * 当前切片信息
     */
    private ChunkBean chunkInfo;
    
    /**
     * 出现异常是否停止后面操作
     */
    private boolean exceptionCancel;
    
    /**
     * 注释内容
     */
    private AtomicBoolean hasException;
    
    /** 
     * 默认构造函数
     * @param chunk 切片
     * @param chunkInfo 切片信息
     */
    public ChunkThread(IChunk chunk, ChunkBean chunkInfo)
    {
        this(chunk, chunkInfo, false, new AtomicBoolean(false));
    }
    
    /** 
     * 默认构造函数
     * @param chunk 切片
     * @param chunkInfo 切片信息
     * @param exceptionCancel 异常是否取消
     * @param hasException 是否有异常
     */
    public ChunkThread(IChunk chunk, ChunkBean chunkInfo, boolean exceptionCancel, AtomicBoolean hasException)
    {
        this.chunk = chunk;
        this.chunkInfo = chunkInfo;
        this.exceptionCancel = exceptionCancel;
        this.hasException = hasException;
    }
    
    @Override
    public ChunkBean call()
    {
        chunkInfo.setStartTime(new Date());
        LOGGER.info(chunkInfo.getStartLog());
        
        try
        {
            // 如果设置了异常取消并且某切片出现了异常，那么后面所有的切片不再执行
            if (exceptionCancel && hasException.get())
            {
                chunkInfo.setReturnCode(ChunkBean.HANDLER_CANCEL);
                chunkInfo.setReturnMsg("cancel");
            }
            else
            {
                // 调用业务方法
                chunk.handle(chunkInfo);
            }
        }
        catch (MyException oe)
        {
            // 出现异常
            hasException.set(true);
            chunkInfo.setReturnCode(ChunkBean.BIZ_EXCEPTION);
            chunkInfo.setReturnMsg(oe.getMessage());
            
            chunkInfo.setEndTime(new Date());
            LOGGER.error(chunkInfo.getEndLog());
        }
        
        chunkInfo.setEndTime(new Date());
        LOGGER.info(chunkInfo.getEndLog());
        return chunkInfo;
    }
    
}
