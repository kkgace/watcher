package com.baixing.monitor.service;

import com.baixing.monitor.model.AppModel;

import java.util.List;

/**
 * Created by kofee on 16/7/23.
 */
public interface AppService {

    //注册一个应用
    int registerApp(AppModel appModel);

    //更新app图的信息
    int updateApp(AppModel appModel);


    List<AppModel> getAllApp();
}
