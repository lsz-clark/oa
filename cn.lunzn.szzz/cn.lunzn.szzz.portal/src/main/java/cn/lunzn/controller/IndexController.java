package cn.lunzn.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 首页跳转
 * @author clark
 */
@Controller
@RequestMapping("/")
public class IndexController
{
    /** 
     * 主页
     * @return String
     * @see [类、类#方法、类#成员]
     */
    @RequestMapping("/")
    public String home()
    {
        return "redirect:/portal/home.html";
    }
}
