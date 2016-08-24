package com.baixing.monitor.service.impl;

import com.baixing.monitor.mapper.AppMapper;
import com.baixing.monitor.model.AppModel;
import com.baixing.monitor.service.AppService;
import com.baixing.monitor.service.DashService;
import com.baixing.monitor.service.PullDataService;
import com.baixing.monitor.service.TaskService;
import com.baixing.monitor.util.BxMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by kofee on 16/7/24.
 */
@Service
public class AppServiceImpl implements AppService {
    private static final Logger logger = LoggerFactory.getLogger(AppServiceImpl.class);

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
            System.out.println(result);

            if (result == 1) {
                //再生成应用的一个图表
                result = dashService.addDashboard(appModel);

                if (result == 1) {
                    //todo 更新
                    PullDataService.addApp(appModel);
                }
            }

            BxMonitor.recordOne("注册应用", System.currentTimeMillis() - begin);
            return result;
        } catch (DuplicateKeyException e) {
            BxMonitor.recordOne("注册应用重复", System.currentTimeMillis() - begin);
            logger.warn("注册应用重复 app={}", appModel, e);
            return 1;
        } catch (Exception e) {
            BxMonitor.recordOne("注册应用失败", System.currentTimeMillis() - begin);
            logger.error("注册应用失败 app={}", appModel, e);
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
        BxMonitor.recordOne("get_all_app", System.currentTimeMillis() - begin);
        return appModelList;
    }


}
