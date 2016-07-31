package com.baixing.monitor.service;

import com.baixing.monitor.Application;
import com.baixing.monitor.mapper.DashMapper;
import com.baixing.monitor.model.DashModel;
import com.baixing.monitor.model.AppModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kofee on 16/7/23.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
public class Grafana {

    @Autowired
    private DashService dashService;

    @Autowired
    private DashMapper dashMapper;


    @Test
    public void saveDashTest() {

        AppModel appModel = new AppModel();
        appModel.setName("测试标题2");
        appModel.setAppDesc("测试描述");
        dashService.addDashboard(appModel);
    }

    @Test
    public void addPanel() {
        List<String> panelKeyList = new ArrayList<>();
        panelKeyList.add("key1");
        panelKeyList.add("key2");
        panelKeyList.add("key3");
        dashService.addDashPanel(panelKeyList, 17, "");
    }

    @Test
    public void test() {
        DashModel dashboard = dashMapper.getDashboardById(3, "test");
        System.out.println(dashboard);

        int result = dashMapper.updateDataById("hello world", 1);
        System.out.println(result);

        DashModel dashboard1 = new DashModel();
        dashboard1.setTitle("测试");
        dashboard1.setSlug("1");
        result = dashMapper.addDashboard(dashboard1);
        System.out.println(result);

    }
}
