package com.superbool.watcher.web;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.superbool.watcher.model.AppModel;
import com.superbool.watcher.model.MeasurementModel;
import com.superbool.watcher.model.ResponseModel;
import com.superbool.watcher.service.AppService;
import com.superbool.watcher.service.PushDataService;
import com.superbool.watcher.service.external.GrafanaService;
import com.superbool.watcher.service.external.InfluxDBService;
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

import java.util.ArrayList;
import java.util.List;
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
    private InfluxDBService influxDBService;

    @Autowired
    private GrafanaService grafanaService;

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
    public String getPushMetric(@RequestBody JsonObject metricJson) {

        return "{\"状态\":" + "}";
    }

    //接收推送的指标
    @RequestMapping(value = "/push", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseModel refreshDashboard(@RequestBody JsonObject metricJson) {
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

    @RequestMapping(value = "/ajax", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String ajaxTest(@RequestBody String request) {
        logger.info("http post request={}", request);
        JsonObject jsonObject = new Gson().fromJson(request, JsonObject.class);
        logger.info(jsonObject.toString());
        return "{\"code\":0,\"msg\":\"成功\"}";
    }

    @RequestMapping(value = "/influxdb", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseModel influxdbTest(@RequestBody String request) {
        logger.info("http post request={}", request);

        List<MeasurementModel> measurementList = new ArrayList<>();
        List<String> databases = influxDBService.showDatabases();
        for (String database : databases) {
            if ("_internal".equals(database)) {
                continue;
            }
            List<String> measuresName = influxDBService.showMeasurements(database);
            for (String measureName : measuresName) {
                MeasurementModel measurement = influxDBService.getMeasurement(database, measureName);
                measurementList.add(measurement);
            }
        }

        String result = new Gson().toJson(measurementList);
        logger.info("result={}", result);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("code", 1);
        jsonObject.addProperty("meg", "成功");
        jsonObject.addProperty("data", result);

        ResponseModel response = new ResponseModel();

        return response;
    }

    @RequestMapping(value = "/application", method = RequestMethod.GET)
    @ResponseBody
    public ResponseModel application() {

        List<JsonObject> measurementList = new ArrayList<>();
        List<String> databases = influxDBService.showDatabases();

        for (int i = 0; i < databases.size(); i++) {
            if ("_internal".equals(databases.get(i))) {
                continue;
            }

            JsonObject parentObj = new JsonObject();
            parentObj.addProperty("id", i);
            parentObj.addProperty("pId", 0);
            parentObj.addProperty("name", databases.get(i));
            parentObj.addProperty("isParent", true);
            measurementList.add(parentObj);

            List<String> measuresName = influxDBService.showMeasurements(databases.get(i));

            for (int j = 0; j < measuresName.size(); j++) {

                //每个表不能超过1000 且总的表数也不能超过1000，否则数字重复导致ztree混乱
                JsonObject ztreeObj = new JsonObject();
                ztreeObj.addProperty("id", 1000 + j);
                ztreeObj.addProperty("pId", i);
                ztreeObj.addProperty("name", measuresName.get(j));
                measurementList.add(ztreeObj);

            }
        }

        JsonElement result = new Gson().toJsonTree(measurementList);
        logger.info("result={}", result);

        ResponseModel response = new ResponseModel(0, "成功", result.toString());
        logger.info("response={}", response);
        return response;
    }

    @RequestMapping(value = "/measurement", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseModel measurement(String database, String measurement) {
        logger.info("http post database={},measurement={}", database, measurement);

        MeasurementModel model = influxDBService.getMeasurement(database, measurement);
        ResponseModel response = new ResponseModel(0, "成功", model.toJsonStr());
        logger.info("response={}", response);
        return response;
    }

    @RequestMapping(value = "/dashboard", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseModel dashboard(String database, String measurement) {
        logger.info("http post database={},measurement={}", database, measurement);

        String result = grafanaService.createDashboard(database, database, measurement);
        ResponseModel response = new ResponseModel(0, "成功", result);
        logger.info("response={}", response);
        return response;
    }

    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    @ResponseBody
    public ResponseModel getDashboard(String measurement) {
        logger.info("http get measurement={}", measurement);
        String result = grafanaService.getDashboard(measurement);
        ResponseModel response = new ResponseModel(0, "成功", result);
        logger.info("response={}", response);
        return response;
    }


}
