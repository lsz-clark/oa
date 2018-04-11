package cn.lunzn.common.enums;

/**
 * 转正类型枚举
 * @author  雷懿
 */
public enum ZzTypeEnum
{
    /**
     * 1 按期转正
     */
    UNQUALIFIED(1, "按期转正"),
    
    /**
     * 2 提前转正
     */
    TOBEIMPROVED(2, "提前转正"),
    
    /**
     * 3 延迟转正
     */
    QUALIFIED(3, "延迟转正"),
    
    /**
     * 4 辞退
     */
    GOOD(4, "辞退"),
    
    /**
     * 5 转岗
     */
    EXCELLENT(5, "转岗");
    
    /**
     * 类型
     */
    private int index;
    
    /**
     * 类型名称
     */
    private String name;
    
    private ZzTypeEnum(int index, String name)
    {
        this.index = index;
        this.name = name;
    }
    
    public int getIndex()
    {
        return index;
    }
    
    public String getName()
    {
        return name;
    }
    
    /**
     * 得到评定等级
     * @param index 等级值
     * @return 等级枚举
     */
    public static ZzTypeEnum getType(int index)
    {
        for (ZzTypeEnum l : ZzTypeEnum.values())
        {
            if (l.getIndex() == index)
            {
                return l;
            }
        }
        return null;
    }
    
    /**
     * 获取等级名称
     * @param index 等级值
     * @return 等级名称
     */
    public static String getName(int index)
    {
        for (ZzTypeEnum l : ZzTypeEnum.values())
        {
            if (l.getIndex() == index)
            {
                return l.name;
            }
        }
        return null;
    }
    
    /**
     * 获取等级值
     * @param name 等级名称
     * @return 等级值
     */
    public static int getIndex(String name)
    {
        for (ZzTypeEnum l : ZzTypeEnum.values())
        {
            if (l.getName().equals(name))
            {
                return l.index;
            }
        }
        return -1;
    }
}
