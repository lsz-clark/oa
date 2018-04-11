package cn.lunzn.zz.server.service;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lunzn.common.bean.BaseResponse;
import cn.lunzn.common.date.DateUtil;
import cn.lunzn.common.enums.CommonResultCode;
import cn.lunzn.common.response.ListResponse;
import cn.lunzn.model.zz.ZZ;
import cn.lunzn.zz.server.dao.ZZDao;

/**
 * 员工转正业务类
 * 
 * @author  雷懿
 * @version  [版本号, 2017年8月30日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Service
public class ZZService
{
    /**
     * 员工转正Dao
     */
    @Autowired
    private ZZDao zzDao;
    
    /** 
     * 查询员工转正记录
     * @param param 查询条件
     * @return ListResponse<ZZ>
     * @see [类、类#方法、类#成员]
     */
    public ListResponse<ZZ> query(ZZ param)
    {
        ListResponse<ZZ> zzList = new ListResponse<ZZ>();
        
        List<ZZ> list = zzDao.find(param);
        zzList.setResult(list);
        
        int total = zzDao.findTotal(param);
        zzList.setTotal(total);
        
        return zzList;
    }
    
    /** 
     * 保存员工转正申请记录
     * @param param 员工转正申请信息
     * @return BaseResponse
     * @see [类、类#方法、类#成员]
     */
    public BaseResponse add(ZZ param)
    {
        // 设置提交日期
        param.setSubmitDate(DateUtil.formatDateToString(Calendar.getInstance().getTime()));
        
        BaseResponse baseResponse = new BaseResponse();
        if (zzDao.insert(param) > 0)
        {
            return baseResponse;
        }
        else
        {
            baseResponse.setCode(CommonResultCode.SERVER_BUSY_NOW.getCode());
            baseResponse.setMsg(CommonResultCode.SERVER_BUSY_NOW.getMsg());
            return baseResponse;
        }
    }
    
    /** 
     * 保存员工转正申请记录
     * @param param 员工转正申请信息
     * @return BaseResponse
     * @see [类、类#方法、类#成员]
     */
    public BaseResponse update(ZZ param)
    {
        BaseResponse baseResponse = new BaseResponse();
        if (zzDao.update(param) > 0)
        {
            return baseResponse;
        }
        else
        {
            baseResponse.setCode(CommonResultCode.SERVER_BUSY_NOW.getCode());
            baseResponse.setMsg(CommonResultCode.SERVER_BUSY_NOW.getMsg());
            return baseResponse;
        }
    }
}
