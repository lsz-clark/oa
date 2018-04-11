package cn.lunzn.service.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.lunzn.common.bean.BaseBean;
import cn.lunzn.common.enums.LevelStarEnum;
import cn.lunzn.common.enums.ZzTypeEnum;
import cn.lunzn.model.sz.SZ;
import cn.lunzn.model.zz.ZZ;
import cn.lunzn.service.sz.ReportWorkService;
import cn.lunzn.service.zz.ZZService;

/**
 * 归档业务类
 * @author 雷懿
 */
@Service
public class ArchiveService
{
    /**
     * 数字常量 5，用于评星等级个数
     */
    private static final int NUM_5 = 5;
    
    /**
     * 数字常量 10，用于截取日期字符串
     */
    private static final int NUM_10 = 10;
    
    /**
     * 数字常量3
     */
    private static final int NUM_3 = 3;
    
    /**
     * 日志记录
     */
    private Logger logger = LoggerFactory.getLogger(ArchiveService.class);
    
    /**
     * 年度述职业务类
     */
    @Autowired
    private ReportWorkService reportWorkService;
    
    /**
     * 员工转正业务类
     */
    @Autowired
    private ZZService zzService;
    
    /**
     * 生成归档文件-员工转正
     * @param zzId 员工转正业务ID
     * @param request HTTP请求体
     * @return 归档文件名称
     * @throws IOException 可能发生IO异常
     */
    public String archiveZZ(Integer zzId, HttpServletRequest request)
        throws IOException
    {
        logger.debug("[Archive] Go to the ArchiveService.archiveZZ method.");
        
        // 查询出要归档的员工转正相关信息
        logger.debug("[Archive] Query the information to be archived.");
        JSONObject zzInfo = zzService.queryArchiveInfo(zzId);
        ZZ info = (ZZ)zzInfo.get("info");
        
        // 确定要生成的文件名称和文档模版
        String fileName = zzId + "-" + info.getStaffName() + "-转正申请.html";
        String zzModelPath =
            File.separator + "archives" + File.separator + "model" + File.separator + "业务ID-姓名-转正申请.html";
        
        // 生成归档文件
        archive(fileName, zzModelPath, zzInfo, info, request);
        
        // 返回生成文件名称
        logger.debug("[Archive] The ArchiveService.archiveZZ method ends.");
        return fileName;
    }
    
    /**
     * 生成归档文件-年度述职
     * @param szId 年度述职业务ID
     * @param request HTTP请求体
     * @return 归档文件名称
     * @throws IOException 可能发生IO异常
     */
    public String archiveSZ(Integer szId, HttpServletRequest request)
        throws IOException
    {
        logger.debug("[Archive] Go to the ArchiveService.archiveSZ method.");
        
        // 查询出要归档的员工述职相关信息
        logger.debug("[Archive] Query the information to be archived.");
        JSONObject szInfo = reportWorkService.queryReportInfo(szId);
        SZ info = (SZ)szInfo.get("info");
        
        // 确定要生成的文件
        String fileName = szId + "-" + info.getStaffName() + "-" + info.getTimeQuantum() + "-年度述职.html";
        String szModelPath =
            File.separator + "archives" + File.separator + "model" + File.separator + "业务ID-姓名-年份-述职报告.html";
        
        // 生成归档文件
        archive(fileName, szModelPath, szInfo, info, request);
        
        // 返回生成文件名称
        logger.debug("[Archive] The ArchiveService.archiveSZ method ends.");
        return fileName;
    }
    
