package com.superbool.watcher.web;

import com.alibaba.fastjson.JSONObject;
import com.superbool.watcher.model.AppModel;
import com.superbool.watcher.model.ResponseModel;
import com.superbool.watcher.service.AppService;
import com.superbool.watcher.service.PushDataService;
import com.superbool.watcher.service.external.GrafanaService;
import com.superbool.watcher.util.Monitor;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;


/**
 * Created by kofee on 16/7/23.
 * 接收post请求,返回页面
 */
@Controller
@RequestMapping(value = "/api")
public class APIController {

    private static final Logger logger = LoggerFactory.getLogger(APIController.class);

    @Autowired
    private AppService appService;

    @Autowired
    private PushDataService pushDataService;

    @Autowired
    private GrafanaService dashService;

    //应用注册
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String appRegister(AppModel appModel, Model model) {

        String errorMsg = "";
        if (appModel == null) {
            errorMsg = "application为空";
        } else if (Strings.isNullOrEmpty(appModel.getGroup())) {
            errorMsg = "应用分组不能为空";
        } else if (Strings.isNullOrEmpty(appModel.getName())) {
            errorMsg = "应用名称不能为空";
        } else if (Strings.isNullOrEmpty(appModel.getHost())) {
            errorMsg = "host地址不能为空";
        } else if (Strings.isNullOrEmpty(appModel.getContact())) {
            errorMsg = "联系人不能为空";
        } else if (appModel.getOrganization() <= 0) {
            errorMsg = "部门不正确";
        }

        if (!errorMsg.isEmpty()) {
            model.addAttribute("message", errorMsg);
            return "redirect:/index";
        } else {

            ResponseModel result = appService.register(appModel);

            if (result.getCode() == 1) {
                return "redirect:/index";
            } else if (result.getCode() == -1) {
                errorMsg = "应用名称重复";
            } else {
                errorMsg = "服务器错误";
            }
            model.addAttribute("message", errorMsg);
            return "redirect:/index";
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String loginCheck(@RequestParam(value = "username") String username,
                             @RequestParam(value = "password") String password,
                             RedirectAttributes model) {
        logger.info("api requset:username={},password={}", username, password);

        if (username.equals("admin")) {
            return "redirect:/index";
        } else {
            model.addFlashAttribute("message", "密码错误");
            return "redirect:/login";
        }
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String getPushMetric(@RequestBody JSONObject metricJson) {


        return "{\"状态\":" + "}";
    }

    //接收推送的指标
    @RequestMapping(value = "/push", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseModel refreshDashboard(@RequestBody JSONObject metricJson) {
        logger.info("request metricJson={}", metricJson);

        ResponseModel result = pushDataService.writePushMetric(metricJson);

        return result;
    }

    //检测服务是否可用
    @RequestMapping(value = "/healthcheck")
    @ResponseBody
    public String healthCheck() {
        return "hello 世界";
    }

    //监控接口
    @RequestMapping(value = "/monitor")
    @ResponseBody
    public String monitor() {
        StringBuilder out = new StringBuilder();
        for (Map.Entry<String, Long> entry : Monitor.getItemMap().entrySet()) {
            String name = entry.getKey();
            Number value = entry.getValue();
            out.append(name + "=" + value + "\n");
        }
        return out.toString();
    }


}
