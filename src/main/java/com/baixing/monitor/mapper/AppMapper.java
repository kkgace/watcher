package com.baixing.monitor.mapper;

import com.baixing.monitor.model.AppModel;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by kofee on 16/7/23.
 */
@Mapper
public interface AppMapper {

    String INSERT_A_APP = "insert into application ( org_id, name, descr, duty ,mail, server, created, updated ) " +
            "values (#{orgId}, #{name}, #{descr}, #{duty}, #{mail}, #{server}, #{created}, #{updated}) ";


    String GET_APP_BY_NAME_ORG = "select id, org_id as orgId, name, descr, duty ,mail, server, created, updated " +
            " from application where name=#{name} and org_id=#{orgId}";


    String GET_ALL_APP = "select id, org_id as orgId, name, descr, duty ,mail, server, created, updated " +
            " from application limit 50";

    @Insert(INSERT_A_APP)
    int addApp(AppModel appModel);

    @Select(GET_APP_BY_NAME_ORG)
    AppModel getAppByNameOrgId(@Param("name") String name, @Param("orgId") int orgId);


    @Select(GET_ALL_APP)
    List<AppModel> getAllApp();


}