    /**
     * 下载文件到本地
     * @param request 请求参数对象
     * @param response 响应参数对象
     * @throws IOException 可能抛出IO异常
     */
    public void downloadFile(HttpServletRequest request, HttpServletResponse response)
        throws IOException
    {
        // 获取要下载的文件名
        String fileName = request.getParameter("fileName");
        // 要下载的文件所在目录
        String dirPath = URLDecoder.decode(
            request.getServletContext().getRealPath(File.separator) + File.separator + "archives" + File.separator,
            "UTF-8");
        // 防止下载时文件名出现中文乱码
        String userAgent = request.getHeader("User-Agent");
        String formFileName = fileName;
        if (userAgent.contains("MSIE") || userAgent.contains("Trident"))
        {
            formFileName = java.net.URLEncoder.encode(formFileName, "UTF-8");
        }
        else
        {
            // 非IE浏览器的处理
            formFileName = new String(formFileName.getBytes("UTF-8"), "ISO-8859-1");
        }
        
        // dirPath是指欲下载的文件所在目录
        File file = getFile(dirPath, fileName);
        
        // 以流的形式下载文件。
        InputStream fis = new BufferedInputStream(new FileInputStream(file));
        byte[] buffer = new byte[fis.available()];
        fis.read(buffer);
        fis.close();
        
        // 清空response
        response.reset();
        
        // 设置response的Header
        response.addHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", formFileName));
        response.addHeader("Content-Length", "" + file.length());
        OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
        response.setContentType("application/octet-stream;charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        toClient.write(buffer);
        toClient.flush();
        toClient.close();
    }
    
    /**
     * 生成归档文件
     * @param fileName 文件名称
     * @param zzModelPath 模版路径
     * @param archiveInfo 归档信息
     * @param info 员工基本信息
     * @param request HTTP请求体
     * @throws IOException 可能发生IO异常
     */
    private void archive(String fileName, String modelPath, JSONObject archiveInfo, BaseBean info,
        HttpServletRequest request)
        throws IOException
    {
        // 获取归档的信息
        JSONArray auditInfos = archiveInfo.getJSONObject("auditInfo").getJSONArray("result");
        auditInfos = auditInfoSort(auditInfos);
        String rejectCount = archiveInfo.getString("rejectCount");
        
        // 确定要生成的文件
        String filePath = request.getServletContext().getRealPath(File.separator) + File.separator + "archives"
            + File.separator + fileName;
        File file = new File(URLDecoder.decode(filePath, "UTF-8"));
        
        logger.info("[Archive] Identify the files to be generated, fileName: {}", fileName);
        
        // 如果文件已存在（说明已经归档过了）直接结束方法，不再重新创建
        if (file.exists())
        {
            return;
        }
        
        // 读取模版
        logger.debug("[Archive] Read the archive template.");
        modelPath = request.getServletContext().getRealPath(File.separator) + modelPath;
        File input = new File(URLDecoder.decode(modelPath, "UTF-8"));
        Document doc = Jsoup.parse(input, "UTF-8");
        
        // 添加网页标题
        logger.debug("[Archive] Edit the page title.");
        doc.getElementsByTag("title").get(0).text(fileName.replace(".html", ""));
        
        // 填充基本信息
        logger.debug("[Archive] Fill basic information.");
        if (info instanceof ZZ)
        {
            ZZ zzInfo = (ZZ)info;
            createBaseInfo(zzInfo, rejectCount, doc);
        }
        else if (info instanceof SZ)
        {
            SZ szInfo = (SZ)info;
            createBaseInfo(szInfo, rejectCount, doc);
        }
        
        // 填充历史审核信息
        logger.debug("[Archive] Fill in historical audit information.");
        for (int i = 0; i < auditInfos.size(); i++)
        {
            JSONObject auditInfo = auditInfos.getJSONObject(i);
            String auditValue = auditInfo.getString("auditValue");
            
            // 员工转正部门审核的审核信息
            if ("2".equals(auditInfo.getString("auditType")) && "1".equals(auditInfo.getString("auditStep")))
            {
                JSONObject auditObj = getJsonObj(auditValue);
                // 等级评定
                createRatingNode(doc, auditObj);
                // 转正类型
                createZzTypeNode(doc, auditInfo);
                // 评语
                createTitle(doc, "评语");
                createContent(doc,
                    auditObj.getString("comment"),
                    auditInfo.getString("state"),
                    auditInfo.getString("staffName"),
                    auditInfo.getString("finishTime"));
                // 部门审核后肯定还有审核步骤的，所以这里直接添加一条分割线
                createSplitLine(doc);
                continue;
            }
            // 非员工转正部门审核信息
            createTitle(doc, "评语");
            createContent(doc,
                auditValue,
                auditInfo.getString("state"),
                auditInfo.getString("staffName"),
                auditInfo.getString("finishTime"));
            // 非最后一个审核步骤，加一条分割线
            if (i < auditInfos.size() - 1)
            {
                createSplitLine(doc);
            }
        }
        
        // 写入本地文件
        logger.info("[Archive] Save as a file, filePath: {}", file);
        FileOutputStream fos = new FileOutputStream(file);
        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
        osw.write(doc.html());
        osw.close();
    }
    
    /** 
     * 如果原始字符串为空，则返回空格字符串
     * @param sourceStr 原始字符串
     * @return String
     * @see [类、类#方法、类#成员]
     */
    private String setDefaultEmpty(String sourceStr)
    {
        if (StringUtils.isEmpty(sourceStr))
        {
            return " ";
        }
        else
        {
            return sourceStr;
        }
    }
    
    /**
     * 填充转正基本信息
     * @param info 转正信息
     * @param rejectCount 驳回次数
     * @param doc 文档对象
     */
    private void createBaseInfo(ZZ info, String rejectCount, Document doc)
    {
        doc.getElementById("submitTime").text(setDefaultEmpty(info.getSubmitDate()));
        doc.getElementById("rejectCount").text(setDefaultEmpty(rejectCount));
        doc.getElementById("staffName").text(setDefaultEmpty(info.getStaffName()));
        doc.getElementById("department").text(setDefaultEmpty(info.getDepartment()));
        doc.getElementById("position").text(setDefaultEmpty(info.getPosition()));
        doc.getElementById("entryDate").text(setDefaultEmpty(info.getEntryDate()));
        doc.getElementById("education").text(setDefaultEmpty(info.getEducation()));
        doc.getElementById("gradDate").text(setDefaultEmpty(info.getGradDate()));
        doc.getElementById("zzDate").text(setDefaultEmpty(info.getZzDate()));
        doc.getElementById("effort").text(setDefaultEmpty(info.getEffort()));
        doc.getElementById("gain").text(setDefaultEmpty(info.getGain()));
        doc.getElementById("suggest").text(setDefaultEmpty(info.getSuggest()));
    }
    
    /**
     * 填充述职基本信息
     * @param info 述职信息
     * @param rejectCount 驳回次数
     * @param doc 文档对象
     */
    private void createBaseInfo(SZ info, String rejectCount, Document doc)
    {
        doc.getElementById("submitTime").text(setDefaultEmpty(info.getSubmitdate()));
        doc.getElementById("rejectCount").text(setDefaultEmpty(rejectCount));
        doc.getElementById("staffName").text(setDefaultEmpty(info.getStaffName()));
        doc.getElementById("department").text(setDefaultEmpty(info.getDepartment()));
        doc.getElementById("position").text(setDefaultEmpty(info.getPosition()));
        doc.getElementById("entryDate").text(setDefaultEmpty(info.getEntryDate()));
        doc.getElementById("education").text(setDefaultEmpty(info.getEducation()));
        doc.getElementById("gradDate").text(setDefaultEmpty(info.getGradDate()));
        doc.getElementById("timequantum").text(setDefaultEmpty(info.getTimeQuantum()));
        doc.getElementById("workresults").text(setDefaultEmpty(info.getWorkresults()));
        doc.getElementById("evaluation").text(setDefaultEmpty(info.getEvaluation()));
        doc.getElementById("deficiency").text(setDefaultEmpty(info.getDeficiency()));
        doc.getElementById("plan").text(setDefaultEmpty(info.getPlan()));
    }
    
    /**
     * 创建审核记录内容
     * @param doc 文档对象
     * @param cententText 文本内容
     * @param state 审核状态
     * @param authorName 审核人
     * @param date 审核时间
     */
    private void createContent(Document doc, String contentText, String state, String authorName, String date)
    {
        // 找到历史记录节点
        Element auditNode = doc.select("#auditInfo").first();
        // 在历史记录节点上新增一个节点作为审核内容
        Element contentNode = auditNode.appendElement("div");
        contentNode.attr("align", "center");
        // 在内容节点上新增一个div，作为内容主体
        Element contentDiv = contentNode.appendElement("div");
        contentDiv.attr("align", "center");
        contentDiv.attr("style", "width: 55%;text-align: left;margin-left: 10%");
        Element textarea = contentDiv.appendElement("textarea");
        textarea.attr("style", "width: 100%;border: 0px;overflow:hidden;");
        textarea.attr("readonly", true);
        textarea.text(setDefaultEmpty(contentText));
        // 在内容节点上新增第二个div，记录评审人
        Element authorNode = contentNode.appendElement("div");
        authorNode.attr("style", "width: 55%;text-align: right;;margin-left: 5%");
        Element stateNode = authorNode.appendElement("span");
        String stateText = "通过";
        String color = "green;";
        if ("2".equals(state))
        {
            stateText = "驳回";
            color = "red;";
        }
        stateNode.attr("style", "margin-right: 5px;color: " + color);
        stateNode.appendText(stateText);
        Element name = authorNode.appendElement("span");
        name.appendText(setDefaultEmpty(authorName));
        Element auditDate = authorNode.appendElement("span");
        auditDate.attr("style", "margin-left: 5px;");
        auditDate.appendText(date.substring(0, NUM_10));
    }
    
    /**
     * 创建审核记录标题
     * @param doc 文档对象
     * @param titleText 标题内容
     */
    private void createTitle(Document doc, String titleText)
    {
        // 找到历史记录节点
        Element auditNode = doc.select("#auditInfo").first();
        // 在历史记录节点上新增一个节点作为标题
        Element br = auditNode.appendElement("br");
        logger.debug("[Archive] Add br tag, {}.", br);
        Element titleNode = auditNode.appendElement("div");
        titleNode.attr("align", "center");
        // 在标题节点上新增一个div靠左，调整标题位置
        Element titleDiv = titleNode.appendElement("div");
        titleDiv.attr("align", "left");
        titleDiv.attr("style", "width: 60%;font-size: 26px;");
        // 标题内容，使用h2标签并设置文本
        Element textNode = titleDiv.appendElement("text");
        textNode.attr("class", "absTitle");
        textNode.appendText(setDefaultEmpty(titleText));
    }
    
    /**
     * 创建转正等级评定节点
     * @param doc 
     */
    private void createRatingNode(Document doc, JSONObject auditObj)
    {
        // 添加标题节点
        createTitle(doc, "等级评定");
        
        // 找到历史记录节点
        Element auditNode = doc.select("#auditInfo").first();
        
        // 在历史记录节点上新增一个节点作为评星节点
        Element starNode = auditNode.appendElement("div");
        starNode.attr("align", "center");
        starNode.attr("class", "star-rating");
        starNode.attr("style", "width: 45%;text-align: left;");
        
        // 在评星节点上添加一个原始星节点
        Element spanDiv = starNode.appendElement("div");
        spanDiv.attr("class", "star-rating-bottom");
        
        // 在原始星节点上添加评分前的星星符号
        String star = "★";
        int level = auditObj.getIntValue("level");
        for (int i = 0; i < NUM_5; i++)
        {
            if (i >= level)
            {
                star = "☆";
            }
            Element spanNode = spanDiv.appendElement("span");
            spanNode.appendText(star);
        }
        Element spanNode = spanDiv.appendElement("span");
        spanNode.attr("style", "font-size: 20px;");
        spanNode.appendText(LevelStarEnum.getText(level));
    }
    
    /**
     * 创建转正类型节点
     * @param doc 
     * @param auditObj 
     */
    private void createZzTypeNode(Document doc, JSONObject auditInfo)
    {
        // 添加标题节点
        createTitle(doc, "转正类型");
        
        // 找到历史记录节点
        Element auditNode = doc.select("#auditInfo").first();
        
        // 在历史记录节点上新增一个节点作为类型节点
        Element zzTypeNode = auditNode.appendElement("div");
        zzTypeNode.attr("align", "center");
        zzTypeNode.attr("style", "text-align: left;width: 45%;");
        
        // 类型节点下添加类型内容
        Element divNode = zzTypeNode.appendElement("div");
        JSONObject auditValue = JSONObject.parseObject(auditInfo.getString("auditValue"));
        for (int i = 0; i < NUM_5;)
        {
            Element input = divNode.appendElement("input");
            input.attr("type", "radio");
            input.attr("value", ++i + "");
            input.attr("disabled", "true");
            input.appendText(ZzTypeEnum.getName(i));
            boolean checked = i == auditValue.getIntValue("type");
            
            if (checked)
            {
                input.attr("checked", true);
            }
            if (i > 1)
            {
                Element inputDate = divNode.appendElement("input");
                inputDate.attr("type", "text");
                inputDate.attr("readonly", true);
                inputDate.attr("class", "removeBorder");
                inputDate.attr("style", "text-align: center;");
                if (checked)
                {
                    inputDate.attr("value", auditValue.getString("typeVal"));
                }
                if (i > NUM_3)
                {
                    inputDate.attr("style", "margin-left: 25px;text-align: center;");
                }
            }
            if (i < NUM_5)
            {
                // 该变量没什么用，如果不接收有findbugs问题，日志也没有任何意义
                Element br = divNode.appendElement("br");
                br = divNode.appendElement("br");
                logger.debug("[Archive] Add br under zzTypeNode, {}.", br);
            }
        }
    }
    
    /**
     * 将JSON字符串转换成json对象，如果传入字符串不是json格式，返回null
     * @param string 要转换的字符串
     * @return 转换后的json对象，字符串格式非json返回null
     */
    private JSONObject getJsonObj(String string)
    {
        try
        {
            JSONObject jsonObj = (JSONObject)JSON.parse(string);
            return jsonObj;
        }
        catch (Exception e)
        {
            return null;
        }
    }
    
    /**
     * 按照审核时间给审核内容排序
     * @param jsonArrStr 审核内容JSONArray
     * @return 排序后的审核内容JSONArray
     */
    private JSONArray auditInfoSort(JSONArray jsonArr)
    {
        List<JSONObject> jsonValues = new ArrayList<JSONObject>();
        
        // 将要排序的jsonArray字符串保存为jsonObject的List集合
        for (int i = 0; i < jsonArr.size(); i++)
        {
            jsonValues.add(jsonArr.getJSONObject(i));
        }
        
        // 排序
        Collections.sort(jsonValues, new Comparator<JSONObject>()
        {
            // 排序字段
            private static final String KEY_NAME = "finishTime";
            
            @Override
            public int compare(JSONObject a, JSONObject b)
            {
                String valA = (String)a.get(KEY_NAME);
                String valB = (String)b.get(KEY_NAME);
                return valA.compareTo(valB);
            }
        });
        return JSONArray.parseArray(JSONArray.toJSONString(jsonValues));
    }
    
    /**
     * 返回要下载的文件，如果文件不存在则返回null
     * @param dirPath 下载文件所在目录
     * @param fileName 要下载的文件名称
     * @return 要下载的文件，如果文件不存在则返回null
     */
    private File getFile(String dirPath, String fileName)
    {
        File parentDir = new File(dirPath);
        String[] files = parentDir.list();
        if (ArrayUtils.isEmpty(files))
        {
            return null;
        }
        
        for (String string : files)
        {
            if (string.equals(fileName))
            {
                File tmpFile = new File(dirPath + fileName);
                if (tmpFile.isFile())
                {
                    return tmpFile;
                }
            }
        }
        return null;
    }
    
    /**
     * 在历史审核记录中添加一条分割线
     * @param doc 文档对象
     */
    private void createSplitLine(Document doc)
    {
        // 找到历史记录节点
        Element auditNode = doc.select("#auditInfo").first();
        Element br = auditNode.appendElement("br");
        logger.debug("[Archive] Add a split line, {}.", br);
        Element hr = auditNode.appendElement("hr");
        hr.attr("style", "width: 65%;height: 1px;border: none;border-top:1px solid #ccc;");
    }
}