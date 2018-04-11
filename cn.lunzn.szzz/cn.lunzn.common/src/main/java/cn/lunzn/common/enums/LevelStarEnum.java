package cn.lunzn.common.enums;

/**
 * 等级评定枚举
 * @author  雷懿
 */
public enum LevelStarEnum
{
    /**
     * 1 不合格
     */
    UNQUALIFIED(1, "不合格"),
    
    /**
     * 2 需改进
     */
    TOBEIMPROVED(2, "需改进"),
    
    /**
     * 3 合格
     */
    QUALIFIED(3, "合格"),
    
    /**
     * 4 良好
     */
    GOOD(4, "良好"),
    
    /**
     * 5 优秀
     */
    EXCELLENT(5, "优秀");
    
    /**
     * 等级值
     */
    private int level;
    
    /**
     * 等级名称
     */
    private String text;
    
    private LevelStarEnum(int level, String text)
    {
        this.level = level;
        this.text = text;
    }
    
    public int getLevel()
    {
        return level;
    }
    
    public String getText()
    {
        return text;
    }
    
    /**
     * 得到评定等级
     * @param level 等级值
     * @return 等级枚举
     */
    public static LevelStarEnum getLevel(int level)
    {
        for (LevelStarEnum l : LevelStarEnum.values())
        {
            if (l.getLevel() == level)
            {
                return l;
            }
        }
        return null;
    }
    
    /**
     * 获取等级名称
     * @param level 等级值
     * @return 等级名称
     */
    public static String getText(int level)
    {
        for (LevelStarEnum l : LevelStarEnum.values())
        {
            if (l.getLevel() == level)
            {
                return l.text;
            }
        }
        return null;
    }
    
    /**
     * 获取等级值
     * @param text 等级名称
     * @return 等级值
     */
    public static int getIndex(String text)
    {
        for (LevelStarEnum l : LevelStarEnum.values())
        {
            if (l.getText().equals(text))
            {
                return l.level;
            }
        }
        return -1;
    }
}
