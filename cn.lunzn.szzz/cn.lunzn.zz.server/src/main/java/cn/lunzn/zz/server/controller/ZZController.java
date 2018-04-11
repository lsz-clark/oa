package cn.lunzn.zz.server.controller;

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
import cn.lunzn.model.zz.ZZ;
import cn.lunzn.zz.server.service.ZZService;

/**
 * @author 雷懿
 * 员工转正控制器
 */
@RestController
@RequestMapping(value = "/zz")
public class ZZController extends BaseController
{
    private Logger logger = LoggerFactory.getLogger(ZZController.class);
    
    /**
     * 员工转正业务类
     */
    @Autowired
    private ZZService zzService;
    
    /**
     * 查询记录
     * @param request HTTP请求体
     * @param response HTTP响应体
     * @param jsonData 请求参数
     * @return String
     */
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public String query(HttpServletRequest request, HttpServletResponse response, @RequestBody String jsonData)
    {
        logger.info("[zhuanzheng] query record, request data:" + jsonData);
        
        ZZ param = JSON.parseObject(jsonData, ZZ.class);
        
        return zzService.query(param).toString();
    }
    
    /**
     * 申请员工转正
     * @param request HTTP请求体
     * @param response HTTP响应体
     * @param jsonData 请求参数
     * @return String
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String add(HttpServletRequest request, HttpServletResponse response, @RequestBody String jsonData)
    {
        logger.info("[zhuanzheng] add record, request data:" + jsonData);
        ZZ param = JSON.parseObject(jsonData, ZZ.class);
        
        return zzService.add(param).toString();
    }
    
    /**
     * 更新员工转正
     * @param request HTTP请求体
     * @param response HTTP响应体
     * @param jsonData 请求参数
     * @return String
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String update(HttpServletRequest request, HttpServletResponse response, @RequestBody String jsonData)
    {
        logger.info("[zhuanzheng] update record, request data:" + jsonData);
        ZZ param = JSON.parseObject(jsonData, ZZ.class);
        
        return zzService.update(param).toString();
    }
}
