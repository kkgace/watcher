package com.baixing.monitor.service.external;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baixing.monitor.WatcherApplication;
import com.baixing.monitor.model.AppModel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Created by kofee on 16/8/29.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(WatcherApplication.class)
public class GrafanaTest {
    @Autowired
    GrafanaService grafanaService;

    @Test
    public void getDashboardTest() {
        String result = grafanaService.getDashboard("disk");
        System.out.println(result);
    }

    @Test
    public void createDashboardTest() {
        AppModel appModel = new AppModel();
        appModel.setGroup("telegraf");
        appModel.setName("disk");
        String result = grafanaService.crateDashboard(appModel);
        JSONObject jsonObject = JSON.parseObject(result);
        Assert.assertEquals("disk",jsonObject.getString("slug"));
        Assert.assertEquals("success",jsonObject.getString("status"));
        System.out.println(result);
    }


    @Test
    public void delteDashboardTest() {
        int result = grafanaService.deleteDashboard("disk");
        Assert.assertEquals(200,result);
    }
}
