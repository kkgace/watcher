package com.superbool.watcher.service.dao;

import com.superbool.watcher.WatcherApplication;
import com.superbool.watcher.mapper.AppMapper;
import com.superbool.watcher.model.AppModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by kofee on 16/7/23.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(WatcherApplication.class)
public class AppTest {

    @Autowired
    private AppMapper appMapper;

    @Test
    public void addApp() {
        AppModel appModel = new AppModel();

        appModel.setOrganization(1);
        appModel.setGroup("test");
        appModel.setName("测试应用");
        appModel.setDescribe("这是一个测试应用");
        appModel.setContact("柯飞");
        appModel.setEmail("kefei@baixing.com");
        appModel.setHost("storm01:8080,storm02:8080");
        appModel.setStyle("gather");
        appModel.setMode("pull");
        String token = UUID.randomUUID().toString();
        appModel.setToken(token);
        System.out.println(appModel);
        appMapper.addApp(appModel);
    }


    @Test
    public void getApp() {
        AppModel appModel = appMapper.getAppByNameGroup("测试应用", "test");
        System.out.println(appModel);
    }

    @Test
    public void getAllApp() {
        List<AppModel> appList = appMapper.getAllApp();

        System.out.println(appList);
    }

    @Test
    public void getByToken() {
        AppModel appModel = appMapper.getByToken("dc9a8d21-bec8-42c3-b53a-40be62b4e3ae");
        System.out.println(appModel);
    }

    @Test
    public void updateToken() {
        int result = appMapper.updateToken("123", "测试应用", "test", new Date());
        System.out.println(result);
    }
}
