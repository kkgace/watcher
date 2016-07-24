package com.baixing.monitor.service.impl;

import com.baixing.monitor.mapper.AppMapper;
import com.baixing.monitor.model.AppModel;
import com.baixing.monitor.service.AppService;
import com.baixing.monitor.service.DashService;
import com.baixing.monitor.service.TaskService;
import com.baixing.monitor.util.BXMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by kofee on 16/7/24.
 */
@Service
public class AppServiceImpl implements AppService {

    @Autowired
    private AppMapper appMapper;

    @Autowired
    private DashService dashService;

    @Override
    public int registerApp(AppModel appModel) {
        long begin = System.currentTimeMillis();

        try {
            //先在app表中将应用的信息插入
            int result = appMapper.addApp(appModel);

            if (result == 1) {
                //再生成应用的一个图表
                result = dashService.addDashboard(appModel);

                if (result == 1) {
                    //todo更新
                    TaskService.addServerMap(appModel);
                }
            }

            BXMonitor.recordOne("注册应用", System.currentTimeMillis() - begin);
            return result;
        } catch (DuplicateKeyException e) {
            BXMonitor.recordOne("注册应用失败", System.currentTimeMillis() - begin);
            return -1;
        }
    }

    @Override
    public int updateApp(AppModel appModel) {
        return 0;
    }

    @Override
    public List<AppModel> getAllApp() {
        long begin = System.currentTimeMillis();
        List<AppModel> appModelList = appMapper.getAllApp();
        BXMonitor.recordOne("get_all_app", System.currentTimeMillis() - begin);
        return appModelList;
    }


}
