package cn.lunzn.sz.server.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import cn.lunzn.common.constant.ResultCodeJson;
import cn.lunzn.common.response.ListResponse;
import cn.lunzn.model.common.AuditFlow;
import cn.lunzn.sz.server.dao.AuditFlowDao;

/**
 * @author clark
 * 审核电子流业务类
 */
@Service
public class AuditFlowService
{
    @Autowired
    private AuditFlowDao auditFlowDao;
    
    /**
     * 查询审核电子流
     * @param flow 查询条件
     * @return JSONObject
     */
    public JSONObject find(AuditFlow flow)
    {
        List<AuditFlow> records = auditFlowDao.find(flow);
        int total = auditFlowDao.findTotal(flow);
        
        ListResponse<AuditFlow> listRecord = new ListResponse<AuditFlow>();
        listRecord.setResult(records);
        listRecord.setTotal(total);
        
        return JSONObject.parseObject(listRecord.toString());
    }
    
    /**
     * 查询审核电子流-数量
     * @param flow 查询条件
     * @return JSONObject
     */
    public JSONObject findTotal(AuditFlow flow)
    {
        List<AuditFlow> records = new ArrayList<AuditFlow>();
        int total = auditFlowDao.findTotal(flow);
        
        ListResponse<AuditFlow> listRecord = new ListResponse<AuditFlow>();
        listRecord.setResult(records);
        listRecord.setTotal(total);
        
        return JSONObject.parseObject(listRecord.toString());
    }
    
    /**
     * 新增审核电子流
     * @param flow 请求参数
     * @return BaseResponse
     */
    public JSONObject insert(AuditFlow flow)
    {
        int rtcode = auditFlowDao.insert(flow);
        
        if (rtcode > 0)
        {
            return ResultCodeJson.getSuccess();
        }
        return ResultCodeJson.get500();
    }
    
    /**
     * 更新审核电子流
     * @param flow 请求参数
     * @return BaseResponse
     */
    public JSONObject update(AuditFlow flow)
    {
        int rtcode = auditFlowDao.update(flow);
        
        if (rtcode > 0)
        {
            return ResultCodeJson.getSuccess();
        }
        return ResultCodeJson.get500();
    }
    
    /**
     * 删除审核电子流
     * @param flow 请求参数
     * @return BaseResponse
     */
    public JSONObject delete(AuditFlow flow)
    {
        int rtcode = auditFlowDao.delete(flow);
        
        if (rtcode > 0)
        {
            return ResultCodeJson.getSuccess();
        }
        return ResultCodeJson.get500();
    }
}
