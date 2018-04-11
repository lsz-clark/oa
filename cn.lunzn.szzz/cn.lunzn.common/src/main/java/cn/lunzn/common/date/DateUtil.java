package cn.lunzn.common.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.lunzn.common.exception.MyRuntimeException;

/**
 * 管理台日期处理工具类
 * <功能详细描述>
 * 
 * @author  lunzn
 * @version  [版本号, 2017年4月5日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public abstract class DateUtil
{
    /**
     * 到月的时间格式
     */
    public static final String DATE_FORMAT_MONTH_BAR = "yyyy-MM";
    
    /**
     * 到天的时间格式
     */
    public static final String DATE_FORMAT_DAY_BAR = "yyyy-MM-dd";
    
    /**
     * 到分钟的时间格式
     */
    public static final String DATE_FORMAT_MINUTE_BAR = "yyyy-MM-dd HH:mm";
    
    /**
     * 到秒的时间格式
     */
    public static final String DATE_FORMAT_SECOND_BAR = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * 到毫秒的时间格式
     */
    public static final String DATE_FORMAT_MILLI_SECOND_BAR = "yyyy-MM-dd HH:mm:ss:SSS";
    
    // private static final String DATE_FORMAT_MONTH_NOBAR = "yyyyMM";
    
    /**
     * 没有横杆到天的时间格式
     */
    private static final String DATE_FORMAT_DAY_NOBAR = "yyyyMMdd";
    
    /**
     * 拿到当天的日期
     * <功能详细描述>
     * @return 当前系统日期
     * @see [类、类#方法、类#成员]
     */
    public static String getCurrentDate()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }
    
    /**
     * 按指定格式获取当前日期
     * <功能详细描述>
     * @param para 日期格式
     * @return 按指定格式获取的当前日期
     * @see [类、类#方法、类#成员]
     */
    public static String getCurrentDate(String para)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(para);
        return sdf.format(new Date());
    }
    
    /** 
     * 格式化时间
     * <功能详细描述>
     * @param format 格式
     * @param value 日期
     * @return 格式化后的日期
     * @see [类、类#方法、类#成员]
     */
    public static String formatData(String format, Object value)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(value);
    }
    
    /**
     * 获取当前时间
     * <功能详细描述>
     * @return 当前时间yyyyMMddHHmmss
     * @see [类、类#方法、类#成员]
     */
    public static String getCurrentTime()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date());
    }
    
    /** 
     * 格式化时间(Date to String)
     * @param format 格式
     * @param value 日期
     * @return 日期字符串
     * @see [类、类#方法、类#成员]
     */
    public static String formatDateToString(String format, Date value)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(value);
    }
    
    /** 
     * 格式化时间(Date to String)
     * yyyy-MM-dd
     * @param value 日期
     * @return  String
     * @see [类、类#方法、类#成员]
     */
    public static String formatDateToString(Date value)
    {
        return formatDateToString(DATE_FORMAT_DAY_BAR, value);
    }
    
    /** 
     * 格式化时间(Date to String)
     * yyyyMMdd
     * @param value 日期
     * @return 日期字符串
     * @see [类、类#方法、类#成员]
     */
    public static String formatDateToYYYYMMDD(Date value)
    {
        return formatDateToString(DATE_FORMAT_DAY_NOBAR, value);
    }
    
    /** 
     * 格式化时间(Object to Date)
     * 
     * @param format 格式
     * @param value 日期
     * @return 日期
     * @see [类、类#方法、类#成员]
     */
    public static Date formatStringToDate(String format, Object value)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        
        String dateStr = value.toString();
        
        Date date = null;
        try
        {
            date = sdf.parse(dateStr);
        }
        catch (ParseException pe)
        {
            throw new MyRuntimeException("msg:格式化时间异常，时间为：" + dateStr, pe);
        }
        
        return date;
    }
    
    /** 
     * 将20161209字符串 格式化为 日期格式
     * <功能详细描述>
     * @param value 日期
     * @return Date日期
     * @see [类、类#方法、类#成员]
     */
    public static Date formatStringToDate(Object value)
    {
        return formatStringToDate(DATE_FORMAT_DAY_NOBAR, value);
    }
    
    /** 
     * 将字符串2016-12-09 17:47:30 格式化为 日期格式
     * 
     * @param value 日期格式为：yyyyMMdd
     * @return Date
     * @see [类、类#方法、类#成员]
     */
    public static Date formatBarStringSecondToDate(Object value)
    {
        return formatStringToDate(DATE_FORMAT_SECOND_BAR, value);
    }
    
    /** 
     * 字符串20161209 转换成 字符串2016-12-09
     * 
     * @param value 日期格式为：yyyyMMdd
     * @return String
     * @see [类、类#方法、类#成员]
     */
    public static String formatDateStrToBarDate(Object value)
    {
        return formatDateToString(formatStringToDate(DATE_FORMAT_DAY_NOBAR, value));
    }
    
    /** 
     * 将毫秒的日期转换成日期
     * 
     * 
     * @param datetime 毫秒日期格式
     * @return String
     * @see [类、类#方法、类#成员]
     */
    public static Date formatDatetimeToDate(Object datetime)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(datetime.toString()));
        return calendar.getTime();
    }
    
    /** 
     * 将毫秒的日期转换成自定义格式
     * 
     * @param format 自选格式
     * @param datetime 毫秒日期格式
     * @return String
     * @see [类、类#方法、类#成员]
     */
    public static String formatDatetimeToBarDate(String format, Object datetime)
    {
        return formatDateToString(format, formatDatetimeToDate(datetime));
    }
    
    /** 
     * 将毫秒的日期转换成yyyy-MM-dd HH:mm格式
     * 
     * @param datetime 时间
     * @return String
     * @see [类、类#方法、类#成员]
     */
    public static String formatDatetimeToBarDateMinute(Object datetime)
    {
        return formatDatetimeToBarDate(DATE_FORMAT_MINUTE_BAR, datetime);
    }
    
    /** 
     * 将毫秒的日期转换成yyyy-MM-dd HH:mm:ss格式
     * 
     * @param datetime 时间
     * @return String
     * @see [类、类#方法、类#成员]
     */
    public static String formatDatetimeToBarDateSecond(Object datetime)
    {
        return formatDatetimeToBarDate(DATE_FORMAT_SECOND_BAR, datetime);
    }
}
