package com.superbool.monitor.web;

import com.superbool.monitor.util.OrgEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by kofee on 16/8/27.
 */
@Controller
public class PageController {
    private static final Logger logger = LoggerFactory.getLogger(PageController.class);

    //@RequestMapping("/")
    public String index(Model model) {
        List<String> appNameList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {

            //appNameList.add(appModel);
        }

        model.addAttribute("appNameList", appNameList);
        model.addAttribute("username", "柯飞");

        return "index";
    }

    @RequestMapping("/index")
    public String indexPage(Model model) {
        return index(model);
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
    public String dashboard(Model model) {

        return "dashboard";
    }

    @RequestMapping("/register")
    public String register(Model model) {
        model.addAttribute("orgs", OrgEnum.values());
        return "register";
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
