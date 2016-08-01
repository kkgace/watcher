package com.baixing.monitor.mapper;

import com.baixing.monitor.model.DashModel;
import org.apache.ibatis.annotations.*;

/**
 * Created by kofee on 16/7/21.
 */
@Mapper
public interface DashMapper {

    String GET_DASHBOARD_BY_ID = "select id, version, slug, title, data, org_id ," +
            "created, updated, updated_by as updatedBy, created_by as createdBy, gnet_id as gnetId," +
            "plugin_id as pluginId from dashboard where org_id=#{orgId} and title=#{title}";

    String UPDATE_DATA_BY_ID = "UPDATE dashboard SET data=#{data} WHERE id =#{id}";

    String INSERT_A_DASHBOARD = "insert into dashboard (version, slug, title, data, org_id ," +
            "created, updated, updated_by , created_by, gnet_id ," +
            "plugin_id) values (#{version}, #{slug}, #{title}, #{data}, #{orgId}," +
            "#{created}, #{updated}, #{updatedBy}, #{createdBy}, #{gnetId}, #{pluginId}) ";

    // 通过应用的标题和orgId查找
    @Select(GET_DASHBOARD_BY_ID)
    DashModel getDashboardById(@Param("orgId") long orgId, @Param("title") String title);

    //todo 更新人和时间
    @Update(UPDATE_DATA_BY_ID)
    int updateDataById(@Param("data") String data, @Param("id") long id);

    //插入一条数据
    @Insert(INSERT_A_DASHBOARD)
    int addDashboard(DashModel dashModel);
}
