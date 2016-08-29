package com.baixing.monitor.web;

import com.baixing.monitor.util.OrgEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Created by kofee on 16/8/27.
 */
@Controller
public class PageController {
    private static final Logger logger = LoggerFactory.getLogger(PageController.class);

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("username", "柯飞");
        return "index";
    }

    @RequestMapping("/index")
    public String indexPage(Model model) {
        model.addAttribute("username", "柯飞");
        return "index";
    }


    /**
     * 登录页
     */
    @RequestMapping("/login")
    public String login() {
        logger.info("================>login");
        return "login";
    }

    /**
     * dashboard页
     */
    @RequestMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }


    /**
     * 404页
     */
    @RequestMapping("/404")
    public String error404() {
        return "404";
    }

    /**
     * 401页
     */
    @RequestMapping("/401")
    public String error401() {
        return "401";
    }

    /**
     * 500页
     */
    @RequestMapping("/500")
    public String error500() {
        return "500";
    }

}
