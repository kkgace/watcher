package com.baixing.monitor.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baixing.monitor.util.FileUtil;
import com.baixing.monitor.util.HttpUtil;
import com.google.common.base.Strings;

/**
 * Created by kofee on 16/8/19.
 */
public class AlertConf {

    private static String KAPACITOR_API = "http://storm01:9093/kapacitor/v1/tasks";

    private static String taskAlertScript = FileUtil.getFileToStr("taskAlertScript");


    //创建一个task
    public static String createTask(String measurement, String tag, String field,
                                    String condition, String email) {
        /**
         var data = stream
         |from()
         .measurement('%s')
         .groupBy('%s')
         data
         |alert()
         .id('%s {{ index .Tags "%s" }} ')
         .message('{{ .ID }} is {{ .Level }} %s:{{ index .Fields "%s" }}')
         .crit(lambda: "%s" %s)
         .email('%s')
         .talk()
         */
        String alert = String.format(taskAlertScript, measurement, tag,
                measurement, tag, field, field, field, condition, email);


        JSONObject param = new JSONObject();


        JSONObject dbrp = new JSONObject();
        dbrp.put("db", "yaoguang");
        dbrp.put("rp", "default");
        JSONArray dbrps = new JSONArray();
        dbrps.add(dbrp);

        param.put("type", "stream");
        param.put("dbrps", dbrps);
        param.put("script", alert);
        param.put("status", "enabled");
        param.put("id", field);

        String result = HttpUtil.post(KAPACITOR_API, param.toJSONString());

        return result;
    }

    //更新一个task  先删除 再创建
    public static String updateTask() {
        return null;
    }

    //删除一个task
    public static int deleteTask(String field) {
        return HttpUtil.delete(KAPACITOR_API + "/" + field);
    }


    //查询任务,如果没有查询所有的任务
    public static String getTask(String field) {
        String result;
        if (Strings.isNullOrEmpty(field)) {
            result = HttpUtil.get(KAPACITOR_API);
        } else {
            result = HttpUtil.get(KAPACITOR_API + "/" + field);
        }
        return result;
    }

    public static void main(String[] args) {
        String database = "yaoguang";

        String measurement = "yaoguang_push";
        String tag = "host";
        String field = "test";
        String condition = ">10";
        String email = "hellokofee@163.com,kefei@baixing.com,chengyichao@baixing.com";


        System.out.println(createTask(measurement, tag, "dao_get_company_by_id_count", "< 10", email));

        //System.out.println(deleteTask("dao_get_company_by_id_count"));

        //System.out.println(getTask("dao_get_company_by_id_count"));

        //System.out.println(getTask(""));

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
