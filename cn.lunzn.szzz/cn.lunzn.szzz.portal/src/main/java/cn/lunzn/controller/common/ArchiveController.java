package cn.lunzn.controller.common;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

import cn.lunzn.common.bean.BaseController;
import cn.lunzn.common.bean.BaseResponse;
import cn.lunzn.service.common.ArchiveService;

/**
 * 归档下载
 * @author 雷懿
 */
@Controller
@RequestMapping("/archive")
public class ArchiveController extends BaseController
{
    /**
     * 日志记录
     */
    private Logger logger = LoggerFactory.getLogger(ArchiveController.class);
    
    /**
     * 归档业务类
     */
    @Autowired
    private ArchiveService archiveService;
    
    /** 
     * 员工转正归档
     * @param request HTTP请求体
     * @param response HTTP响应体
     * @param jsonData 业务id
     * @return String
     * @see [类、类#方法、类#成员]
     */
    @RequestMapping(value = "/zz", method = RequestMethod.POST)
    @ResponseBody
    public String archiveZZ(HttpServletRequest request, HttpServletResponse response, @RequestBody String jsonData)
    {
        logger.info("[Archive] Start generating files.");
        
        JSONObject param = JSONObject.parseObject(jsonData);
        Integer id = param.getInteger("id");
        
        try
        {
            // 生成归档文件
            String fileName = archiveService.archiveZZ(id, request);
            // 生成下载地址
            BaseResponse br = new BaseResponse();
            JSONObject result = JSONObject.parseObject(br.toString());
            result.put("fileName", fileName);
            
            logger.info("[Archive] End generating files.");
            
            return result.toString();
        }
        catch (IOException e)
        {
            logger.error("[Archive] File generation failed.", e);
        }
        return null;
    }
    
    /** 
     * 年度述职档案生成归档文件
     * @param request HTTP请求体
     * @param response HTTP响应体
     * @param jsonData 业务id
     * @return String 归档文件名称
     */
    @RequestMapping(value = "/sz", method = RequestMethod.POST)
    @ResponseBody
    public String archiveSZ(HttpServletRequest request, HttpServletResponse response, @RequestBody String jsonData)
    {
        logger.info("[Archive] Start generating files.");
        
        JSONObject param = JSONObject.parseObject(jsonData);
        Integer id = param.getInteger("id");
        
        try
        {
            // 生成归档文件
            String fileName = archiveService.archiveSZ(id, request);
            // 生成下载地址
            BaseResponse br = new BaseResponse();
            JSONObject result = JSONObject.parseObject(br.toString());
            result.put("fileName", fileName);
            
            logger.info("[Archive] End generating files.");
            return result.toString();
        }
        catch (IOException e)
        {
            logger.error("[Archive] File generation failed.", e);
        }
        return null;
    }
    
    /** 
     * 档案下载
     * @param request HTTP请求体
     * @param response HTTP响应体
     */
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void archiveZZ(HttpServletRequest request, HttpServletResponse response)
    {
        try
        {
            archiveService.downloadFile(request, response);
        }
        catch (UnsupportedEncodingException e)
        {
            logger.error("[Archive] download file failed, msg: ", e);
        }
        catch (IOException e)
        {
            logger.error("[Archive] download file failed, msg: ", e);
        }
    }
}