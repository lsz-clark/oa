package cn.lunzn.common.chunk;

/**
 * 切片自定义异常
 * 
 * @author  shizeng.li
 * @version  [版本号, 2017年7月3日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class ChunkException extends Exception
{
    /**
     * 序列化版本编号
     */
    private static final long serialVersionUID = -754538451412345921L;
    
    /**
     * 分片异常个数
     */
    private int exceptionCounter;
    
    /**
     * 总分片数
     */
    private int chunkCounter;
    
    /** 
     * 默认构造函数
     */
    public ChunkException()
    {
        super();
    }
    
    /** 
     * 构造函数
     * @param chunkCounter 切片数
     * @param exceptionCounter 异常数
     * @param message 消息
     */
    public ChunkException(int chunkCounter, int exceptionCounter, String message)
    {
        super(message);
        this.chunkCounter = chunkCounter;
        this.exceptionCounter = exceptionCounter;
    }
    
    /** 
     * 构造函数
     * @param message 消息
     */
    public ChunkException(String message)
    {
        super(message);
    }
    
    /** 
     * 构造函数
     * @param cause 异常
     */
    public ChunkException(Throwable cause)
    {
        super(cause);
    }
    
    /** 
     * 构造函数
     * @param message 消息
     * @param cause 异常
     */
    public ChunkException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public int getExceptionCounter()
    {
        return exceptionCounter;
    }
    
    public void setExceptionCounter(int exceptionCounter)
    {
        this.exceptionCounter = exceptionCounter;
    }
    
    public int getChunkCounter()
    {
        return chunkCounter;
    }
    
    public void setChunkCounter(int chunkCounter)
    {
        this.chunkCounter = chunkCounter;
    }
}
