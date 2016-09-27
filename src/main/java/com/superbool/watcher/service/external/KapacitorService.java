package com.superbool.watcher.service.external;

import com.superbool.watcher.util.FileUtil;
import com.superbool.watcher.util.HttpUtil;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by kofee on 16/8/29.
 */
@Service
public class KapacitorService {

    @Value(value = "${kapacitor.url}")
    private String kapacitorUrl;

    private static String KAPACITOR_API = "/kapacitor/v1/tasks";

    private static String ALERT_TICK = FileUtil.getFileToStr("alertTick");


    //创建一个task
    public String addTask(String database, String measurement, String tag,
                          String field, String condition, String email) {
        /**
         var data = stream
         |from()
         .measurement('%s')
         .groupBy('%s')
         data
         |alert()
         .id('%s {{ index .Tags "%s" }}')
         .message('{{ .ID }} is {{ .Level }} %s:{{ index .Fields "%s" }}/30s')
         .crit(lambda: "%s" %s)
         .stateChangesOnly()
         .slack()
         .email('%s')
         */
        String emails = Joiner.on("','").skipNulls().join(Splitter.on(",").split(email)).toString();

        String alert = String.format(ALERT_TICK, measurement, tag,
                measurement, tag, field, field, field, condition, emails);

        JsonObject param = new JsonObject();

        JsonObject dbrp = new JsonObject();
        dbrp.addProperty("db", database);
        dbrp.addProperty("rp", "default");
        JsonArray dbrps = new JsonArray();
        dbrps.add(dbrp);

        param.addProperty("type", "stream");
        param.add("dbrps", dbrps);
        param.addProperty("script", alert);
        param.addProperty("status", "enabled");
        param.addProperty("id", field);

        String result = HttpUtil.post(kapacitorUrl + KAPACITOR_API, param.toString());

        return result;
    }


    //更新一个task  先删除 再创建
    public String updateTask() {
        return null;
    }

    //删除一个task
    public int deleteTask(String field) {
        return HttpUtil.delete(kapacitorUrl + KAPACITOR_API + "/" + field);
    }


    //查询任务,如果没有查询所有的任务
    public String getTask(String field) {
        String result;
        if (Strings.isNullOrEmpty(field)) {
            result = HttpUtil.get(kapacitorUrl + KAPACITOR_API);
        } else {
            result = HttpUtil.get(kapacitorUrl + KAPACITOR_API + "/" + field);
        }
        return result;
    }

    /**
     * 创建一个task
     * 1 Create a new task with ID TASK_ID.
     * <p>
     * POST /kapacitor/v1/tasks/
     * {
     * "id" : "TASK_ID",
     * "type" : "stream",
     * "dbrps": [{"db": "DATABASE_NAME", "rp" : "RP_NAME"}],
     * "script": "stream\n    |from()\n        .measurement('cpu')\n"
     * }
     * ----------------------------------------
     * 修改task
     * 2 Modify only the dbrps of the task.
     * <p>
     * PATCH /kapacitor/v1/tasks/TASK_ID
     * {
     * "dbrps": [{"db": "NEW_DATABASE_NAME", "rp" : "NEW_RP_NAME"}]
     * }
     * ----------------------------------------
     * 允许task
     * 3 Enable an existing task.
     * <p>
     * PATCH /kapacitor/v1/tasks/TASK_ID
     * {
     * "status" : "enabled",
     * }
     * ----------------------------------------
     * 关闭task
     * 4 Disable an existing task.
     * <p>
     * PATCH /kapacitor/v1/tasks/TASK_ID
     * {
     * "status" : "disabled",
     * }
     * ----------------------------------------
     * 创建一个task并且允许
     * 5 Define a new task that is enabled on creation.
     * <p>
     * POST /kapacitor/v1/tasks/TASK_ID
     * {
     * "type" : "stream",
     * "dbrps" : [{"db": "DATABASE_NAME", "rp" : "RP_NAME"}],
     * "script" : "stream\n    |from()\n        .measurement('cpu')\n",
     * "status" : "enabled"
     * }
     * ----------------------------------------
     * 获取一个task的信息
     * 6 Get information about a task using defaults.
     * <p>
     * GET /kapacitor/v1/tasks/TASK_ID
     * <p>
     * ----------------------------------------
     * 删除task
     * 7 To delete a task make a DELETE request to the /kapacitor/v1/tasks/TASK_ID endpoint
     * <p>
     * DELETE /kapacitor/v1/tasks/TASK_ID
     * <p>
     * ----------------------------------------
     * 获取所有的task
     * 8 Get all tasks.
     * <p>
     * GET /kapacitor/v1/tasks
     * <p>
     * ----------------------------------------
     * 调试task
     * 9 GET /kapacitor/v1/ping
     */

}
