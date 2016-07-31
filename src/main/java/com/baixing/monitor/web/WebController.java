package com.baixing.monitor.web;

import com.baixing.monitor.util.BXMonitor;
import com.baixing.monitor.util.OrgEnum;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created by kofee on 16/7/26.
 */
@Controller
public class WebController {

    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value = "name", required = false, defaultValue = "World") String name, Model model) {
        model.addAttribute("name", name);
        return "greeting";
    }

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("orgs", OrgEnum.values());
        return "index";
    }


    //检测服务是否可用
    @RequestMapping(value = "/healthcheck")
    @ResponseBody
    public String healthCheck() {
        return "hello 世界";
    }

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
