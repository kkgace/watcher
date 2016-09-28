package com.superbool.watcher.service.external;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.superbool.watcher.model.AppModel;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Created by kofee on 16/8/29.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest()
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
        JsonObject jsonObject = new Gson().fromJson(result,JsonObject.class);
        Assert.assertEquals("disk",jsonObject.get("slug").getAsString());
        Assert.assertEquals("success",jsonObject.get("status").getAsString());
        System.out.println(result);
    }


    @Test
    public void delteDashboardTest() {
        int result = grafanaService.deleteDashboard("disk");
        Assert.assertEquals(200,result);
    }
}
