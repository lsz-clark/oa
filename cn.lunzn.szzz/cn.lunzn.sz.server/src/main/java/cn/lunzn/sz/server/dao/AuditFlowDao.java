package cn.lunzn.sz.server.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import cn.lunzn.model.common.AuditFlow;

/**
 * @author clark
 * 
 * 审核电子流
 */
@Mapper
public interface AuditFlowDao
{
    /**
     * 查询
     * @param param 请求参数
     * @return List<AuditFlow>
     */
    List<AuditFlow> find(AuditFlow param);
    
    /**
     * 查询总记录数
     * @param param 请求参数
     * @return int
     */
    int findTotal(AuditFlow param);
    
    /**
     * 添加
     * @param param 请求参数
     * @return int
     */
    int insert(AuditFlow param);
    
    /**
     * 更新
     * @param param 请求参数
     * @return int
     */
    int update(AuditFlow param);
    
    /**
     * 删除
     * @param param 请求参数
     * @return int
     */
    int delete(AuditFlow param);
}
