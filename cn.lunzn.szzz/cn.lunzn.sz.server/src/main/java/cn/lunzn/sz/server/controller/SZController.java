package cn.lunzn.sz.server.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;

import cn.lunzn.common.bean.BaseController;
import cn.lunzn.common.constant.ResultCodeJson;
import cn.lunzn.common.response.ListResponse;
import cn.lunzn.model.sz.SZ;
import cn.lunzn.sz.server.service.SZService;

/**
 * @author clark
 * 用户控制器
 */
@RestController
@RequestMapping(value = "/report")
public class SZController extends BaseController
{
    private Logger logger = LoggerFactory.getLogger(SZController.class);
    
    /**
     * 用户业务类
     */
    @Autowired
    private SZService szService;
    
    /**
     * 获取述职报告列表
     * @param request HTTP 请求体
     * @param response HTTP 响应体
     * @param jsonData 请求参数
     * @return String  
     */
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public String queryReportList(HttpServletRequest request, HttpServletResponse response,
        @RequestBody String jsonData)
    {
        logger.info("start queryReportList, param:{}", jsonData);
        
        SZ sz = JSON.parseObject(jsonData, SZ.class);
        List<SZ> result = szService.getSZ(sz);
        
        ListResponse<SZ> resp = new ListResponse<SZ>();
        resp.setResult(result);
        resp.setTotal(szService.getSZTotal(sz));
        
        return resp.toString();
    }
    
    /**
     * 1、保存述职报告
     * 2、提交操作电子流，保存草稿操作不用提交电子流
     * @param request HTTP 请求体
     * @param response HTTP 响应体
     * @param jsonData 请求参数
     * @return String  响应
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String saveReportWorkInfo(HttpServletRequest request, HttpServletResponse response,
        @RequestBody String jsonData)
    {
        logger.info("start queryReportList, param:{}", jsonData);
        
        SZ sz = JSON.parseObject(jsonData, SZ.class);
        int i = szService.saveSZ(sz);
        if (i == 1)
        {
            logger.info("述职信息保存成功");
            return ResultCodeJson.getSuccess().toJSONString();
        }
        else
        {
            logger.info("述职信息保存失败");
            return ResultCodeJson.get500().toJSONString();
        }
    }
    
    /**
     * 1、保存述职报告
     * 2、提交操作电子流，保存草稿操作不用提交电子流
     * @param request HTTP 请求体
     * @param response HTTP 响应体
     * @param jsonData 请求参数
     * @return String  响应
     */
    @RequestMapping(value = "/updateState", method = RequestMethod.POST)
    public String updateReportWorkState(HttpServletRequest request, HttpServletResponse response,
        @RequestBody String jsonData)
    {
        logger.info("start queryReportList, param:{}", jsonData);
        
        SZ sz = JSON.parseObject(jsonData, SZ.class);
        int i = szService.updateSZState(sz);
        if (i == 1)
        {
            logger.info("述职状态更新成功");
            return ResultCodeJson.getSuccess().toJSONString();
        }
        else
        {
            logger.info("述职状态更新失败");
            return ResultCodeJson.get500().toJSONString();
        }
    }
    
    /**
     * 查询驳回次数
     * @param request HTTP 请求体
     * @param response HTTP 响应体
     * @param jsonData 请求参数
     * @return String  响应
     */
    @RequestMapping(value = "/queryRejectTime", method = RequestMethod.POST)
    public String queryRejectTime(HttpServletRequest request, HttpServletResponse response,
        @RequestBody String jsonData)
    {
        logger.info("start queryRejectTime, param:{}", jsonData);
        int i = szService.queryRejectTime(JSON.parseObject(jsonData, SZ.class));
        SZ sz = new SZ();
        sz.setRejectTime(i + "");
        List<SZ> result = new ArrayList<SZ>();
        result.add(sz);
        ListResponse<SZ> resp = new ListResponse<SZ>();
        resp.setResult(result);
        
        return resp.toString();
    }
    
    /**
     * 查询当前年份，当前员工提交述职记录数
     * @param request HTTP 请求体
     * @param response HTTP 响应体
     * @param jsonData 请求参数
     * @return String  响应
     */
    @RequestMapping(value = "/querySubmitTime", method = RequestMethod.POST)
    public String querySubmitTime(HttpServletRequest request, HttpServletResponse response,
        @RequestBody String jsonData)
    {
        logger.info("start querySubmitTime, param:{}", jsonData);
        int i = szService.querySubmitTime(JSON.parseObject(jsonData, SZ.class));
        SZ sz = new SZ();
        sz.setSubmitTime(i + "");
        List<SZ> result = new ArrayList<SZ>();
        result.add(sz);
        ListResponse<SZ> resp = new ListResponse<SZ>();
        resp.setResult(result);
        
        return resp.toString();
    }
    
}
