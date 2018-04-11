package cn.lunzn.common.chunk;

import com.alibaba.fastjson.JSONObject;

/**
 * 默认分片计算类
 * 
 * @author  shizeng.li
 * @version  [版本号, 2017年7月3日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class ChunkHandler
{
    /**
     * 默认每片大小
     */
    private static final int DEFAULT_CHUNK_SIZE = 5000;
    
    /**
     * 业务的总数据量
     */
    private long dataTotal = 0;
    
    /**
     * 每片处理数据量的大小，默认每片处理5000条数据
     */
    private int chunkSize = DEFAULT_CHUNK_SIZE;
    
    /**
     * 当前第几片
     */
    private int chunkIndex = 0;
    
    /**
     * 分片方法的实现类
     */
    private IChunk chunkInstance;
    
    /**
     * 参数
     */
    private JSONObject param;
    
    /** 
     * 默认构造函数
     * @param dataTotal 总数据量
     * @param chunkInstance 切片实例
     * @param param 参数
     */
    public ChunkHandler(long dataTotal, IChunk chunkInstance, JSONObject param)
    {
        this.dataTotal = dataTotal;
        this.chunkInstance = chunkInstance;
        this.param = param;
    }
    
    /** 
     * 默认构造函数
     * @param dataTotal 总数据量
     * @param chunkSize 每片大小
     * @param chunkInstance 切片实例
     * @param param 参数
     */
    public ChunkHandler(long dataTotal, int chunkSize, IChunk chunkInstance, JSONObject param)
    {
        this.dataTotal = dataTotal;
        this.chunkSize = chunkSize;
        this.chunkInstance = chunkInstance;
        this.param = param;
    }
    
    /** 
     * 是否还有下一片
     * @return boolean
     * @see [类、类#方法、类#成员]
     */
    public boolean hasNextChunk()
    {
        return dataTotal > chunkIndex * chunkSize;
    }
    
    /** 
     * 下一片
     * @return IChunk
     * @see [类、类#方法、类#成员]
     */
    public IChunk nextChunk()
    {
        this.chunkIndex++;
        return this.chunkInstance;
    }
    
    /** 
     * 当前切片索引
     * @return int
     * @see [类、类#方法、类#成员]
     */
    public int getChunkIndex()
    {
        return this.chunkIndex;
    }
    
    /** 
     * 当前切片大小
     * @return int
     * @see [类、类#方法、类#成员]
     */
    public int getChunkSize()
    {
        return this.chunkSize;
    }
    
    /** 
     * 参数
     * @return Map<String, Object>
     * @see [类、类#方法、类#成员]
     */
    public JSONObject getParam()
    {
        return this.param;
    }
    
    /** 
     * 设置参数
     * @param param 参数
     * @see [类、类#方法、类#成员]
     */
    public void setParam(JSONObject param)
    {
        this.param = param;
    }
    
}
