package cn.lunzn.zz.server.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import cn.lunzn.model.zz.ZZ;

/**
 * 员工转正Dao
 * 
 * @author  雷懿
 * @version  [版本号, 2017年8月30日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Mapper
public interface ZZDao
{
    /** 
     * 查询员工转正记录
     * @param param 查询条件
     * @return List<ZZ>
     * @see [类、类#方法、类#成员]
     */
    List<ZZ> find(ZZ param);
    
    /** 
     * 查询员工转正记录数
     * @param param 查询条件
     * @return int
     * @see [类、类#方法、类#成员]
     */
    int findTotal(ZZ param);
    
    /** 
     * 转正申请
     * @param param 转正信息
     * @return int
     * @see [类、类#方法、类#成员]
     */
    int insert(ZZ param);
    
    /** 
     * 修改转正
     * @param param 转正信息
     * @return int
     * @see [类、类#方法、类#成员]
     */
    int update(ZZ param);
}
