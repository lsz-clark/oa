package cn.lunzn.sz.server.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import cn.lunzn.model.sz.SZ;

/**
 * 年度述职Dao
 * 
 * @author  杜浩
 * @version  [版本号, 2017年8月22日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Mapper
public interface SZDao
{
    /**
     * 查询用户
     * @param sz 述职信息
     * @return List<UserInfo>
     */
    List<SZ> findSZ(SZ sz);
    
    /**
     * 查询用户总记录数
     * @param sz 述职信息
     * @return int
     */
    int findSZTotal(SZ sz);
    
    /**
     * 保存述职信息
     * @param sz 述职信息
     * @return int
     */
    int addSZ(SZ sz);
    
    /**
     * 修改述职信息
     * @param sz 述职信息
     * @return int
     */
    int updateSZ(SZ sz);
    
    /**
     * 修改述职状态
     * @param sz 述职信息
     * @return int
     */
    int updateSZState(SZ sz);
    
    /**
     * 查询驳回次数
     * @param sz 述职信息
     * @return int
     */
    int queryRejectTime(SZ sz);
    
    /**
     * 查询当前年份，当前员工提交述职记录数
     * @param sz 述职信息
     * @return int
     */
    int querySubmitTime(SZ sz);
    
}
