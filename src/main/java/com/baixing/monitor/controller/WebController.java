package com.baixing.monitor.controller;

import com.baixing.monitor.model.AppModel;
import com.baixing.monitor.model.ResponseModel;
import com.baixing.monitor.service.AppService;
import com.baixing.monitor.service.DashService;
import com.baixing.monitor.util.BXMonitor;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by kofee on 16/7/23.
 */
@RestController
public class WebController {

    private static final Logger logger = LoggerFactory.getLogger(WebController.class);

    @Autowired
    private AppService appService;

    @Autowired
    private DashService dashService;

    //应用注册
    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseModel appRegister(@RequestBody AppModel appModel) {

        ResponseModel responseModel = new ResponseModel(-1, "服务器错误");
        if (appModel == null) {
            return new ResponseModel(-2, "application为空");
        }
        if (Strings.isNullOrEmpty(appModel.getName())) {
            return new ResponseModel(-3, "name不能为空");
        }
        if (Strings.isNullOrEmpty(appModel.getServer())) {
            return new ResponseModel(-4, "host地址不能为空");
        }
        if (Strings.isNullOrEmpty(appModel.getDuty())) {
            return new ResponseModel(-5, "负责人不能为空");
        }


        int result = appService.registerApp(appModel);

        if (result == 1) {
            return new ResponseModel(0, "成功");
        } else if (result == -1) {
            return new ResponseModel(-6, "应用名称重复");
        }

        return responseModel;

    }

    //刷新dashboard
    @RequestMapping(value = "/refresh", method = RequestMethod.GET)
    public String refreshDashboard(@RequestParam("orgId") String orgId,
                                   @RequestParam("appName") String appName) {
        logger.info("orgId={},appName={}", orgId, appName);

        int result = dashService.refreshDashboard(orgId, appName);
        return null;
    }




    //
    @RequestMapping(value = "/healthcheck")
    public String healthCheck() {
        return "hello 世界";
    }

    @RequestMapping(value = "/monitor")
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
