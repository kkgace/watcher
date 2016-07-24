package com.baixing.monitor.dao;

import com.baixing.monitor.model.AppModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kofee on 16/7/21.
 */
public class ServerDao {

    public static List<AppModel> getAllServerList() {
        List<AppModel> appModels = new ArrayList<AppModel>();
        AppModel appModel = new AppModel();
        appModel.setName("yaoguang-push");
        List<String> ipList = new ArrayList<String>();
        ipList.add("storm02");

        appModels.add(appModel);
        return appModels;
    }

}
