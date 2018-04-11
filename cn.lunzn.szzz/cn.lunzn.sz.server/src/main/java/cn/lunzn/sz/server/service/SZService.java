package cn.lunzn.sz.server.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lunzn.model.sz.SZ;
import cn.lunzn.sz.server.dao.SZDao;

/**
 * @author clark
 * 用户业务类
 */
@Service
public class SZService
{
    @Autowired
    private SZDao szDao;
    
    /**
     * 查询用户
     * @param sz 述职信息
     * @return List<UserInfo>
     */
    public List<SZ> getSZ(SZ sz)
    {
        return szDao.findSZ(sz);
    }
    
    /**
     * 查询用户总记录数
     * @param sz 述职信息
     * @return int
     */
    public int getSZTotal(SZ sz)
    {
        return szDao.findSZTotal(sz);
    }
    
    /**
     * 保存述职信息
     * @param sz 述职信息
     * @return int
     */
    public int saveSZ(SZ sz)
    {
        if (sz.getSzId() != null && sz.getSzId() > 0)
        {
            // 更新
            return szDao.updateSZ(sz);
        }
        else
        {
            // 新增
            return szDao.addSZ(sz);
        }
    }
    
    /**
     * 更新述职状态
     * @param sz 述职信息
     * @return int
     */
    public int updateSZState(SZ sz)
    {
        //更新
        return szDao.updateSZState(sz);
    }
    
    /**
     * 查询驳回次数
     * @param sz 述职信息
     * @return int
     */
    public int queryRejectTime(SZ sz)
    {
        return szDao.queryRejectTime(sz);
    }
    
    /**
     * 查询当前年份，当前员工提交述职记录数
     * @param sz 述职信息
     * @return int
     */
    public int querySubmitTime(SZ sz)
    {
        return szDao.querySubmitTime(sz);
    }
    
}
