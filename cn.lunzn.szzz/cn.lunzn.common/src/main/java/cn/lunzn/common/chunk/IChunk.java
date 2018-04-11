package cn.lunzn.common.chunk;

import cn.lunzn.common.exception.MyException;

/**
 * 分片（即多线程）接口
 * 
 * @author  shizeng.li
 * @version  [版本号, 2017年7月3日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public interface IChunk
{
    /** 
     * 分片处理方法
     * @param chunkInfo 分片信息bean
     * @throws MyException 管理台自定义异常
     * @see [类、类#方法、类#成员]
     */
    void handle(ChunkBean chunkInfo)
        throws MyException;
}
