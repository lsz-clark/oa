package cn.lunzn.common.chunk;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 分片启动类
 * <br>
 * 请根据服务器的配置，合理配置线程池与每个切片的大小，已达到最高效率
 * @author  shizeng.li
 * @version  [版本号, 2017年7月3日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class ChunkJob
{
    /**
     * 默认休眠时间-s
     */
    private static final int DEFAULT_SLEEP_TIME = 1000;
    
    /**
     * 日志记录
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ChunkJob.class);
    
    /**
     * 默认线程池大小
     */
    private static final int DEFAULT_THREAD_POOL_SIZE = 30;
    
    /** 
     * 多线程执行[同步等待]，统计并抛异常
     * @param chunkName 切片名称
     * @param chunkHandler 业务对象
     * @param exceptionCancel 异常是否继续
     * 
     * @throws ChunkException 切片异常
     * @see [类、类#方法、类#成员]
     */
    public void execChunkThrow(String chunkName, ChunkHandler chunkHandler, boolean exceptionCancel)
        throws ChunkException
    {
        LOGGER.info("[chunk] ready to begin...");
        // 开始多线程处理业务
        List<ChunkBean> chunkInfoList = exec(chunkName, chunkHandler, exceptionCancel);
        
        // 处理完成，统计结果
        int exceptionTotal = printStatLog(chunkName, chunkInfoList);
        
        // 不成功将抛出异常，业务需要自己处理
        if (exceptionTotal > 0)
        {
            StringBuffer message = new StringBuffer();
            message.append("[chunk] chunk name: ").append(chunkName);
            message.append(" - chunk total: ").append(chunkInfoList.size());
            message.append(" - exception total: ").append(exceptionTotal);
            
            throw new ChunkException(chunkInfoList.size(), exceptionTotal, message.toString());
        }
    }
    
    /** 
     * 多线程执行[同步等待]，不统计不抛异常，仅返回处理结果
     * @param chunkName 切片名称
     * @param chunkHandler 业务对象
     * @param exceptionCancel 异常是否继续
     * @return List<ChunkInfo>
     * @see [类、类#方法、类#成员]
     */
    public List<ChunkBean> execChunk(String chunkName, ChunkHandler chunkHandler, boolean exceptionCancel)
    {
        // 开始多线程处理业务
        List<ChunkBean> chunkInfoList = exec(chunkName, chunkHandler, exceptionCancel);
        
        // 处理完成，统计结果
        printStatLog(chunkName, chunkInfoList);
        
        return chunkInfoList;
    }
    
    /** 
     * 多线程执行[同步等待]，不统计不抛异常，仅返回处理结果
     * @param chunkName 切片名称
     * @param chunkHandler 业务对象
     * @param exceptionCancel 异常是否继续
     * @return List<ChunkInfo>
     * @see [类、类#方法、类#成员]
     */
    private List<ChunkBean> exec(String chunkName, ChunkHandler chunkHandler, boolean exceptionCancel)
    {
        LOGGER.info("[chunk] ready to begin...");
        
        // 实例化一个默认线程池
        ExecutorService pool = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);
        List<ChunkBean> chunkInfoList = new ArrayList<ChunkBean>();
        try
        {
            // 实例化一个批处理任务
            CompletionService<ChunkBean> chunkCompletion = new ExecutorCompletionService<ChunkBean>(pool);
            
            List<Future<ChunkBean>> futures = new ArrayList<Future<ChunkBean>>();
            
            int chunkCounter = 0;
            AtomicBoolean hasException = new AtomicBoolean(false);
            
            // 开始切片
            while (chunkHandler.hasNextChunk())
            {
                // 循环分片处理
                ChunkBean chunkInfo = new ChunkBean();
                chunkInfo.setChunkName(chunkName);
                chunkInfo.setChunkNo(chunkCounter);
                chunkInfo.setChunkSize(chunkHandler.getChunkSize());
                chunkInfo.setParam(chunkHandler.getParam());
                
                IChunk chunk = chunkHandler.nextChunk();
                // 提交给Future，后面通过take\get获取处理结果
                futures.add(chunkCompletion.submit(new ChunkThread(chunk, chunkInfo, exceptionCancel, hasException)));
                
                chunkCounter++;
            }
            
            LOGGER.debug("[chunk] Future<ChunkBean> size :{}", futures.size());
            
            try
            {
                Thread.sleep(DEFAULT_SLEEP_TIME);
            }
            catch (InterruptedException e)
            {
                LOGGER.error("[chunk] Thread sleep exception...");
            }
            
            ChunkBean chunkInfo = null;
            
            for (int i = 0; i < chunkCounter; i++)
            {
                try
                {
                    chunkInfo = chunkCompletion.take().get();
                }
                catch (InterruptedException ie)
                {
                    LOGGER.error("[chunk] future.get() Interrupted Exception...chunk no: {}", i);
                    chunkInfo = new ChunkBean();
                    chunkInfo.setChunkName(chunkName);
                    chunkInfo.setChunkNo(i);
                    Date now = new Date();
                    chunkInfo.setStartTime(now);
                    chunkInfo.setEndTime(now);
                    chunkInfo.setReturnCode(ChunkBean.THREAD_EXCEPTION);
                    chunkInfo.setReturnMsg(ie.getMessage());
                }
                catch (ExecutionException ee)
                {
                    ee.printStackTrace();
                    LOGGER.error("[chunk] future.get() Execution Exception...chunk no: {}", i);
                    chunkInfo = new ChunkBean();
                    chunkInfo.setChunkName(chunkName);
                    chunkInfo.setChunkNo(i);
                    Date now = new Date();
                    chunkInfo.setStartTime(now);
                    chunkInfo.setEndTime(now);
                    chunkInfo.setReturnCode(ChunkBean.THREAD_EXCEPTION);
                    chunkInfo.setReturnMsg(ee.getMessage());
                }
                
                chunkInfoList.add(chunkInfo);
            }
        }
        finally
        {
            // 线程池关闭
            pool.shutdown();
        }
        return chunkInfoList;
    }
    
    /** 
     * 打印统计日志
     * @param chunkInfoList 执行结果
     * @return int 
     * @see [类、类#方法、类#成员]
     */
    private int printStatLog(String chunkName, List<ChunkBean> chunkInfoList)
    {
        // 处理完成，统计结果
        int exceptionTotal = 0;
        for (ChunkBean chunkInfo : chunkInfoList)
        {
            // 不成功打印error日志
            if (chunkInfo.getReturnCode() != ChunkBean.CHUNK_SUCCESS)
            {
                LOGGER.error(chunkInfo.toString());
                exceptionTotal++;
            }
            else
            {
                // 成功打印info日志
                LOGGER.info(chunkInfo.toString());
            }
        }
        
        // 打印统计日志
        StringBuffer message = new StringBuffer();
        message.append("[chunk] chunk name: ").append(chunkName);
        message.append(" - chunk total: ").append(chunkInfoList.size());
        message.append(" - exception total: ").append(exceptionTotal);
        LOGGER.error(message.toString());
        
        return exceptionTotal;
    }
}
