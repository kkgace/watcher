package com.baixing.monitor.service.dao;

import com.baixing.monitor.Application;
import com.baixing.monitor.mapper.AppMapper;
import com.baixing.monitor.model.AppModel;
import com.baixing.monitor.service.AppService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by kofee on 16/7/23.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
public class AppTest {

    @Autowired
    private AppMapper appMapper;

    @Autowired
    private AppService appService;


    @Test
    public void addApp() {
        AppModel appModel = new AppModel();

        appModel.setOrgId(1);
        appModel.setName("测试应用");
        appModel.setAppDesc("这是一个测试应用");
        appModel.setCharger("kefei@baixing.com");
        appModel.setMail("kefei@baixing.com");
        appModel.setHost("storm01:8080,storm02:8080");

        appMapper.addApp(appModel);
    }

    @Test
    public void createApp() {
        AppModel appModel = new AppModel();

        appModel.setOrgId(1);
        appModel.setName("测试应用1");
        appModel.setAppDesc("这是一个测试应用");
        appModel.setCharger("kefei@baixing.com");
        appModel.setMail("kefei@baixing.com");
        appModel.setHost("storm01:8080,storm02:8080");

        appService.registerApp(appModel);
    }

    @Test
    public void getApp() {
        AppModel appModel = appMapper.getAppByNameOrgId("测试应用", 1);
        System.out.println(appModel);
    }

    @Test
    public void getAllApp() {
        List<AppModel> appList = appMapper.getAllApp();

        System.out.println(appList.size());
    }
}
