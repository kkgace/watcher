package com.baixing.monitor.mapper;

import com.baixing.monitor.model.AppModel;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * Created by kofee on 16/7/23.
 */
@Mapper
public interface AppMapper {

    String INSERT_A_APP = "insert into application ( organization, `group`, name, host, style, mode, `describe`, contact, email, token, created, updated ) " +
            "values (#{organization}, #{group}, #{name}, #{host}, #{style}, #{mode}, #{describe}, #{contact}, #{email}, #{token}, #{created}, #{updated}) ";

    String GET_APP_BY_NAME_GROUP = "select * from application where name=#{name} and `group`=#{group}";

    String GET_ALL_APP = "select * from application limit 100";

    String GET_BY_TOKEN = "select * from application where token=#{token}";

    String GET_BY_STYLE = "select * from application where style=#{style}";

    String UPDATE_TOKEN = "update application set token=#{token}, updated=#{updated} where name=#{name} and `group`=#{group}";

    //添加一个应用
    @Insert(INSERT_A_APP)
    int addApp(AppModel appModel);

    //通过name和orgId查询一个应用
    @Select(GET_APP_BY_NAME_GROUP)
    AppModel getAppByNameGroup(@Param("name") String name, @Param("group") String group);

    //获取所有应用
    @Select(GET_ALL_APP)
    List<AppModel> getAllApp();

    //获取一个应用的token
    @Select(GET_BY_TOKEN)
    AppModel getByToken(@Param("token") String token);

    @Select(GET_BY_STYLE)
    List<AppModel> getByStyle(@Param("style") String style);

    @Update(UPDATE_TOKEN)
    int updateToken(@Param("token") String token, @Param("name") String name,
                    @Param("group") String group, @Param("updated") Date updated);

}
