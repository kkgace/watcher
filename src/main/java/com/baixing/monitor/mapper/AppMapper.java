package com.baixing.monitor.mapper;

import com.baixing.monitor.model.AppModel;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by kofee on 16/7/23.
 */
@Mapper
public interface AppMapper {

    String INSERT_A_APP = "insert into application ( org_id, app_name, app_host, app_desc, charger ,mail, created, updated ) " +
            "values (#{orgId}, #{name}, #{host}, #{appDesc}, #{charger}, #{mail}, #{created}, #{updated}) ";


    String GET_APP_BY_NAME_ORG = "select id, org_id AS orgId, app_name AS name, app_host AS host, app_desc AS appDesc, charger ,mail, created, updated " +
            " from application where app_name=#{name} and org_id=#{orgId}";


    String GET_ALL_APP = "select id, org_id AS orgId, app_name AS name, app_host AS host, app_desc AS appDesc, charger ,mail, created, updated " +
            " from application limit 100";

    //添加一个应用
    @Insert(INSERT_A_APP)
    int addApp(AppModel appModel);

    //通过name和orgId查询一个应用
    @Select(GET_APP_BY_NAME_ORG)
    AppModel getAppByNameOrgId(@Param("name") String name, @Param("orgId") int orgId);

    //获取所有应用
    @Select(GET_ALL_APP)
    List<AppModel> getAllApp();

    //TODO 修改一个应用


}
