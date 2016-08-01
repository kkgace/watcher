package com.baixing.monitor.web;

import com.baixing.monitor.model.AppModel;
import com.baixing.monitor.model.ResponseModel;
import com.baixing.monitor.service.AppService;
import com.baixing.monitor.service.DashService;
import com.baixing.monitor.util.BXMonitor;
import com.baixing.monitor.util.OrgEnum;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * Created by kofee on 16/7/23.
 * 接收post请求,返回页面
 */
@Controller
public class APIController {

    private static final Logger logger = LoggerFactory.getLogger(APIController.class);

    @Autowired
    private AppService appService;

    @Autowired
    private DashService dashService;

    //应用注册
    @RequestMapping(value = "/api/register", method = RequestMethod.POST)
    public String appRegister(AppModel appModel, Model model) {

        String errorMsg = "";
        if (appModel == null) {
            errorMsg = "application为空";
        } else if (Strings.isNullOrEmpty(appModel.getName())) {
            errorMsg = "应用名称不能为空";
        } else if (Strings.isNullOrEmpty(appModel.getHost())) {
            errorMsg = "host地址不能为空";
        } else if (Strings.isNullOrEmpty(appModel.getCharger())) {
            errorMsg = "负责人不能为空";
        } else if (appModel.getOrgId() <= 0L) {
            errorMsg = "部门不正确";
        }

        if (!errorMsg.isEmpty()) {
            model.addAttribute("errorMsg", errorMsg);
            return "index";
        } else {

            int result = appService.registerApp(appModel);

            if (result == 1) {
                return "success";
            } else if (result == -1) {
                errorMsg = "应用名称重复";
            } else {
                errorMsg = "服务器错误";
            }
            model.addAttribute("errorMsg", errorMsg);
            return "index";
        }


    }

    //刷新dashboard
    @RequestMapping(value = "/api/refresh", method = RequestMethod.GET)
    public String refreshDashboard(@RequestParam("orgId") String orgId,
                                   @RequestParam("appName") String appName) {
        logger.info("orgId={},appName={}", orgId, appName);

        int result = dashService.refreshDashboard(Long.parseLong(orgId), appName);

        return "{\"状态\":" + result + "}";
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
        for (Map.Entry<String, Long> entry : BXMonitor.getValues().entrySet()) {
            String name = entry.getKey();
            Number value = entry.getValue();
            out.append(name + "=" + value + "\n");
        }
        return out.toString();
    }


}
